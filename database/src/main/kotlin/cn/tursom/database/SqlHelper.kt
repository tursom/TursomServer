package cn.tursom.database

import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.clauses.Clause
import cn.tursom.database.clauses.ClauseMaker
import java.io.Closeable
import java.lang.reflect.Field

/**
 * MySQLHelper，SQLite辅助使用类
 * 实现创建表格、查询、插入和更新功能
 */

interface SqlHelper : Closeable {
	val closed: Boolean

	/**
	 * 创建表格
	 * @param table: 表格名
	 * @param keys: 属性列表
	 */
	fun createTable(table: String, keys: Iterable<String>)

	/**
	 * 根据提供的class对象自动化创建表格
	 * 但是有诸多缺陷，所以不是很建议使用
	 */
	fun createTable(fields: Class<*>)

	/**
	 * 删除表格
	 */
	fun deleteTable(table: String)

	/**
	 * 删除表格
	 */
	fun dropTable(table: String)

	/**
	 * 查询
	 * @param adapter 用于保存查询结果的数据类，由SQLAdapter继承而来
	 * @param fields 查询字段
	 * @param where 指定从一个表或多个表中获取数据的条件,Pair左边为字段名，右边为限定的值
	 * @param maxCount 最大查询数量
	 */
	fun <T : Any> select(
		adapter: SqlAdapter<T>,
		fields: Iterable<String>? = null,
		where: Clause,
		order: Field? = null,
		reverse: Boolean = false,
		maxCount: Int? = null
	): SqlAdapter<T>

	/**
	 * 用于支持灵活查询
	 */
	fun <T : Any> select(
		adapter: SqlAdapter<T>,
		fields: String = "*",
		where: String? = null,
		order: String? = null,
		reverse: Boolean = false,
		maxCount: Int? = null
	): SqlAdapter<T>

	/**
	 * 插入
	 * @param value 值
	 */
	fun insert(value: Any): Int

	fun insert(valueList: Iterable<*>): Int

	fun insert(table: String, fields: String, values: String): Int

	fun update(table: String, set: String, where: String = ""): Int

	fun update(value: Any, where: Clause): Int

	fun delete(table: String, where: String? = null): Int

	fun delete(table: String, where: Clause?): Int

	fun commit()
}

/**
 * 用于支持灵活查询
 */
inline fun <reified T : Any> SqlHelper.select(
	fields: String = "*",
	where: String? = null,
	order: String? = null,
	reverse: Boolean = false,
	maxCount: Int? = null
): SqlAdapter<T> = select(SqlAdapter(T::class.java), fields, where, order, reverse, maxCount)

inline fun <reified T : Any> SqlHelper.select(
	fields: Iterable<String> = listOf("*"),
	where: Clause,
	order: Field? = null,
	reverse: Boolean = false,
	maxCount: Int? = null
): SqlAdapter<T> = select(SqlAdapter(T::class.java), fields, where, order, reverse, maxCount)

fun <T : Any> SqlHelper.select(
	adapter: SqlAdapter<T>,
	maker: SqlSelector<T>.() -> Unit
): SqlAdapter<T> {
	val selector = SqlSelector(this, adapter)
	selector.maker()
	return selector.select()
}

inline infix fun <reified T : Any> SqlHelper.select(
	noinline maker: SqlSelector<T>.() -> Unit
): SqlAdapter<T> = select(SqlAdapter(T::class.java), maker)

fun <T : Any> SqlHelper.select(
	clazz: Class<T>,
	fields: Iterable<String> = listOf("*"),
	where: Clause,
	order: Field? = null,
	reverse: Boolean = false,
	maxCount: Int? = null
): SqlAdapter<T> = select(SqlAdapter(clazz), fields, where, order, reverse, maxCount)

/**
 * 用于支持灵活查询
 */
fun <T : Any> SqlHelper.select(
	clazz: Class<T>,
	fields: String = "*",
	where: String? = null,
	order: String? = null,
	reverse: Boolean = false,
	maxCount: Int? = null
): SqlAdapter<T> = select(SqlAdapter(clazz), fields, where, order, reverse, maxCount)

fun SqlHelper.delete(
	clazz: Class<*>,
	where: String? = null
) = delete(clazz.tableName, where)

fun SqlHelper.delete(
	table: String,
	where: ClauseMaker.() -> Clause
) = delete(table, ClauseMaker.where())

infix fun SqlHelper.delete(helper: SqlDeleter.() -> Unit): Int {
	val deleter = SqlDeleter(this)
	deleter.helper()
	return deleter.delete()
}

infix fun SqlHelper.update(updater: SqlUpdater.() -> Unit): Int {
	val sqlUpdater = SqlUpdater(this)
	sqlUpdater.updater()
	return sqlUpdater.update()
}
