package cn.tursom.database.clauses

import cn.tursom.database.SqlUtils.fieldName
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class InfixExpressionClause(field: String, value: String, expression: String) : Clause {
	constructor(field: Field, value: String, expression: String) : this(field.fieldName, value, expression)
	constructor(field: KProperty<*>, value: String, expression: String) : this(field.javaField!!, value, expression)
	
	override val sqlStr = "$field$expression$value"
	override fun toString() = sqlStr
}