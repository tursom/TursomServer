@file:Suppress("unused")

package cn.tursom.core

import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.io.*
import java.nio.ByteOrder

class WrongPushTypeException : Exception()

fun Char.toByteArray(): ByteArray {
	val array = ByteArray(2)
	array.put(this)
	return array
}

fun Short.toByteArray(): ByteArray {
	val array = ByteArray(2)
	array.put(this)
	return array
}

fun Int.toByteArray(): ByteArray {
	val array = ByteArray(4)
	array.put(this)
	return array
}

fun Long.toByteArray(): ByteArray {
	val array = ByteArray(8)
	array.put(this)
	return array
}

fun Float.toByteArray(): ByteArray {
	val array = ByteArray(4)
	array.put(this)
	return array
}

fun Double.toByteArray(): ByteArray {
	val array = ByteArray(8)
	array.put(this)
	return array
}

fun CharArray.toByteArray(): ByteArray {
	val newArray = ByteArray(size * 2)
	repeat(size) {
		newArray.put(this[it], it * 2)
	}
	return newArray
}

fun ShortArray.toByteArray(): ByteArray {
	val newArray = ByteArray(size * 2)
	repeat(size) {
		newArray.put(this[it], it * 2)
	}
	return newArray
}

fun IntArray.toByteArray(): ByteArray {
	val newArray = ByteArray(size * 4)
	repeat(size) {
		newArray.put(this[it], it * 4)
	}
	return newArray
}

fun LongArray.toByteArray(): ByteArray {
	val newArray = ByteArray(size * 8)
	repeat(size) {
		newArray.put(this[it], it * 8)
	}
	return newArray
}

fun FloatArray.toByteArray(): ByteArray {
	val newArray = ByteArray(size * 4)
	repeat(size) {
		newArray.put(this[it], it * 4)
	}
	return newArray
}

fun DoubleArray.toByteArray(): ByteArray {
	val newArray = ByteArray(size * 8)
	repeat(size) {
		newArray.put(this[it], it * 8)
	}
	return newArray
}

fun ByteArray.toChar(offset: Int = 0): Char {
	return if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		(this[offset].toInt() or (this[offset + 1].toInt() shl 8))
	} else {
		(this[offset + 1].toInt() or (this[offset].toInt() shl 8))
	}.toChar()
}

fun ByteArray.toShort(offset: Int = 0): Short {
	return if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		(this[offset].toInt() or (this[offset + 1].toInt() shl 8))
	} else {
		(this[offset + 1].toInt() or (this[offset].toInt() shl 8))
	}.toShort()
}

fun ByteArray.toInt(offset: Int = 0): Int {
	return if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		this[offset].toInt() and 0xff or (this[offset + 1].toInt() shl 8 and 0xff00) or
			(this[offset + 2].toInt() shl 16 and 0xff0000) or (this[offset + 3].toInt() shl 24 and 0xff000000.toInt())
	} else {
		this[offset + 3].toInt() and 0xff or (this[offset + 2].toInt() shl 8 and 0xff00) or
			(this[offset + 1].toInt() shl 16 and 0xff0000) or (this[offset].toInt() shl 24 and 0xff000000.toInt())
	}
}

fun ByteArray.toLong(offset: Int = 0): Long {
	return if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		toInt(offset).toLong() or (toInt(offset + 4).toLong().shl(32) and 0xffff_ffffL.inv())
	} else {
		toInt(offset + 4).toLong() or (toInt(offset).toLong().shl(32) and 0xffff_ffffL.inv())
	}.toLong()
}

fun ByteArray.toFloat(offset: Int = 0): Float {
	return Float.fromBits(toInt(offset))
}

fun ByteArray.toDouble(offset: Int = 0): Double {
	return Double.fromBits(toLong(offset))
}

fun ByteArray.toCharArray(offset: Int, size: Int): CharArray {
	if (offset + size * 2 > this.size) throw IndexOutOfBoundsException()
	val newArray = CharArray(size)
	repeat(newArray.size) {
		newArray[it] = toChar(it * 2)
	}
	return newArray
}

fun ByteArray.toShortArray(offset: Int, size: Int): ShortArray {
	if (offset + size * 2 > this.size) throw IndexOutOfBoundsException()
	val newArray = ShortArray(size)
	repeat(newArray.size) {
		newArray[it] = toShort(it * 2)
	}
	return newArray
}

