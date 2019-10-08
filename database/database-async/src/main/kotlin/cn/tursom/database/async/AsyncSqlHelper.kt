package cn.tursom.database.async

import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.clauses.Clause
import cn.tursom.database.clauses.ClauseMaker
import cn.tursom.database.SqlUtils.tableName
import io.vertx.ext.sql.SQLConnection
import java.lang.reflect.Field

interface AsyncSqlHelper {
	val connection: SQLConnection

	suspend fun doSql(sql: String): Int

	suspend fun createTable(fields: Class<*>)

	suspend fun deleteTable(table: String)
	suspend fun deleteTable(table: Class<*>) = deleteTable(table.tableName)
	suspend fun dropTable(table: String)
	suspend fun dropTable(table: Class<*>) = dropTable(table.tableName)

	suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		fields: String = "*",
		where: String? = null,
		order: String? = null,
		reverse: Boolean = false,
		maxCount: Int? = null,
		table: String = adapter.clazz.tableName
	): AsyncSqlAdapter<T>

	suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		fields: Iterable<String>,
		where: Clause? = null,
		order: Field? = null,
		reverse: Boolean = false,
		maxCount: Int? = null,
		table: String = adapter.clazz.tableName
	): AsyncSqlAdapter<T>

	suspend fun insert(value: Any): Int

	suspend fun insert(valueList: Iterable<*>): Int

	suspend fun replace(table: String, value: Any): Int

	suspend fun replace(table: String, valueList: Iterable<*>): Int

	suspend fun update(table: String, set: String, where: String? = null): Int

	suspend fun update(value: Any, where: Clause): Int

	suspend fun delete(table: String, where: String? = null): Int

	suspend fun delete(table: String, where: Clause?): Int

	suspend fun commit()

	suspend fun close()


	suspend fun replace(value: Any): Int = replace(value.javaClass.tableName, value)

	suspend fun replace(valueList: Iterable<*>): Int {
		return replace((valueList.first() ?: return 0).tableName, valueList)
	}

	suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		maker: AsyncSqlSelector<T>.() -> Unit
	): AsyncSqlAdapter<T> {
		val selector = AsyncSqlSelector(this, adapter)
		selector.maker()
		return selector.select()
	}

	suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		where: Clause,
		fields: String = "*",
		order: Field? = null,
		reverse: Boolean = false,
		maxCount: Int? = null,
		table: String = adapter.clazz.tableName
	): AsyncSqlAdapter<T> = select(adapter, fields, where.sqlStr, order?.fieldName, reverse, maxCount, table)

	suspend fun <T : Any> select(
		clazz: Class<T>,
		where: Clause,
		fields: String = "*",
		order: Field? = null,
		reverse: Boolean = false,
		maxCount: Int? = null,
		table: String = clazz.tableName
	): AsyncSqlAdapter<T> = select(AsyncSqlAdapter(clazz), fields, where.sqlStr, order?.fieldName, reverse, maxCount, table)

	suspend fun <T : Any> select(
		clazz: Class<T>,
		fields: Iterable<String> = listOf("*"),
		where: Clause,
		order: Field? = null,
		reverse: Boolean = false,
		maxCount: Int? = null,
		table: String = clazz.tableName
	): AsyncSqlAdapter<T> = select(AsyncSqlAdapter(clazz), fields, where, order, reverse, maxCount, table)

	suspend fun delete(
		clazz: Class<*>,
		where: String? = null
	) = delete(clazz.tableName, where)

	suspend fun delete(
		table: String,
		where: ClauseMaker.() -> Clause
	) = delete(table, ClauseMaker.where())

	suspend infix fun delete(helper: AsyncSqlDeleter.() -> Unit): Int {
		val deleter = AsyncSqlDeleter(this)
		deleter.helper()
		return deleter.delete()
	}

	suspend infix fun update(updater: AsyncSqlUpdater.() -> Unit): Int {
		val sqlUpdater = AsyncSqlUpdater(this)
		sqlUpdater.updater()
		return sqlUpdater.update()
	}
}

suspend inline fun <reified T : Any> AsyncSqlHelper.select(
	fields: Iterable<String> = listOf("*"),
	where: Clause? = null,
	order: Field? = null,
	reverse: Boolean = false,
	maxCount: Int? = null
): AsyncSqlAdapter<T> = select(AsyncSqlAdapter(T::class.java), fields, where, order, reverse, maxCount)

suspend inline infix fun <reified T : Any> AsyncSqlHelper.select(
	noinline maker: AsyncSqlSelector<T>.() -> Unit
): AsyncSqlAdapter<T> = select(AsyncSqlAdapter(T::class.java), maker)
