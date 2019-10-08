package cn.tursom.database.async

import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.SqlUtils.sqlStr
import cn.tursom.database.clauses.Clause
import cn.tursom.database.clauses.ClauseMaker
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class AsyncSqlUpdater(val helper: AsyncSqlHelper) {
	constructor(helper: AsyncSqlHelper, table: String) : this(helper) {
		this.table = table
	}
	
	private lateinit var table: String
	private val set = HashMap<String, String>()
	private var where: String? = null
	
	infix fun table(table: String) {
		this.table = table
	}
	
	infix fun table(clazz: Class<*>) {
		table = clazz.tableName
	}
	
	infix fun table(clazz: KClass<*>) {
		table = clazz.tableName
	}
	
	infix fun Field.setTo(value: String) {
		set[fieldName] = value.sqlStr
	}
	
	infix fun Field.setTo(value: Any) {
		set[fieldName] = value.toString()
	}
	
	infix fun KProperty<*>.setTo(value: String) {
		set[fieldName] = value.sqlStr
	}
	
	infix fun KProperty<*>.setTo(value: Any) {
		set[fieldName] = value.toString()
	}
	
	infix operator fun Field.plus(value: Any) = +"$fieldName+$value"
	infix operator fun Field.minus(value: Any) = +"$fieldName-$value"
	infix operator fun Field.times(value: Any) = +"$fieldName*$value"
	infix operator fun Field.div(value: Any) = +"$fieldName/$value"
	infix operator fun Field.rem(value: Any) = +"$fieldName%$value"
	
	infix operator fun KProperty<*>.plus(value: Any) = +"$fieldName+$value"
	infix operator fun KProperty<*>.minus(value: Any) = +"$fieldName-$value"
	infix operator fun KProperty<*>.times(value: Any) = +"$fieldName*$value"
	infix operator fun KProperty<*>.div(value: Any) = +"$fieldName/$value"
	infix operator fun KProperty<*>.rem(value: Any) = +"$fieldName%$value"
	
	infix fun where(clause: ClauseMaker.() -> Clause) {
		where = ClauseMaker.clause().sqlStr
	}
	
	operator fun String.unaryPlus() = StringUnit(this)
	
	class StringUnit(private val str: String) {
		operator fun not() = str
		override fun toString() = str
	}
	
	suspend fun update(): Int {
		val set = StringBuilder()
		this.set.forEach { (field, value) ->
			set.append("$field=$value,")
		}
		if (set.isNotEmpty()) {
			set.delete(set.length - 1, set.length)
		}
		return helper.update(table, set.toString(), where ?: "")
	}
}