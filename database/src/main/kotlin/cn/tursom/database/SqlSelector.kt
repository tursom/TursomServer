package cn.tursom.database

import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.SqlUtils.sqlStr
import cn.tursom.database.clauses.Clause
import cn.tursom.database.clauses.ClauseMaker
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class SqlSelector<T : Any>(
	private val helper: SqlHelper,
	private val adapter: SqlAdapter<T>
) {
	private var where: String? = null
	private var fields: FieldSelector = FieldSelector()
	private var order: String? = null
	private var reverse: Boolean = false
	private var maxCount: Int? = null
		set(value) {
			when {
				value ?: 1 <= 0 -> return
				else -> field = value
			}
		}
	
	fun fields(selector: FieldSelector.() -> Unit) {
		fields.selector()
	}
	
	fun where(clause: ClauseMaker.() -> Clause) {
		where = ClauseMaker.clause().sqlStr
	}
	
	fun select() =
		helper.select(adapter, fields.fieldName ?: "*", where, order, reverse, maxCount)
	
	infix fun orderBy(field: String) {
		order = field.sqlStr
	}
	
	infix fun orderBy(field: Field) {
		order = field.fieldName
	}
	
	infix fun orderBy(field: KProperty<*>) {
		order = field.fieldName
	}
	
	infix fun KProperty<*>.reverse(reverse: Boolean) {
		order = fieldName
		this@SqlSelector.reverse = reverse
	}
	
	infix fun Field.reverse(reverse: Boolean) {
		order = fieldName
		this@SqlSelector.reverse = reverse
	}
	
	infix fun reverse(reverse: Boolean) {
		this.reverse = reverse
	}
	
	infix fun limit(maxCount: Int?) {
		this.maxCount = maxCount
	}
	
	infix fun Field.limit(maxCount: Int?) {
		order = fieldName
		this@SqlSelector.maxCount = maxCount
	}
	
	infix fun KProperty<*>.limit(maxCount: Int?) {
		order = fieldName
		this@SqlSelector.maxCount = maxCount
	}
	
	infix fun Any.limit(maxCount: Int?) {
		this@SqlSelector.maxCount = maxCount
	}
}