@file:Suppress("unused", "DuplicatedCode")

package cn.tursom.core

import java.io.*
import java.nio.ByteOrder

class WrongPushTypeException : Exception()

fun Char.reverseBytes() = Character.reverseBytes(this)
fun Short.reverseBytes() = java.lang.Short.reverseBytes(this)
fun Int.reverseBytes() = Integer.reverseBytes(this)
fun Long.reverseBytes() = java.lang.Long.reverseBytes(this)
fun Float.reverseBytes() = Float.fromBits(toBits().reverseBytes())
fun Double.reverseBytes() = Double.fromBits(toBits().reverseBytes())

fun Char.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(2)
  array.put(this, 0, byteOrder)
  return array
}

fun Short.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(2)
  array.put(this, 0, byteOrder)
  return array
}

fun Int.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(4)
  array.put(this, 0, byteOrder)
  return array
}

fun Long.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(8)
  array.put(this, 0, byteOrder)
  return array
}

fun Float.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(4)
  array.put(this, 0, byteOrder)
  return array
}

fun Double.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(8)
  array.put(this, 0, byteOrder)
  return array
}

fun CharArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 2)
  repeat(size) {
    newArray.put(this[it], it * 2, byteOrder)
  }
  return newArray
}

fun ShortArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 2)
  repeat(size) {
    newArray.put(this[it], it * 2, byteOrder)
  }
  return newArray
}

fun IntArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 4)
  repeat(size) {
    newArray.put(this[it], it * 4, byteOrder)
  }
  return newArray
}

fun LongArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 8)
  repeat(size) {
    newArray.put(this[it], it * 8, byteOrder)
  }
  return newArray
}

fun FloatArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 4)
  repeat(size) {
    newArray.put(this[it], it * 4, byteOrder)
  }
  return newArray
}

fun DoubleArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 8)
  repeat(size) {
    newArray.put(this[it], it * 8, byteOrder)
  }
  return newArray
}

fun ByteArray.toChar(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Char {
  return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
    (this[offset].toInt() or (this[offset + 1].toInt() shl 8))
  } else {
    (this[offset + 1].toInt() or (this[offset].toInt() shl 8))
  }.toChar()
}

fun ByteArray.toShort(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Short {
  return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
    (this[offset].toInt() or (this[offset + 1].toInt() shl 8))
  } else {
    (this[offset + 1].toInt() or (this[offset].toInt() shl 8))
  }.toShort()
}

fun ByteArray.toInt(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
  return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
    this[offset].toInt() and 0xff or (this[offset + 1].toInt() shl 8 and 0xff00) or
      (this[offset + 2].toInt() shl 16 and 0xff0000) or (this[offset + 3].toInt() shl 24 and 0xff000000.toInt())
  } else {
    this[offset + 3].toInt() and 0xff or (this[offset + 2].toInt() shl 8 and 0xff00) or
      (this[offset + 1].toInt() shl 16 and 0xff0000) or (this[offset].toInt() shl 24 and 0xff000000.toInt())
  }
}

fun ByteArray.toLong(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Long {
  return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
    toInt(offset).toLong() or (toInt(offset + 4).toLong().shl(32) and 0xffff_ffffL.inv())
  } else {
    toInt(offset + 4).toLong() or (toInt(offset).toLong().shl(32) and 0xffff_ffffL.inv())
  }.toLong()
}

