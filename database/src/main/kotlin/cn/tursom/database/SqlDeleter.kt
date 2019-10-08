package cn.tursom.database

import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.clauses.Clause
import cn.tursom.database.clauses.ClauseMaker
import kotlin.reflect.KClass

class SqlDeleter(
	private val helper: SqlHelper,
	private var table: String? = null,
	private var where: Clause? = null
) {
	infix fun Class<*>.where(where: ClauseMaker.() -> Clause) {
		table = tableName
		this@SqlDeleter.where = ClauseMaker.where()
	}
	
	infix fun KClass<*>.where(where: ClauseMaker.() -> Clause) {
		table = tableName
		this@SqlDeleter.where = ClauseMaker.where()
	}
	
	fun delete() = helper.delete(table!!, where)
	
}