fun ByteArray.toIntArray(offset: Int, size: Int): IntArray {
	if (offset + size * 4 > this.size) throw IndexOutOfBoundsException()
	val newArray = IntArray(size)
	repeat(newArray.size) {
		newArray[it] = toInt(it * 4)
	}
	return newArray
}

fun ByteArray.toLongArray(offset: Int, size: Int): LongArray {
	if (offset + size * 8 > this.size) throw IndexOutOfBoundsException()
	val newArray = LongArray(size)
	repeat(newArray.size) {
		newArray[it] = toLong(it * 8)
	}
	return newArray
}

fun ByteArray.toFloatArray(offset: Int, size: Int): FloatArray {
	if (offset + size * 4 > this.size) throw IndexOutOfBoundsException()
	val newArray = FloatArray(size)
	repeat(newArray.size) {
		newArray[it] = toFloat(it * 4)
	}
	return newArray
}

fun ByteArray.toDoubleArray(offset: Int, size: Int): DoubleArray {
	if (offset + size * 8 > this.size) throw IndexOutOfBoundsException()
	val newArray = DoubleArray(size)
	repeat(newArray.size) {
		newArray[it] = toDouble(it * 8)
	}
	return newArray
}

fun Short.hton(): Short = ntoh()
fun Short.ntoh(): Short {
	return if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		this
	} else {
		val value = toInt()
		(value shr 8 or (value shl 8)).toShort()
	}
}


fun Int.hton(): Int = ntoh()
fun Int.ntoh(): Int {
	return if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		this
	} else {
		shr(24) or (shr(16) and 0xff00) or (shr(8) and 0xff0000) or (this and 0xff)
	}
}


fun Long.hton(): Long = ntoh()
fun Long.ntoh(): Long {
	return if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
		this
	} else {
		shr(56) or (shr(48) and 0xff00) or (shr(40) and 0xff0000)
		or(shr(32) and 0xff000000) or (shl(32) and (0xff shl 32))
		or(shl(40) and (0xff shl 40)) or (shl(48) and (0xff shl 48)) or shl(56)
	}
}

fun ByteArray.put(char: Char, offset: Int = 0) {
	val value = char.toInt()
	when (ByteOrder.nativeOrder()) {
		ByteOrder.BIG_ENDIAN -> {
			this[offset] = value.toByte()
			this[offset + 1] = (value shr 8).toByte()
		}
		ByteOrder.LITTLE_ENDIAN -> {
			this[offset + 1] = value.toByte()
			this[offset] = (value shr 8).toByte()
		}
	}
}

fun ByteArray.put(short: Short, offset: Int = 0) {
	val value = short.toInt()
	when (ByteOrder.nativeOrder()) {
		ByteOrder.BIG_ENDIAN -> {
			this[offset] = value.toByte()
			this[offset + 1] = (value shr 8).toByte()
		}
		ByteOrder.LITTLE_ENDIAN -> {
			this[offset + 1] = value.toByte()
			this[offset] = (value shr 8).toByte()
		}
	}
}

fun ByteArray.put(int: Int, offset: Int = 0) {
	when (ByteOrder.nativeOrder()) {
		ByteOrder.BIG_ENDIAN -> {
			this[offset] = int.toByte()
			this[offset + 1] = (int shr 8).toByte()
			this[offset + 2] = (int shr 16).toByte()
			this[offset + 3] = (int shr 24).toByte()
		}
		ByteOrder.LITTLE_ENDIAN -> {
			this[offset + 3] = int.toByte()
			this[offset + 2] = (int shr 8).toByte()
			this[offset + 1] = (int shr 16).toByte()
			this[offset] = (int shr 24).toByte()
		}
	}
}

fun ByteArray.put(long: Long, offset: Int = 0) {
	when (ByteOrder.nativeOrder()) {
		ByteOrder.BIG_ENDIAN -> {
			this[offset] = long.toByte()
			this[offset + 1] = (long shr 8).toByte()
			this[offset + 2] = (long shr 16).toByte()
			this[offset + 3] = (long shr 24).toByte()
			this[offset + 4] = (long shr 32).toByte()
			this[offset + 5] = (long shr 40).toByte()
			this[offset + 6] = (long shr 48).toByte()
			this[offset + 7] = (long shr 56).toByte()
		}
		ByteOrder.LITTLE_ENDIAN -> {
			this[offset + 7] = long.toByte()
			this[offset + 6] = (long shr 8).toByte()
			this[offset + 5] = (long shr 16).toByte()
			this[offset + 4] = (long shr 24).toByte()
			this[offset + 3] = (long shr 32).toByte()
			this[offset + 2] = (long shr 40).toByte()
			this[offset + 1] = (long shr 48).toByte()
			this[offset] = (long shr 56).toByte()
		}
	}
}

