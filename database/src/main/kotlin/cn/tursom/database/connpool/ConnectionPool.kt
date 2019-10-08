package cn.tursom.database.connpool

import cn.tursom.database.SqlAdapter
import cn.tursom.database.SqlHelper
import cn.tursom.database.clauses.Clause
import java.lang.reflect.Field

interface ConnectionPool<T : SqlHelper> : SqlHelper {
	fun getConnection(): T
	fun returnConnection(conn: T)

	override val closed: Boolean get() = getConnection().closed

	override fun createTable(table: String, keys: Iterable<String>) = use {
		it.createTable(table, keys)
	}


	override fun createTable(fields: Class<*>) = use {
		it.createTable(fields)
	}

	override fun deleteTable(table: String) = use {
		it.deleteTable(table)
	}

	override fun dropTable(table: String) = use {
		it.dropTable(table)
	}

	override fun <T : Any> select(
		adapter: SqlAdapter<T>,
		fields: Iterable<String>?,
		where: Clause, order: Field?,
		reverse: Boolean,
		maxCount: Int?
	): SqlAdapter<T> = use {
		it.select(adapter, fields, where, order, reverse, maxCount)
	}

	override fun <T : Any> select(
		adapter: SqlAdapter<T>,
		fields: String,
		where: String?,
		order: String?,
		reverse: Boolean,
		maxCount: Int?
	): SqlAdapter<T> = use {
		it.select(adapter, fields, where, order, reverse, maxCount)
	}

	override fun insert(value: Any): Int = use {
		it.insert(value)
	}

	override fun insert(valueList: Iterable<*>): Int = use {
		it.insert(valueList)
	}

	override fun insert(table: String, fields: String, values: String): Int = use {
		it.insert(table, fields, values)
	}

	override fun update(table: String, set: String, where: String): Int = use {
		it.update(table, set, where)
	}

	override fun update(value: Any, where: Clause): Int = use {
		it.update(value, where)
	}

	override fun delete(table: String, where: String?): Int = use {
		it.delete(table, where)
	}

	override fun delete(table: String, where: Clause?): Int = use {
		it.delete(table, where)
	}

	override fun commit() = use {
		it.commit()
	}

	override fun close() = use {
		it.close()
	}
}

inline fun <T : SqlHelper, T1> ConnectionPool<T>.use(action: (helper: T) -> T1): T1 {
	val helper = getConnection()
	val ret = action(helper)
	returnConnection(helper)
	return ret
}