fun ByteArray.toFloat(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Float {
  return Float.fromBits(toInt(offset, byteOrder))
}

fun ByteArray.toDouble(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Double {
  return Double.fromBits(toLong(offset, byteOrder))
}

fun ByteArray.toCharArray(offset: Int, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): CharArray {
  if (offset + size * 2 > this.size) throw IndexOutOfBoundsException()
  val newArray = CharArray(size)
  repeat(newArray.size) {
    newArray[it] = toChar(it * 2, byteOrder)
  }
  return newArray
}

fun ByteArray.toShortArray(offset: Int, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ShortArray {
  if (offset + size * 2 > this.size) throw IndexOutOfBoundsException()
  val newArray = ShortArray(size)
  repeat(newArray.size) {
    newArray[it] = toShort(it * 2, byteOrder)
  }
  return newArray
}

fun ByteArray.toIntArray(offset: Int, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): IntArray {
  if (offset + size * 4 > this.size) throw IndexOutOfBoundsException()
  val newArray = IntArray(size)
  repeat(newArray.size) {
    newArray[it] = toInt(it * 4, byteOrder)
  }
  return newArray
}

fun ByteArray.toLongArray(offset: Int, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): LongArray {
  if (offset + size * 8 > this.size) throw IndexOutOfBoundsException()
  val newArray = LongArray(size)
  repeat(newArray.size) {
    newArray[it] = toLong(it * 8, byteOrder)
  }
  return newArray
}

fun ByteArray.toFloatArray(offset: Int, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): FloatArray {
  if (offset + size * 4 > this.size) throw IndexOutOfBoundsException()
  val newArray = FloatArray(size)
  repeat(newArray.size) {
    newArray[it] = toFloat(it * 4, byteOrder)
  }
  return newArray
}

fun ByteArray.toDoubleArray(offset: Int, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): DoubleArray {
  if (offset + size * 8 > this.size) throw IndexOutOfBoundsException()
  val newArray = DoubleArray(size)
  repeat(newArray.size) {
    newArray[it] = toDouble(it * 8, byteOrder)
  }
  return newArray
}

fun Short.hton(): Short = ntoh()
fun Short.ntoh(): Short {
  return if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
    this
  } else {
    val value = toInt()
    (value shr 8 or (value shl 8)).toShort()
  }
}


fun Int.hton(): Int = ntoh()
fun Int.ntoh(): Int {
  return if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
    this
  } else {
    shr(24) or (shr(16) and 0xff00) or (shr(8) and 0xff0000) or (this and 0xff)
  }
}


fun Long.hton(): Long = ntoh()
fun Long.ntoh(): Long {
  return if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
    this
  } else {
    shr(56) or (shr(48) and 0xff00) or (shr(40) and 0xff0000)
    or(shr(32) and 0xff000000) or (shl(32) and (0xff shl 32))
    or(shl(40) and (0xff shl 40)) or (shl(48) and (0xff shl 48)) or shl(56)
  }
}

fun ByteArray.put(char: Char, offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  val value = char.code
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      this[offset] = value.toByte()
      this[offset + 1] = (value shr 8).toByte()
    }
    ByteOrder.BIG_ENDIAN -> {
      this[offset + 1] = value.toByte()
      this[offset] = (value shr 8).toByte()
    }
  }
}

fun ByteArray.put(short: Short, offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  val value = short.toInt()
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      this[offset] = value.toByte()
      this[offset + 1] = (value shr 8).toByte()
    }
    ByteOrder.BIG_ENDIAN -> {
      this[offset + 1] = value.toByte()
      this[offset] = (value shr 8).toByte()
    }
  }
}

fun ByteArray.put(int: Int, offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      this[offset] = int.toByte()
      this[offset + 1] = (int shr 8).toByte()
      this[offset + 2] = (int shr 16).toByte()
      this[offset + 3] = (int shr 24).toByte()
    }
    ByteOrder.BIG_ENDIAN -> {
      this[offset + 3] = int.toByte()
      this[offset + 2] = (int shr 8).toByte()
      this[offset + 1] = (int shr 16).toByte()
      this[offset] = (int shr 24).toByte()
    }
  }
}

