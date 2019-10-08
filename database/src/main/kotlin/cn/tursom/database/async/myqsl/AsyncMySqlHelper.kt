package cn.tursom.database.async.myqsl

import cn.tursom.database.*
import cn.tursom.database.annotation.FieldType
import cn.tursom.database.annotation.ForeignKey
import cn.tursom.database.annotation.Getter
import cn.tursom.database.annotation.TextLength
import cn.tursom.database.async.AsyncSqlAdapter
import cn.tursom.database.async.AsyncSqlHelper
import cn.tursom.database.async.vertx
import cn.tursom.database.clauses.Clause
import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.SqlUtils.fieldStr
import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.SqlUtils.valueStr
import cn.tursom.database.SqlUtils.fieldValue
import cn.tursom.database.SqlUtils.ignored
import cn.tursom.database.SqlUtils.appendField
import cn.tursom.database.SqlUtils.getAnnotation
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Field
import java.sql.SQLSyntaxErrorException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AsyncMySqlHelper(url: String, user: String, password: String, base: String? = null) : AsyncSqlHelper {
	override suspend fun replace(table: String, value: Any): Int {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override suspend fun replace(table: String, valueList: Iterable<*>): Int {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override val connection = runBlocking {
		val config = JsonObject()
		config.put("url", "jdbc:mysql://$url?characterEncoding=utf-8&serverTimezone=UTC")
		config.put("_driver class", "org.sqlite.JDBC")
		config.put("user", user)
		config.put("password", password)
		suspendCoroutine<SQLConnection> { cont ->
			JDBCClient.createShared(vertx, config, "$url@$user").getConnection {
				if (!it.failed()) {
					cont.resume(it.result())
				} else {
					cont.resumeWithException(it.cause())
				}
			}
		}
	}

	@Suppress("MemberVisibilityCanBePrivate")
	var basename: String? = base
		set(value) {
			runBlocking {
				value?.let { base ->
					doSql("USE $base")
					field = base
				}
			}
		}

	override suspend fun doSql(sql: String): Int = suspendCoroutine { cont ->
		connection.execute(sql) {
			if (it.succeeded()) {
				cont.resume(1)
			} else {
				cont.resumeWithException(it.cause())
			}
		}
	}

	override suspend fun commit() = suspendCoroutine<Unit> { cont ->
		connection.commit {
			if (it.succeeded()) {
				cont.resume(Unit)
			} else {
				cont.resumeWithException(it.cause())
			}
		}
	}

	/**
	 * 根据提供的class对象自动化创建表格
	 */
	override suspend fun createTable(fields: Class<*>) {
		createTable(fields.tableName, fields, "InnoDB", "utf8")
	}

	/**
	 * 根据提供的class对象自动化创建表格
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	suspend fun createTable(table: String, keys: Class<*>, engine: String = "InnoDB", charset: String = "utf8") {
		doSql(createTableStr(table, keys, engine, charset))
	}

	/**
	 * 删除表格
	 */
	override suspend fun deleteTable(table: String) {
		doSql("DROP TABLE if exists $table ENGINE = InnoDB DEFAULT CHARSET=utf8;")
	}

	/**
	 * 删除表格
	 */
	override suspend fun dropTable(table: String) {
		deleteTable(table)
	}

	/**
	 * 查询
	 * @param adapter 用于保存查询结果的数据类，由AsyncSqlAdapter继承而来
	 * @param fields 查询字段
	 * @param where 指定从一个表或多个表中获取数据的条件,Pair左边为字段名，右边为限定的值
	 * @param maxCount 最大查询数量
	 */
	override suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		fields: Iterable<String>,
		where: Clause?,
		order: Field?,
		reverse: Boolean,
		maxCount: Int?,
		table: String
	): AsyncSqlAdapter<T> {
		val sql = "SELECT ${fields.fieldStr()} FROM ${table
		}${if (where != null) " WHERE ${where.sqlStr}" else ""
		}${if (order != null) " ORDER BY ${order.fieldName} ${if (reverse) "DESC" else "ASC"}" else ""
		}${if (maxCount != null) " limit $maxCount" else ""
		};"
		return suspendCoroutine { cont ->
			connection.query(sql) {
				if (it.succeeded()) {
					adapter.adapt(it.result())
					cont.resume(adapter)
				} else {
					cont.resumeWithException(it.cause())
				}
			}
		}
	}

	override suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		fields: String,
		where: String?,
		order: String?,
		reverse: Boolean,
		maxCount: Int?,
		table: String
	): AsyncSqlAdapter<T> {
		val sql = "SELECT $fields FROM ${adapter.clazz.tableName
		}${if (where != null) " WHERE $where" else ""
		}${if (order != null) " ORDER BY $order ${if (reverse) "DESC" else "ASC"}" else ""
		}${if (maxCount != null) " limit $maxCount" else ""
		};"
		return suspendCoroutine { cont ->
			connection.query(sql) {
				if (it.succeeded()) {
					adapter.adapt(it.result())
					cont.resume(adapter)
				} else {
					cont.resumeWithException(it.cause())
				}
			}
		}
	}

	/**
	 * 更新数据库数据
	 * @param value 用来存储数据的bean对象
	 * @param where SQL语句的一部分，用来限定查找的条件。每一条String储存一个条件
	 */
	override suspend fun update(value: Any, where: Clause): Int {
		val sb = StringBuilder()
		value.javaClass.declaredFields.forEach {
			it.isAccessible = true
			sb.append("${it.fieldName}=${it.get(value)?.fieldValue ?: return@forEach},")
		}
		if (sb.isNotEmpty())
			sb.delete(sb.length - 1, sb.length)
		val whereStr = where.sqlStr
		val sql = "UPDATE ${value.tableName} SET $sb${if (whereStr.isNotEmpty()) " WHERE ${whereStr}" else ""};"
		return doSql(sql)
	}

	override suspend fun update(table: String, set: String, where: String?): Int {
		val sql = "UPDATE $table SET $set${if (where != null && where.isNotEmpty()) " WHERE $where" else ""};"
		return doSql(sql)
	}

	private suspend fun insert(sql: String, table: Class<*>): Int {
		return try {
			doSql(sql)
		} catch (e: SQLSyntaxErrorException) {
			createTable(table)
			doSql(sql)
		}
	}

	override suspend fun insert(value: Any): Int {
		val clazz = value.javaClass
		val fields = clazz.declaredFields
		val sql = "INSERT INTO ${value.tableName} (${fields.fieldStr()}) VALUES (${clazz.valueStr(value) ?: return 0});"
		return insert(sql, clazz)
	}

	override suspend fun insert(valueList: Iterable<*>): Int {
		val first = valueList.firstOrNull() ?: return 0
		val clazz = first.javaClass
		val fields = ArrayList<SqlFieldData>()
		clazz.declaredFields.forEach { field ->
			if (field.ignored) return@forEach
			val getter = field.getAnnotation(Getter::class.java)?.let { clazz.getDeclaredMethod(it.getter) }
			fields.add(SqlFieldData(field, getter))
		}
		val values = fields.valueStr(valueList) ?: return 0
		if (values.isEmpty()) return 0
		val sql = "INSERT INTO ${first.tableName} (${first.javaClass.declaredFields.fieldStr()}) VALUES $values;"
		return insert(sql, clazz)
	}

	override suspend fun delete(table: String, where: String?): Int {
		val sql = "DELETE FROM `$table`${if (where != null) " WHERE $where" else ""};"
		return doSql(sql)

	}

	override suspend fun delete(table: String, where: Clause?) =
		delete(table, where?.sqlStr)

	override suspend fun close() {
		connection.close()
	}

	companion object {
		init {
			Class.forName("com.mysql.cj.jdbc.Driver")
		}

		fun createTableStr(keys: Class<*>, engine: String = "InnoDB", charset: String = "utf8"): String =
			createTableStr(keys.tableName, keys, engine, charset)

		fun createTableStr(table: String, keys: Class<*>, engine: String = "InnoDB", charset: String = "utf8"): String {
			val fieldSet = keys.declaredFields
			val valueStrBuilder = StringBuilder()
			valueStrBuilder.append("CREATE TABLE IF NOT EXISTS `$table`(")
			val primaryKeySet = ArrayList<String>()

			val foreignKey = keys.getAnnotation(ForeignKey::class.java)?.let {
				if (it.target.isNotEmpty()) it.target else null
			}
			val foreignKeyList = ArrayList<Pair<String, String>>()

			fieldSet.forEach {
				valueStrBuilder.appendField(it, { it.fieldType }, foreignKeyList) {
					primaryKeySet.add(fieldName)
				}
			}

			if (primaryKeySet.isNotEmpty()) {
				valueStrBuilder.append("PRIMARY KEY(${primaryKeySet.fieldName}),")
			}

			if (foreignKey != null && foreignKeyList.isEmpty()) {
				val (source, target) = foreignKeyList.fieldStr()
				valueStrBuilder.append("FOREIGN KEY ($source) REFERENCES $foreignKey ($target),")
			}
			valueStrBuilder.deleteCharAt(valueStrBuilder.length - 1)

			valueStrBuilder.append(")ENGINE=$engine DEFAULT CHARSET=$charset;")
			return valueStrBuilder.toString()
		}

		private val Field.fieldType: String?
			get() = getAnnotation(FieldType::class.java)?.name ?: when (type) {
				java.lang.Byte::class.java -> "TINYINT"
				java.lang.Character::class.java -> "TINYINT"
				java.lang.Short::class.java -> "SMALLINT"
				java.lang.Integer::class.java -> "INT"
				java.lang.Long::class.java -> "BIGINT"
				java.lang.Float::class.java -> "FLOAT"
				java.lang.Double::class.java -> "DOUBLE"

				Byte::class.java -> "TINYINT"
				Char::class.java -> "TINYINT"
				Short::class.java -> "SMALLINT"
				Int::class.java -> "INT"
				Long::class.java -> "BIGINT"
				Float::class.java -> "FLOAT"
				Double::class.java -> "Double"

				java.lang.String::class.java -> getAnnotation(TextLength::class.java)?.let { "CHAR(${it.length})" }
					?: "TEXT"
				else ->
					getAnnotation<FieldType>()?.name ?: type.getAnnotation<FieldType>()?.name ?: type.name.split('.').last()
			}
	}
}