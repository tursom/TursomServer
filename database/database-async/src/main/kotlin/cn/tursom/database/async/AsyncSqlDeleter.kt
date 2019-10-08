package cn.tursom.database.async

import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.clauses.Clause
import cn.tursom.database.clauses.ClauseMaker
import kotlin.reflect.KClass


class AsyncSqlDeleter(
	private val helper: AsyncSqlHelper,
	private var table: String? = null,
	private var where: Clause? = null
) {
	infix fun Class<*>.where(where: ClauseMaker.() -> Clause) {
		table = tableName
		this@AsyncSqlDeleter.where = ClauseMaker.where()
	}
	
	infix fun KClass<*>.where(where: ClauseMaker.() -> Clause) {
		table = tableName
		this@AsyncSqlDeleter.where = ClauseMaker.where()
	}
	
	suspend fun delete() = helper.delete(table!!, where)
}