fun ByteArray.put(long: Long, offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      this[offset] = long.toByte()
      this[offset + 1] = (long shr 8).toByte()
      this[offset + 2] = (long shr 16).toByte()
      this[offset + 3] = (long shr 24).toByte()
      this[offset + 4] = (long shr 32).toByte()
      this[offset + 5] = (long shr 40).toByte()
      this[offset + 6] = (long shr 48).toByte()
      this[offset + 7] = (long shr 56).toByte()
    }
    ByteOrder.BIG_ENDIAN -> {
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

fun ByteArray.put(float: Float, offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  put(float.toBits(), offset, byteOrder)
}

fun ByteArray.put(double: Double, offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  put(double.toBits(), offset, byteOrder)
}

fun ByteArray.put(
  charArray: CharArray,
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  var index = offset
  if (addLength) {
    put(charArray.size, index, byteOrder)
    index += 4
  }
  charArray.forEach {
    put(it, index, byteOrder)
    index += 2
  }
}

fun ByteArray.put(
  shortArray: ShortArray,
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  var index = offset
  if (addLength) {
    put(shortArray.size, index, byteOrder)
    index += 4
  }
  shortArray.forEach {
    put(it, index, byteOrder)
    index += 2
  }
}

fun ByteArray.put(
  intArray: IntArray,
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  var index = offset
  if (addLength) {
    put(intArray.size, index, byteOrder)
    index += 4
  }
  intArray.forEach {
    put(it)
    index += 4
  }
}

fun ByteArray.put(
  longArray: LongArray,
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  var index = offset
  if (addLength) {
    put(longArray.size, index, byteOrder)
    index += 4
  }
  longArray.forEach {
    put(it, index, byteOrder)
    index += 8
  }
}

fun ByteArray.put(
  floatArray: FloatArray,
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  var index = offset
  if (addLength) {
    put(floatArray.size, index, byteOrder)
    index += 4
  }
  floatArray.forEach {
    put(it, index, byteOrder)
    index += 4
  }
}

fun ByteArray.put(
  doubleArray: DoubleArray,
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  var index = offset
  if (addLength) {
    put(doubleArray.size, index, byteOrder)
    index += 4
  }
  doubleArray.forEach {
    put(it, index, byteOrder)
    index += 8
  }
}

fun ByteArray.put(
  str: String,
  offset: Int = 0,
  addLength: Boolean = false,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): Int {
  val utf8Array = str.toByteArray()
  return if (addLength) {
    put(utf8Array.size, 0, byteOrder)
    utf8Array.copyInto(this, offset + 4)
    utf8Array.size + 4
  } else {
    utf8Array.copyInto(this, offset)
    this[offset + utf8Array.size] = 0
    utf8Array.size
  }
}

fun ByteArray.pop(
  array: CharArray,
  offset: Int = 0,
  fromIndex: Int = 0,
  size: Int = (this.size - fromIndex) / 2,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  repeat(size) {
    array[offset + it] = toChar(fromIndex + it * 2, byteOrder)
  }
}

fun ByteArray.pop(
  array: ShortArray,
  offset: Int = 0,
  fromIndex: Int = 0,
  size: Int = (this.size - fromIndex) / 2,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  repeat(size) {
    array[offset + it] = toShort(fromIndex + it * 2, byteOrder)
  }
}

fun ByteArray.pop(
  array: IntArray,
  offset: Int = 0,
  fromIndex: Int = 0,
  size: Int = (this.size - fromIndex) / 4,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  repeat(size) {
    array[offset + it] = toInt(fromIndex + it * 4, byteOrder)
  }
}

fun ByteArray.pop(
  array: LongArray,
  offset: Int = 0,
  fromIndex: Int = 0,
  size: Int = (this.size - fromIndex) / 8,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  repeat(size) {
    array[offset + it] = toLong(fromIndex + it * 8, byteOrder)
  }
}

fun ByteArray.pop(
  array: FloatArray,
  offset: Int = 0,
  fromIndex: Int = 0,
  size: Int = (this.size - fromIndex) / 4,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  repeat(size) {
    array[offset + it] = toFloat(fromIndex + it * 4, byteOrder)
  }
}

fun ByteArray.pop(
  array: DoubleArray,
  offset: Int = 0,
  fromIndex: Int = 0,
  size: Int = (this.size - fromIndex) / 8,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
) {
  repeat(size) {
    array[offset + it] = toDouble(fromIndex + it * 8, byteOrder)
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


fun ByteArray.put(obj: Any, offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
  return when (obj) {
    is Byte -> if (offset < size) {
      this[offset] = obj
      1
    } else {
      throw IndexOutOfBoundsException()
    }
    is Char -> {
      put(obj, offset, byteOrder)
      2
    }
    is Short -> {
      put(obj, offset, byteOrder)
      2
    }
    is Int -> {
      put(obj, offset, byteOrder)
      4
    }
    is Long -> {
      put(obj, offset, byteOrder)
      8
    }
    is Float -> {
      put(obj, offset, byteOrder)
      4
    }
    is Double -> {
      put(obj, offset, byteOrder)
      8
    }

    is ByteArray -> if (size < offset + obj.size) {
      put(obj.size, offset, byteOrder)
      obj.copyInto(this, offset + 4)
      obj.size + 4
    } else {
      throw IndexOutOfBoundsException()
    }
    is CharArray -> {
      put(obj, offset, byteOrder)
      obj.size * 2 + 4
    }
    is ShortArray -> {
      put(obj, offset, byteOrder)
      obj.size * 2 + 4
    }
    is IntArray -> {
      put(obj, offset, byteOrder)
      obj.size * 4 + 4
    }
    is LongArray -> {
      put(obj, offset, byteOrder)
      obj.size * 8 + 4
    }
    is FloatArray -> {
      put(obj, offset, byteOrder)
      obj.size * 4 + 4
    }
    is DoubleArray -> {
      put(obj, offset, byteOrder)
      obj.size * 8 + 4
    }

    is String -> {
      put(obj, offset, true, byteOrder)
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

private val ByteArrayOutputStream_buf =
  ByteArrayOutputStream::class.java.getDeclaredField("buf").apply { isAccessible = true }
private val ByteArrayOutputStream_count =
  ByteArrayOutputStream::class.java.getDeclaredField("count").apply { isAccessible = true }

val ByteArrayOutputStream.buf get() = ByteArrayOutputStream_buf.get(this) as ByteArray
val ByteArrayOutputStream.count get() = ByteArrayOutputStream_count.get(this) as Int

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

inline fun Char.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  val value = code
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      action(value.toByte())
      action((value shr 8).toByte())
    }
    ByteOrder.BIG_ENDIAN -> {
      action((value shr 8).toByte())
      action(value.toByte())
    }
  }
}

inline fun Short.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  val value = toInt()
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      action(value.toByte())
      action((value shr 8).toByte())
    }
    ByteOrder.BIG_ENDIAN -> {
      action((value shr 8).toByte())
      action(value.toByte())
    }
  }
}

