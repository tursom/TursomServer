package cn.tursom.utils.bytebuffer

/**
 * support type:
 * Byte Char Short Int Long Float Double String
 * ByteArray CharArray ShortArray IntArray LongArray FloatArray DoubleArray Array<*>
 * Collection(serialize) Map(serialize)
 *
 * will support
 */

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.isStatic
import cn.tursom.core.unsafe

class UnsupportedException : Exception()

fun AdvanceByteBuffer.serialize(obj: Any) {
	when (obj) {
		is Char -> put(obj)
		is Short -> put(obj)
		is Int -> put(obj)
		is Long -> put(obj)
		is Float -> put(obj)
		is Double -> put(obj)
		is String -> serialize(obj.toByteArray())
		is ByteArray -> {
			put(obj.size)
			put(obj)
		}
		is CharArray -> {
			put(obj.size)
			obj.forEach {
				put(it)
			}
		}
		is ShortArray -> {
			put(obj.size)
			obj.forEach { put(it) }
		}
		is IntArray -> {
			put(obj.size)
			obj.forEach { put(it) }
		}
		is LongArray -> {
			put(obj.size)
			obj.forEach { put(it) }
		}
		is FloatArray -> {
			put(obj.size)
			obj.forEach { put(it) }
		}
		is DoubleArray -> {
			put(obj.size)
			obj.forEach { put(it) }
		}
		is Array<*> -> {
			put(obj.size)
			obj.forEach { serialize(it ?: return@forEach) }
		}
		is Collection<*> -> {
			put(obj.size)
			obj.forEach { serialize(it ?: return@forEach) }
		}
		is Map<*, *> -> {
			put(obj.size)
			obj.forEach { (k, v) ->
				serialize(k ?: return@forEach)
				serialize(v ?: return@forEach)
			}
		}
		else -> {
			obj.javaClass.declaredFields.forEach {
				if (it.isStatic()) return@forEach
				it.isAccessible = true
				serialize(it.get(obj) ?: return@forEach)
			}
		}
	}
}

@Suppress("UNCHECKED_CAST")
fun <T> AdvanceByteBuffer.unSerialize(clazz: Class<T>): T {
	when {
		clazz == Byte::class.java -> {
			return get() as T
		}
		clazz == Char::class.java -> {
			return getChar() as T
		}
		clazz == Short::class.java -> {
			return getShort() as T
		}
		clazz == Int::class.java -> {
			return getInt() as T
		}
		clazz == Long::class.java -> {
			return getLong() as T
		}
		clazz == Float::class.java -> {
			return getFloat() as T
		}
		clazz == Double::class.java -> {
			return getDouble() as T
		}
		
		clazz == java.lang.Byte::class.java -> {
			return get() as T
		}
		clazz == java.lang.Character::class.java -> {
			return getChar() as T
		}
		clazz == java.lang.Short::class.java -> {
			return getShort() as T
		}
		clazz == java.lang.Integer::class.java -> {
			return getInt() as T
		}
		clazz == java.lang.Long::class.java -> {
			return getLong() as T
		}
		clazz == java.lang.Float::class.java -> {
			return getFloat() as T
		}
		clazz == java.lang.Double::class.java -> {
			return getDouble() as T
		}
		
		clazz == String::class.java -> {
			return getString(getInt()) as T
		}
		
		clazz == ByteArray::class.java -> {
			val array = ByteArray(getInt())
			repeat(array.size) {
				array[it] = get()
			}
			return array as T
		}
		clazz == CharArray::class.java -> {
			val array = CharArray(getInt())
			repeat(array.size) {
				array[it] = getChar()
			}
			return array as T
		}
		clazz == ShortArray::class.java -> {
			val array = ShortArray(getInt())
			repeat(array.size) {
				array[it] = getShort()
			}
			return array as T
		}
		clazz == IntArray::class.java -> {
			val array = IntArray(getInt())
			repeat(array.size) {
				array[it] = getInt()
			}
			return array as T
		}
		clazz == LongArray::class.java -> {
			val array = LongArray(getInt())
			repeat(array.size) {
				array[it] = getLong()
			}
			return array as T
		}
		clazz == FloatArray::class.java -> {
			val array = FloatArray(getInt())
			repeat(array.size) {
				array[it] = getFloat()
			}
			return array as T
		}
		clazz == DoubleArray::class.java -> {
			val array = DoubleArray(getInt())
			repeat(array.size) {
				array[it] = getDouble()
			}
			return array as T
		}
		clazz.isArray -> {
			val componentType = clazz.componentType
			val newArray = java.lang.reflect.Array.newInstance(componentType, getInt())
			repeat(java.lang.reflect.Array.getLength(newArray)) {
				java.lang.reflect.Array.set(newArray, it, unSerialize(componentType))
			}
			return newArray as T
		}
		else -> {
			val instance = try {
				clazz.newInstance()
			} catch (e: Throwable) {
				unsafe.allocateInstance(clazz)
			}
			clazz.declaredFields.forEach {
				if (it.isStatic()) return@forEach
				it.isAccessible = true
				it.set(instance, unSerialize(it.type))
			}
			return instance as T
		}
	}
}

inline fun <reified T : Any> AdvanceByteBuffer.unSerialize(): T {
	return unSerialize(T::class.java)
}
