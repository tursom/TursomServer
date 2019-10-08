package cn.tursom.database.clauses

import cn.tursom.database.SqlUtils.sqlStr
import cn.tursom.database.SqlUtils.fieldName
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class LikeClause(field: String, value: String) : Clause {
	constructor(field: Field, value: String) : this(field.fieldName, value)
	constructor(field: KProperty<*>, value: String) : this(field.javaField!!, value)
	
	constructor(field: String, value: LikeWildcard.() -> String)
		: this(field, LikeWildcard.value())
	
	constructor(field: Field, value: LikeWildcard.() -> String)
		: this(field, LikeWildcard.value())
	
	constructor(field: KProperty<*>, value: LikeWildcard.() -> String)
		: this(field, LikeWildcard.value())
	
	override val sqlStr = "$field LIKE '${value.sqlStr}'"
	override fun toString() = sqlStr
	
	object LikeWildcard {
		val single: String = "_"
		val many: String = "%"
		fun charList(charList: String) = "[$charList]"
		fun unCharList(charList: String) = "[!$charList]"
		infix operator fun String.rangeTo(target: String) = "$this-$target"
	}
}