inline fun Int.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      action(this.toByte())
      action((this shr 8).toByte())
      action((this shr 16).toByte())
      action((this shr 24).toByte())
    }
    ByteOrder.BIG_ENDIAN -> {
      action((this shr 24).toByte())
      action((this shr 16).toByte())
      action((this shr 8).toByte())
      action(this.toByte())
    }
  }
}

inline fun Long.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      action(this.toByte())
      action((this shr 8).toByte())
      action((this shr 16).toByte())
      action((this shr 24).toByte())
      action((this shr 32).toByte())
      action((this shr 40).toByte())
      action((this shr 48).toByte())
      action((this shr 56).toByte())
    }
    ByteOrder.BIG_ENDIAN -> {
      action((this shr 56).toByte())
      action((this shr 48).toByte())
      action((this shr 40).toByte())
      action((this shr 32).toByte())
      action((this shr 24).toByte())
      action((this shr 16).toByte())
      action((this shr 8).toByte())
      action(this.toByte())
    }
  }
}

inline fun Float.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  toBits().toBytes(byteOrder, action)
}

inline fun Double.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  toBits().toBytes(byteOrder, action)
}

inline fun toChar(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Char {
  return when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      get().toInt() or (get().toInt() shl 8)
    }
    ByteOrder.BIG_ENDIAN -> {
      val late = get()
      get().toInt() or (late.toInt() shl 8)
    }
    else -> {
      throw UnsupportedOperationException()
    }
  }.toChar()
}

inline fun toShort(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Short {
  return when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      get().toInt() or (get().toInt() shl 8)
    }
    ByteOrder.BIG_ENDIAN -> {
      val late = get()
      get().toInt() or (late.toInt() shl 8)
    }
    else -> throw UnsupportedOperationException()
  }.toShort()
}

inline fun toInt(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Int {
  return when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      get().toInt() and 0xff or (get().toInt() shl 8 and 0xff00) or
        (get().toInt() shl 16 and 0xff0000) or (get().toInt() shl 24 and 0xff000000.toInt())
    }
    ByteOrder.BIG_ENDIAN -> {
      val i1 = get()
      val i2 = get()
      val i3 = get()
      get().toInt() and 0xff or (i3.toInt() shl 8 and 0xff00) or
        (i2.toInt() shl 16 and 0xff0000) or (i1.toInt() shl 24 and 0xff000000.toInt())
    }
    else -> throw UnsupportedOperationException()
  }
}

inline fun toLong(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Long {
  return if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
    toInt(byteOrder, get).toLong() or (toInt(byteOrder, get).toLong().shl(32) and 0xffff_ffffL.inv())
  } else {
    val late = toInt(byteOrder, get)
    get().toLong() or (late.toLong().shl(32) and 0xffff_ffffL.inv())
  }.toLong()
}

inline fun toFloat(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Float {
  return Float.fromBits(toInt(byteOrder, get))
}

inline fun toDouble(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Double {
  return Double.fromBits(toLong(byteOrder, get))
}


private val UPPER_HEX_ARRAY = "0123456789ABCDEF".toCharArray()
private val LOWER_HEX_ARRAY = "0123456789abcdef".toCharArray()

private inline fun toHexString(
  upper: Boolean,
  length: Int,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  toBytes: (ByteOrder, (Byte) -> Unit) -> Unit,
): String {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(length)
  var i = 0
  toBytes(byteOrder) { b ->
    @Suppress("NAME_SHADOWING") val b = b.toInt()
    hexChars[i shl 1] = hexArray[b ushr 4 and 0x0F]
    hexChars[(i shl 1) + 1] = hexArray[b and 0x0F]
    i++
  }
  return String(hexChars)
}

fun Char.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String = toHexString(upper, 4, byteOrder, ::toBytes)

fun Short.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String = toHexString(upper, 4, byteOrder, ::toBytes)

fun Int.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String = toHexString(upper, 8, byteOrder, ::toBytes)

fun Long.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String = toHexString(upper, 16, byteOrder, ::toBytes)

fun Float.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String = toHexString(upper, 8, byteOrder, ::toBytes)

fun Double.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String = toHexString(upper, 16, byteOrder, ::toBytes)

