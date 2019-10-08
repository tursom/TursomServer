package cn.tursom.database

import java.lang.reflect.Field
import kotlin.reflect.KProperty
import cn.tursom.database.SqlUtils.fieldName

class FieldSelector : HashSet<String>() {
	//	operator fun String.unaryPlus() = add(this.sqlStr)
	operator fun Field.unaryPlus(): Field {
		add(fieldName)
		return this
	}
	
	operator fun KProperty<*>.unaryPlus(): KProperty<*> {
		add(fieldName)
		return this
	}
	
	infix operator fun Field.plus(field: Field): Field {
		add(field.fieldName)
		return this
	}
	
	infix operator fun Field.plus(field: KProperty<*>): Field {
		add(field.fieldName)
		return this
	}
	
	infix operator fun KProperty<*>.plus(field: KProperty<*>): KProperty<*> {
		add(field.fieldName)
		return this
	}
	
	infix operator fun KProperty<*>.plus(field: Field): KProperty<*> {
		add(field.fieldName)
		return this
	}
}