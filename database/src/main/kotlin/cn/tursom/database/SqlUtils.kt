package cn.tursom.database

import cn.tursom.database.annotation.*
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.contains
import kotlin.collections.forEach
import kotlin.collections.iterator
import kotlin.collections.last
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

object SqlUtils {
	inline operator fun <T> invoke(action: SqlUtils.() -> T): T = this.action()

	val Field.ignored get() = getAnnotation(Ignore::class.java) != null
	val Field.constructor get() = getAnnotation(Constructor::class.java)?.constructor

	val Class<*>.dataField: List<Field>
		get() {
			val fields = declaredFields
			val fieldList = ArrayList<Field>()
			fields.forEach {
				if (!it.ignored) fieldList.add(it)
			}
			return fieldList
		}
	val Field.fieldName: String
		get() = getAnnotation(FieldName::class.java)?.name ?: name

	val KProperty<*>.fieldName: String
		get() = javaField!!.fieldName

	val Any.fieldValue: String
		get() = when (this) {
			is SqlField<*> -> javaClass.getAnnotation(StringField::class.java)?.let {
				sqlValue.sqlStr
			} ?: sqlValue
			is String -> sqlStr
			else -> toString()
		}

	val Iterable<String>.fieldName: String?
		get() {
			val stringBuffer = StringBuffer()
			forEach {
				if (it.isNotEmpty())
					stringBuffer.append("`$it`,")
			}
			return if (stringBuffer.isNotEmpty()) {
				stringBuffer.delete(stringBuffer.length - 1, stringBuffer.length)
				stringBuffer.toString()
			} else {
				null
			}
		}

	val Class<*>.isSqlField
		get() = interfaces.contains(SqlField::class.java)

	val Any.tableName: String
		get() = javaClass.tableName

	val Class<*>.tableName: String
		get() = (getAnnotation<TableName>()?.name ?: name.split('.').last()).toLowerCase()


	val KClass<*>.tableName: String
		get() = java.tableName

	inline fun <reified T : Annotation> Field.getAnnotation(): T? = getAnnotation(T::class.java)

	inline fun <reified T : Annotation> Class<*>.getAnnotation(): T? = getAnnotation(T::class.java)

	fun Array<out Field>.fieldStr(): String {
		val fields = StringBuilder()
		forEach field@{ field ->
			if (field.ignored) return@field
			field.isAccessible = true
			fields.append("${field.fieldName},")
		}
		fields.deleteCharAt(fields.length - 1)
		return fields.toString()
	}

	fun Iterable<String>.fieldStr(): String {
		val stringBuffer = StringBuffer()
		forEach {
			if (it.isNotEmpty())
				stringBuffer.append("$it,")
		}
		stringBuffer.delete(stringBuffer.length - 1, stringBuffer.length)
		return stringBuffer.toString()
	}

	fun Class<*>.valueStr(value: Any): String? {
		val values = StringBuilder()
		declaredFields.forEach field@{ field ->
			field.isAccessible = true
			values.append(field.getAnnotation(Getter::class.java)?.let {
				getDeclaredMethod(field.name).invoke(null) as String
			} ?: field.get(value)?.fieldValue)
			values.append(',')
		}
		if (values.isNotEmpty()) {
			values.deleteCharAt(values.length - 1)
		} else {
			return null
		}
		return values.toString()
	}

	fun Array<out Field>.valueStr(value: Any): String? {
		val clazz = value.javaClass
		val values = StringBuilder()
		forEach field@{ field ->
			if (field.ignored) return@field
			field.isAccessible = true
			val getter = field.getAnnotation(Getter::class.java)
			if (getter != null) {
				val method = clazz.getDeclaredMethod(getter.getter)
				method.isAccessible = true
				try {
					values.append(method.invoke(value))
				} catch (e: InvocationTargetException) {
					throw e.targetException
				}
				values.append(',')
			} else {
				values.append(field.get(value)?.fieldValue)
				values.append(',')
			}
		}
		if (values.isNotEmpty()) {
			values.deleteCharAt(values.length - 1)
		} else {
			return null
		}
		return values.toString()
	}

	fun Iterable<*>.valueStr(sqlFieldMap: Array<out Field>): String? {
		val values = StringBuilder()
		forEach { value ->
			value ?: return@forEach
			values.append("(${sqlFieldMap.valueStr(value) ?: return@forEach}),")
		}
		if (values.isNotEmpty()) {
			values.deleteCharAt(values.length - 1)
		} else {
			return null
		}
		return values.toString()
	}

	fun Iterable<SqlFieldData>.valueStr(value: Iterable<*>): String? {
		val values = StringBuilder()
		forEach field@{ (field, _) ->
			field.isAccessible = true
		}
		value.forEach { obj ->
			values.append('(')
			val iterator = iterator()
			if (!iterator.hasNext()) return@forEach
			iterator.next().let { (field, getter) ->
				values.append(getter?.invoke(obj) ?: field.get(obj)?.fieldValue)
			}
			for ((field, getter) in iterator) {
				values.append(',')
				values.append(getter?.invoke(obj) ?: field.get(obj)?.fieldValue)
			}
			values.append("),")
		}
		if (values.isNotEmpty()) {
			values.deleteCharAt(values.length - 1)
		} else {
			return null
		}
		return values.toString()
	}

	fun List<Pair<String, String>>.fieldStr(): Pair<String, String> {
		val first = StringBuilder()
		val second = StringBuilder()
		forEach { (f, s) ->
			first.append("$f,")
			second.append("$s,")
		}
		if (first.isNotEmpty()) first.deleteCharAt(first.length - 1)
		if (second.isNotEmpty()) second.deleteCharAt(second.length - 1)
		return first.toString() to second.toString()
	}

	fun StringBuilder.appendField(
		field: Field,
		fieldType: Field.() -> String?,
		foreignKeyList: AbstractCollection<Pair<String, String>>,
		autoIncrement: String = "AUTO_INCREMENT",
		primaryKey: Field.() -> Unit
	) {
		if (field.ignored) return
		val fieldName = field.fieldName
		append("`$fieldName` ${field.fieldType() ?: return}")
		field.annotations.forEach annotations@{ annotation ->
			append(" ${when (annotation) {
				is NotNull -> "NOT NULL"
				is AutoIncrement -> autoIncrement
				is Unique -> "UNIQUE"
				is Default -> "DEFAULT ${annotation.default}"
				is Check -> "CHECK(${field.fieldName}${annotation.func})"
				is ExtraAttribute -> annotation.attributes
				is ForeignKey -> {
					foreignKeyList.add(fieldName to if (annotation.target.isNotEmpty()) annotation.target else fieldName)
					return@annotations
				}
				is PrimaryKey -> {
					field.primaryKey()
					return@annotations
				}
				else -> return@annotations
			}}")
		}
		append(',')
	}

	val String.sqlStr
		get() = "'${replace("'", "''")}'"
}