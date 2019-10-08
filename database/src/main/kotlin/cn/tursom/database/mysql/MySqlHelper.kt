package cn.tursom.database.mysql

import cn.tursom.database.*
import cn.tursom.database.SqlUtils.fieldStr
import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.SqlUtils.appendField
import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.SqlUtils.valueStr
import cn.tursom.database.SqlUtils.isSqlField
import cn.tursom.database.SqlUtils.fieldValue
import cn.tursom.database.SqlUtils.ignored
import cn.tursom.database.annotation.FieldType
import cn.tursom.database.annotation.ForeignKey
import cn.tursom.database.annotation.Getter
import cn.tursom.database.annotation.TextLength
import cn.tursom.database.clauses.Clause
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLSyntaxErrorException

/**
 * MySQLHelper，SQLite辅助使用类
 * 实现创建表格、查询、插入和更新功能
 */
@Suppress("SqlNoDataSourceInspection", "SqlDialectInspection")
class MySqlHelper(
	@Suppress("MemberVisibilityCanBePrivate") val connection: Connection,
	base: String? = null
) : SqlHelper {
	override val closed: Boolean get() = connection.isClosed

	@Suppress("MemberVisibilityCanBePrivate")
	var basename: String? = null
		get() = synchronized(this) {
			return field
		}
		set(value) {
			value?.let { base ->
				synchronized(this) {
					val statement = connection.createStatement()
					statement.executeQuery("USE $base")
					field = base
					statement.closeOnCompletion()
				}
			}
		}

	init {
		connection.autoCommit = false
		basename = base
	}

	constructor(url: String, user: String, password: String, base: String? = null)
		: this(
		DriverManager.getConnection(
			"jdbc:mysql://$url?characterEncoding=utf-8&serverTimezone=UTC",
			user,
			password
		),
		base
	)

	/*
	 * 创建表格
	 * table: 表格名
	 * keys: 属性列表
	 */
	override fun createTable(table: String, keys: Iterable<String>) {
		val statement = connection.createStatement()
		statement.executeUpdate("CREATE TABLE if not exists `$table` ( ${keys.fieldStr()} ) ENGINE = InnoDB DEFAULT CHARSET=utf8;")
		connection.commit()
	}

	/**
	 * 根据提供的class对象自动化创建表格
	 */
	override fun createTable(fields: Class<*>) {
		createTable(fields.tableName, fields, "InnoDB", "utf8")
	}

	/**
	 * 根据提供的class对象自动化创建表格
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun createTable(table: String, keys: Class<*>, engine: String = "InnoDB", charset: String = "utf8") {
		val statement = connection.createStatement()
		statement.executeUpdate(createTableStr(table, keys, engine, charset))
		connection.commit()
	}

	/**
	 * 删除表格
	 */
	override fun deleteTable(table: String) {
		val statement = connection.createStatement()
		statement.executeUpdate("DROP TABLE if exists $table ENGINE = InnoDB DEFAULT CHARSET=utf8;")
		connection.commit()
	}

	/**
	 * 删除表格
	 */
	override fun dropTable(table: String) {
		deleteTable(table)
	}

	/**
	 * 查询
	 * @param adapter 用于保存查询结果的数据类，由SQLAdapter继承而来
	 * @param fields 查询字段
	 * @param where 指定从一个表或多个表中获取数据的条件,Pair左边为字段名，右边为限定的值
	 * @param maxCount 最大查询数量
	 */
	override fun <T : Any> select(
		adapter: SqlAdapter<T>,
		fields: Iterable<String>?,
		where: Clause,
		order: Field?,
		reverse: Boolean,
		maxCount: Int?
	): SqlAdapter<T> = select(
		adapter = adapter,
		fields = fields?.fieldStr() ?: "*",
		where = where.sqlStr,
		order = order?.fieldName,
		reverse = reverse,
		maxCount = maxCount
	)


	override fun <T : Any> select(
		adapter: SqlAdapter<T>,
		fields: String,
		where: String?,
		order: String?,
		reverse: Boolean,
		maxCount: Int?
	): SqlAdapter<T> {
		val sql = "SELECT $fields FROM ${adapter.clazz.tableName
		}${if (where != null) " WHERE $where" else ""
		}${if (order != null) " ORDER BY $order ${if (reverse) "DESC" else "ASC"}" else ""
		}${if (maxCount != null) " limit $maxCount" else ""
		};"
		val statement = connection.createStatement()
		adapter.adapt(statement.executeQuery(sql))
		statement.closeOnCompletion()
		return adapter
	}

	override fun update(
		table: String,
		set: String,
		where: String
	): Int {
		val sql = "UPDATE $table SET $set${if (where.isNotEmpty()) " WHERE $where" else ""};"
		val statement = connection.createStatement()
		return try {
			statement.executeUpdate(sql)
		} finally {
			commit()
			statement.closeOnCompletion()
		}
	}

	/**
	 * 更新数据库数据
	 * @param value 用来存储数据的bean对象
	 * @param where SQL语句的一部分，用来限定查找的条件。每一条String储存一个条件
	 */
	override fun update(value: Any, where: Clause): Int {
		val sb = StringBuilder()
		value.javaClass.declaredFields.forEach {
			it.isAccessible = true
			sb.append("${it.fieldName}=${it.get(value)?.fieldValue ?: return@forEach},")
		}
		if (sb.isNotEmpty())
			sb.delete(sb.length - 1, sb.length)
		return update(value.tableName, sb.toString(), where.sqlStr)
	}

	private fun insert(connection: Connection, sql: String, table: Class<*>): Int {
		val statement = connection.createStatement()
		return try {
			statement.executeUpdate(sql)
		} catch (e: SQLSyntaxErrorException) {
			if (e.message == "Table '$basename.${table.tableName}' doesn't exist") {
				createTable(table)
				statement.executeUpdate(sql)
			} else {
				throw e
			}
		} finally {
			connection.commit()
			statement.closeOnCompletion()
		}
	}

	override fun insert(table: String, fields: String, values: String): Int {
		val sql = "INSERT INTO $table ($fields) VALUES $values;"
		val statement = connection.createStatement()
		return try {
			statement.executeUpdate(sql)
		} finally {
			connection.commit()
			statement.closeOnCompletion()
		}
	}

	override fun insert(value: Any): Int {
		val clazz = value.javaClass
		val fields = clazz.declaredFields
		val sql = "INSERT INTO ${value.tableName} (${fields.fieldStr()}) VALUES (${clazz.valueStr(value) ?: return 0});"
		return insert(connection, sql, clazz)
	}

	override fun insert(valueList: Iterable<*>): Int {
		val first = valueList.firstOrNull() ?: return 0
		val clazz = first.javaClass
		val fields = ArrayList<SqlFieldData>()
		clazz.declaredFields.forEach { field ->
			val getter = field.getAnnotation(Getter::class.java)?.let { clazz.getDeclaredMethod(field.name) }
			fields.add(SqlFieldData(field, getter))
		}
		val values = fields.valueStr(valueList) ?: return 0
		if (values.isEmpty()) return 0
		val sql = "INSERT INTO ${first.tableName} (${first.javaClass.declaredFields.fieldStr()}) VALUES $values;"
		return insert(connection, sql, clazz)
	}

	override fun delete(table: String, where: String?): Int {
		val sql = "DELETE FROM `$table`${if (where != null) " WHERE $where" else ""};"
		val statement = connection.createStatement()
		return try {
			statement.executeUpdate(sql)
		} finally {
			connection.commit()
			statement.closeOnCompletion()
		}
	}

	override fun delete(table: String, where: Clause?) =
		delete(table, where?.sqlStr)


	override fun close() {
		connection.close()
	}

	override fun commit() {
		connection.commit()
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
				if (it.ignored) return@forEach
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
				else -> if (type.isSqlField) {
					type.getAnnotation(FieldType::class.java)?.name ?: type.name.split('.').last()
				} else {
					null
				}
			}
	}
}