fun ByteArray.put(float: Float, offset: Int = 0) {
	put(float.toBits(), offset)
}

fun ByteArray.put(double: Double, offset: Int = 0) {
	put(double.toBits(), offset)
}

fun ByteArray.put(charArray: CharArray, offset: Int = 0, addLength: Boolean = true) {
	var index = offset
	if (addLength) {
		put(charArray.size, index)
		index += 4
	}
	charArray.forEach {
		put(it)
		index += 2
	}
}

fun ByteArray.put(shortArray: ShortArray, offset: Int = 0, addLength: Boolean = true) {
	var index = offset
	if (addLength) {
		put(shortArray.size, index)
		index += 4
	}
	shortArray.forEach {
		put(it)
		index += 2
	}
}

fun ByteArray.put(intArray: IntArray, offset: Int = 0, addLength: Boolean = true) {
	var index = offset
	if (addLength) {
		put(intArray.size, index)
		index += 4
	}
	intArray.forEach {
		put(it)
		index += 4
	}
}

fun ByteArray.put(longArray: LongArray, offset: Int = 0, addLength: Boolean = true) {
	var index = offset
	if (addLength) {
		put(longArray.size, index)
		index += 4
	}
	longArray.forEach {
		put(it)
		index += 8
	}
}

fun ByteArray.put(floatArray: FloatArray, offset: Int = 0, addLength: Boolean = true) {
	var index = offset
	if (addLength) {
		put(floatArray.size, index)
		index += 4
	}
	floatArray.forEach {
		put(it)
		index += 4
	}
}

fun ByteArray.put(doubleArray: DoubleArray, offset: Int = 0, addLength: Boolean = true) {
	var index = offset
	if (addLength) {
		put(doubleArray.size, index)
		index += 4
	}
	doubleArray.forEach {
		put(it)
		index += 8
	}
}

fun ByteArray.put(str: String, offset: Int = 0, addLength: Boolean = false): Int {
	val utf8Array = str.toByteArray()
	return if (addLength) {
		put(utf8Array.size)
		utf8Array.copyInto(this, offset + 4)
		utf8Array.size + 4
	} else {
		utf8Array.copyInto(this, offset)
		this[offset + utf8Array.size] = 0
		utf8Array.size
	}
}

fun ByteArray.pop(array: CharArray, offset: Int = 0, fromIndex: Int = 0, size: Int = (this.size - fromIndex) / 2) {
	repeat(size) {
		array[offset + it] = toChar(fromIndex + it * 2)
	}
}

fun ByteArray.pop(array: ShortArray, offset: Int = 0, fromIndex: Int = 0, size: Int = (this.size - fromIndex) / 2) {
	repeat(size) {
		array[offset + it] = toShort(fromIndex + it * 2)
	}
}

fun ByteArray.pop(array: IntArray, offset: Int = 0, fromIndex: Int = 0, size: Int = (this.size - fromIndex) / 4) {
	repeat(size) {
		array[offset + it] = toInt(fromIndex + it * 4)
	}
}

fun ByteArray.pop(array: LongArray, offset: Int = 0, fromIndex: Int = 0, size: Int = (this.size - fromIndex) / 8) {
	repeat(size) {
		array[offset + it] = toLong(fromIndex + it * 8)
	}
}

fun ByteArray.pop(array: FloatArray, offset: Int = 0, fromIndex: Int = 0, size: Int = (this.size - fromIndex) / 4) {
	repeat(size) {
		array[offset + it] = toFloat(fromIndex + it * 4)
	}
}

fun ByteArray.pop(array: DoubleArray, offset: Int = 0, fromIndex: Int = 0, size: Int = (this.size - fromIndex) / 8) {
	repeat(size) {
		array[offset + it] = toDouble(fromIndex + it * 8)
	}
}

