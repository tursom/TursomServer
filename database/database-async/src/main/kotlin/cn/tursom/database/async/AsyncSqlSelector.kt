package cn.tursom.database.async

import cn.tursom.database.*
import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.SqlUtils.sqlStr
import cn.tursom.database.clauses.Clause
import cn.tursom.database.clauses.ClauseMaker
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class AsyncSqlSelector<T : Any>(
	private val helper: AsyncSqlHelper,
	private val adapter: AsyncSqlAdapter<T>
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

	suspend infix fun fields(selector: suspend FieldSelector.() -> Unit) {
		fields.selector()
	}

	suspend infix fun where(clause: suspend ClauseMaker.() -> Clause) {
		where = ClauseMaker.clause().sqlStr
	}

	suspend fun select() =
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
		this@AsyncSqlSelector.reverse = reverse
	}

	infix fun Field.reverse(reverse: Boolean) {
		order = fieldName
		this@AsyncSqlSelector.reverse = reverse
	}

	infix fun reverse(reverse: Boolean) {
		this.reverse = reverse
	}

	infix fun limit(maxCount: Int?) {
		this.maxCount = maxCount
	}

	infix fun Field.limit(maxCount: Int?) {
		order = fieldName
		this@AsyncSqlSelector.maxCount = maxCount
	}

	infix fun KProperty<*>.limit(maxCount: Int?) {
		order = fieldName
		this@AsyncSqlSelector.maxCount = maxCount
	}

	infix fun Any.limit(maxCount: Int?) {
		this@AsyncSqlSelector.maxCount = maxCount
	}
}