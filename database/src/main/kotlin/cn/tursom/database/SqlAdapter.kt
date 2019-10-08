package cn.tursom.database

import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.annotation.Constructor
import cn.tursom.database.annotation.NotNull
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import kotlin.collections.forEach

open class SqlAdapter<T : Any>(
	@Suppress("MemberVisibilityCanBePrivate") val clazz: Class<T>,
	private val adapter: (SqlAdapter<T>.(
		resultSet: ResultSet,
		fieldList: List<FieldData>
	) -> Unit)? = null
) : ArrayList<T>() {
	open fun adapt(resultSet: ResultSet) {
		clear() //清空已储存的数据
		try {
			val fieldList = ArrayList<FieldData>()
			if (resultSet.next()) {
				clazz.declaredFields.forEach {
					try {
						val fieldName = it.fieldName
						resultSet.getObject(fieldName)
						it.isAccessible = true
						val constructorAnnotation = it.getAnnotation(Constructor::class.java)
						val constructor = try {
							clazz.getDeclaredMethod(constructorAnnotation.constructor, Any::class.java)
						} catch (e: Exception) {
							null
						}
						val superConstructor = try {
							clazz.getDeclaredMethod(constructorAnnotation.constructor, ResultSet::class.java)
						} catch (e: Exception) {
							null
						}
						constructor?.isAccessible = true
						superConstructor?.isAccessible = true
						fieldList.add(FieldData(it, fieldName, it.type, superConstructor, constructor))
					} catch (e: SQLException) {
					}
				}
				(adapter ?: SqlAdapter<T>::adaptOnce)(resultSet, fieldList)
			}
			// 遍历ResultSet
			while (resultSet.next()) {
				(adapter ?: SqlAdapter<T>::adaptOnce)(resultSet, fieldList)
			}
		} catch (e: SQLException) {
			// ignored
		} catch (e: IllegalStateException) {
			// ignored
		}
	}
	
	@Suppress("UNCHECKED_CAST")
	open fun adaptOnce(resultSet: ResultSet, fieldList: List<FieldData>) {
		//绕过构造函数获取变量0
		val bean = unsafe.allocateInstance(clazz) as T
		fieldList.forEach { (
			                    field,
			                    fieldName,
			                    beanType,
			                    superAdapter,
			                    adapter
		                    ) ->
			try {
				if (superAdapter != null) {
					//如果你有能力直接从ResultSet里面取出数据,那就随君便
					val value = try {
						superAdapter.invoke(bean, resultSet)
					} catch (e: InvocationTargetException) {
						throw e.targetException
					}
					field.set(bean, value)
				} else {
					resultSet.getObject(fieldName)?.let { value ->
						//让我们把数据喂进去
						field.set(bean, handleCast(bean, field, beanType, value, adapter))
					}
				}
			} catch (e: SQLException) {
				e.printStackTrace()
				return@forEach
			}
		}
		add(bean)
	}
	
	private fun handleCast(
		bean: T,
		field: Field,
		beanType: Class<*>,
		value: Any,
		adapter: Method?
	): Any? {
		val dbType = value.javaClass // 这里是获取数据库字段的类型
		return if (adapter != null) {
			try {
				adapter.invoke(bean, value)
			} catch (e: InvocationTargetException) {
				throw e.targetException
			}
		} else if (beanType == java.lang.Float::class.java) {
			if (dbType == java.lang.Double::class.java) {
				(value as Double).toFloat()
			} else {
				//检查是否可以为空
				if (field.getAnnotation(NotNull::class.java) != null) {
					value.toString().toFloat()
				} else {
					value.toString().toFloatOrNull()
				}
			}
		} else if (beanType == java.lang.String::class.java && dbType != java.lang.String::class.java) {
			value.toString()
		} else if (beanType == java.lang.Boolean::class.java) {
			if (field.getAnnotation(NotNull::class.java) != null) {
				value.toString().toBoolean()
			} else {
				try {
					value.toString().toBoolean()
				} catch (e: Exception) {
					null
				}
			}
		} else {
			value
		}
	}
	
	data class FieldData(
		val field: Field,
		val fieldName: String,
		val beanType: Class<*>,
		val superAdapter: Method?,
		val adapter: Method?
	)
	
	companion object {
		//利用Unsafe绕过构造函数获取变量
		private val unsafe by lazy {
			val field = Unsafe::class.java.getDeclaredField("theUnsafe")
			//允许通过反射设置属性的值
			field.isAccessible = true
			field.get(null) as Unsafe
		}
	}
}