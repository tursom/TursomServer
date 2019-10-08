package cn.tursom.database.connpool

import cn.tursom.database.SqlHelper
import cn.tursom.core.pool.LinkedPool

class SqlHelperPool<T : SqlHelper>(
	private val newConnection: () -> T
) : ConnectionPool<T> {
	private val pool = LinkedPool<T>()

	override fun getConnection(): T {
		var conn = pool.get()
		while (conn != null && conn.closed) conn = pool.get()
		return conn ?: newConnection()
	}

	override fun returnConnection(conn: T) {
		if (!conn.closed) {
			pool.put(conn)
		}
	}
}