fun ByteArray.strlen(offset: Int = 0): Int {
	for (index in offset until size) {
		if (get(index).toInt() == 0) return index
	}
	return size
}

fun Float.asInt(): Int = toRawBits()
fun Double.asLong(): Long = toRawBits()
fun Int.asFloat(): Float = Float.fromBits(this)
fun Long.asDouble(): Double = Double.fromBits(this)


fun ByteArray.put(obj: Any, offset: Int = 0): Int {
	return when (obj) {
		is Byte -> if (offset < size) {
			this[offset] = obj
			1
		} else {
			throw IndexOutOfBoundsException()
		}
		is Char -> {
			put(obj, offset)
			2
		}
		is Short -> {
			put(obj, offset)
			2
		}
		is Int -> {
			put(obj, offset)
			4
		}
		is Long -> {
			put(obj, offset)
			8
		}
		is Float -> {
			put(obj, offset)
			4
		}
		is Double -> {
			put(obj, offset)
			8
		}

		is ByteArray -> if (size < offset + obj.size) {
			put(obj.size, offset)
			obj.copyInto(this, offset + 4)
			obj.size + 4
		} else {
			throw IndexOutOfBoundsException()
		}
		is CharArray -> {
			put(obj, offset)
			obj.size * 2 + 4
		}
		is ShortArray -> {
			put(obj, offset)
			obj.size * 2 + 4
		}
		is IntArray -> {
			put(obj, offset)
			obj.size * 4 + 4
		}
		is LongArray -> {
			put(obj, offset)
			obj.size * 8 + 4
		}
		is FloatArray -> {
			put(obj, offset)
			obj.size * 4 + 4
		}
		is DoubleArray -> {
			put(obj, offset)
			obj.size * 8 + 4
		}

		is String -> {
			put(obj, offset, true)
		}

		else -> throw WrongPushTypeException()
	}
}

val Int.ipStr
	get() = formatIpAddress(this)

fun formatIpAddress(ip: Int) =
	"${ip and 0xff}.${(ip shr 8) and 0xff}.${(ip shr 16) and 0xff}.${(ip shr 24) and 0xff}"


/**
 * 序列化
 */
fun <T : OutputStream> serialize(outputStream: T, obj: Any): T {
	val oos = ObjectOutputStream(outputStream)
	oos.writeObject(obj)
	return outputStream
}

fun serialize(obj: Any): ByteArray? = try {
	val baos = ByteArrayOutputStream()
    serialize(baos, obj)
	baos.toByteArray()
} catch (e: Exception) {
	null
}

/**
 * 反序列化
 */
fun unSerialize(bytes: ByteArray, offset: Int = 0, size: Int = bytes.size): Any? = try {
	ObjectInputStream(ByteArrayInputStream(bytes, offset, size)).readObject()
} catch (e: Exception) {
	null
}

private val ByteArrayOutputStream_buf = ByteArrayOutputStream::class.java.getDeclaredField("buf").apply { isAccessible = true }
private val ByteArrayOutputStream_count = ByteArrayOutputStream::class.java.getDeclaredField("count").apply { isAccessible = true }

val ByteArrayOutputStream.buf get() = ByteArrayOutputStream_buf.get(this) as ByteArray
val ByteArrayOutputStream.count get() = ByteArrayOutputStream_count.get(this) as Int

fun ByteArray.toByteBuffer() = HeapByteBuffer(this, 0, size)

inline fun <T> Array<T>.forEachIndex(fromIndex: Int, toIndex: Int, action: (T) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}

inline fun ByteArray.forEachIndex(fromIndex: Int, toIndex: Int, action: (Byte) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}

inline fun CharArray.forEachIndex(fromIndex: Int, toIndex: Int, action: (Char) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}

inline fun ShortArray.forEachIndex(fromIndex: Int, toIndex: Int, action: (Short) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}

inline fun IntArray.forEachIndex(fromIndex: Int, toIndex: Int, action: (Int) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}

inline fun LongArray.forEachIndex(fromIndex: Int, toIndex: Int, action: (Long) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}

inline fun FloatArray.forEachIndex(fromIndex: Int, toIndex: Int, action: (Float) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}

inline fun DoubleArray.forEachIndex(fromIndex: Int, toIndex: Int, action: (Double) -> Unit) {
	for (i in fromIndex..toIndex) {
		action(this[i])
	}
}