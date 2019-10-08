package cn.tursom.database.clauses

import cn.tursom.database.SqlUtils.fieldName
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class GreaterEqualClause(field: String, value: String) : Clause {
	constructor(field: Field, value: String) : this(field.fieldName, value)
	constructor(field: KProperty<*>, value: String) : this(field.javaField!!, value)
	
	override val sqlStr = "$field>=$value"
	override fun toString() = sqlStr
}