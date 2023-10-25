@file:Suppress("unused", "DuplicatedCode")

package cn.tursom.core.util

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
  array[0, byteOrder] = this
  return array
}

fun Short.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(2)
  array[0, byteOrder] = this
  return array
}

fun Int.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(4)
  array[0, byteOrder] = this
  return array
}

fun Long.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(8)
  array[0, byteOrder] = this
  return array
}

fun Float.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(4)
  array[0, byteOrder] = this
  return array
}

fun Double.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val array = ByteArray(8)
  array[0, byteOrder] = this
  return array
}

fun CharArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 2)
  repeat(size) {
    newArray[it * 2, byteOrder] = this[it]
  }
  return newArray
}

fun ShortArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 2)
  repeat(size) {
    newArray[it * 2, byteOrder] = this[it]
  }
  return newArray
}

fun IntArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 4)
  repeat(size) {
    newArray[it * 4, byteOrder] = this[it]
  }
  return newArray
}

fun LongArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 8)
  repeat(size) {
    newArray[it * 8, byteOrder] = this[it]
  }
  return newArray
}

fun FloatArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 4)
  repeat(size) {
    newArray[it * 4, byteOrder] = this[it]
  }
  return newArray
}

fun DoubleArray.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
  val newArray = ByteArray(size * 8)
  repeat(size) {
    newArray[it * 8, byteOrder] = this[it]
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

operator fun ByteArray.set(
  offset: Int = 0,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  char: Char
) {
  val value = char.code
  when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
      this[offset + 1] = value.toByte()
      this[offset] = (value shr 8).toByte()
    }

    ByteOrder.LITTLE_ENDIAN -> {
      this[offset] = value.toByte()
      this[offset + 1] = (value shr 8).toByte()
    }

    else -> throw UnsupportedOperationException()
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  short: Short
) {
  val value = short.toInt()
  when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
      this[offset + 1] = value.toByte()
      this[offset] = (value shr 8).toByte()
    }

    ByteOrder.LITTLE_ENDIAN -> {
      this[offset] = value.toByte()
      this[offset + 1] = (value shr 8).toByte()
    }

    else -> throw UnsupportedOperationException()
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  int: Int,
) {
  when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
      this[offset + 3] = int.toByte()
      this[offset + 2] = (int shr 8).toByte()
      this[offset + 1] = (int shr 16).toByte()
      this[offset] = (int shr 24).toByte()
    }

    ByteOrder.LITTLE_ENDIAN -> {
      this[offset] = int.toByte()
      this[offset + 1] = (int shr 8).toByte()
      this[offset + 2] = (int shr 16).toByte()
      this[offset + 3] = (int shr 24).toByte()
    }

    else -> throw UnsupportedOperationException()
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  long: Long
) {
  when (byteOrder) {
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

    else -> throw UnsupportedOperationException()
  }
}

operator fun ByteArray.set(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, float: Float) {
  set(offset, byteOrder, float.toBits())
}

operator fun ByteArray.set(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, double: Double) {
  set(offset, byteOrder, double.toBits())
}

operator fun ByteArray.set(
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  charArray: CharArray,
) {
  var index = offset
  if (addLength) {
    set(index, byteOrder, charArray.size)
    index += 4
  }
  charArray.forEach {
    set(index, byteOrder, it)
    index += 2
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  shortArray: ShortArray,
) {
  var index = offset
  if (addLength) {
    set(index, byteOrder, shortArray.size)
    index += 4
  }
  shortArray.forEach {
    set(index, byteOrder, it)
    index += 2
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  intArray: IntArray,
) {
  var index = offset
  if (addLength) {
    set(index, byteOrder, intArray.size)
    index += 4
  }
  intArray.forEach {
    set(index, byteOrder, it)
    index += 4
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  longArray: LongArray,
) {
  var index = offset
  if (addLength) {
    set(index, byteOrder, longArray.size)
    index += 4
  }
  longArray.forEach {
    set(index, byteOrder, it)
    index += 8
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  floatArray: FloatArray,
) {
  var index = offset
  if (addLength) {
    set(index, byteOrder, floatArray.size)
    index += 4
  }
  floatArray.forEach {
    set(index, byteOrder, it)
    index += 4
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  addLength: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  doubleArray: DoubleArray,
) {
  var index = offset
  if (addLength) {
    set(index, byteOrder, doubleArray.size)
    index += 4
  }
  doubleArray.forEach {
    set(index, byteOrder, it)
    index += 8
  }
}

operator fun ByteArray.set(
  offset: Int = 0,
  addLength: Boolean = false,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  str: String,
): Int {
  val utf8Array = str.toByteArray()
  return if (addLength) {
    set(0, byteOrder, utf8Array.size)
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


operator fun ByteArray.set(offset: Int = 0, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, obj: Any): Int {
  return when (obj) {
    is Byte -> if (offset < size) {
      this[offset] = obj
      1
    } else {
      throw IndexOutOfBoundsException()
    }

    is Char -> {
      set(offset, byteOrder, obj)
      2
    }

    is Short -> {
      set(offset, byteOrder, obj)
      2
    }

    is Int -> {
      set(offset, byteOrder, obj)
      4
    }

    is Long -> {
      set(offset, byteOrder, obj)
      8
    }

    is Float -> {
      set(offset, byteOrder, obj)
      4
    }

    is Double -> {
      set(offset, byteOrder, obj)
      8
    }

    is ByteArray -> if (size < offset + obj.size) {
      set(offset, byteOrder, obj.size)
      obj.copyInto(this, offset + 4)
      obj.size + 4
    } else {
      throw IndexOutOfBoundsException()
    }

    is CharArray -> {
      set(offset, byteOrder, obj)
      obj.size * 2 + 4
    }

    is ShortArray -> {
      set(offset, byteOrder, obj)
      obj.size * 2 + 4
    }

    is IntArray -> {
      set(offset, byteOrder, obj)
      obj.size * 4 + 4
    }

    is LongArray -> {
      set(offset, byteOrder, obj)
      obj.size * 8 + 4
    }

    is FloatArray -> {
      set(offset, byteOrder, obj)
      obj.size * 4 + 4
    }

    is DoubleArray -> {
      set(offset, byteOrder, obj)
      obj.size * 8 + 4
    }

    is String -> {
      set(offset, true, byteOrder, obj)
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
    ByteOrder.BIG_ENDIAN -> {
      action((value shr 8).toByte())
      action(value.toByte())
    }

    ByteOrder.LITTLE_ENDIAN -> {
      action(value.toByte())
      action((value shr 8).toByte())
    }

    else -> throw UnsupportedOperationException()
  }
}

inline fun Short.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  val value = toInt()
  when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
      action((value shr 8).toByte())
      action(value.toByte())
    }

    ByteOrder.LITTLE_ENDIAN -> {
      action(value.toByte())
      action((value shr 8).toByte())
    }

    else -> throw UnsupportedOperationException()
  }
}

inline fun Int.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
      action((this shr 24).toByte())
      action((this shr 16).toByte())
      action((this shr 8).toByte())
      action(this.toByte())
    }

    ByteOrder.LITTLE_ENDIAN -> {
      action(this.toByte())
      action((this shr 8).toByte())
      action((this shr 16).toByte())
      action((this shr 24).toByte())
    }

    else -> throw UnsupportedOperationException()
  }
}

inline fun Long.toBytes(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, action: (Byte) -> Unit) {
  when (byteOrder) {
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

    else -> throw UnsupportedOperationException()
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
    ByteOrder.BIG_ENDIAN -> {
      val late = get()
      get().toInt() or (late.toInt() shl 8)
    }

    ByteOrder.LITTLE_ENDIAN -> {
      get().toInt() or (get().toInt() shl 8)
    }

    else -> throw UnsupportedOperationException()
  }.toChar()
}

inline fun toShort(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Short {
  return when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
      val late = get()
      get().toInt() or (late.toInt() shl 8)
    }

    ByteOrder.LITTLE_ENDIAN -> {
      get().toInt() or (get().toInt() shl 8)
    }

    else -> throw UnsupportedOperationException()
  }.toShort()
}

inline fun toInt(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN, get: () -> Byte): Int {
  return when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
      val i1 = get()
      val i2 = get()
      val i3 = get()
      get().toInt() and 0xff or (i3.toInt() shl 8 and 0xff00) or
        (i2.toInt() shl 16 and 0xff0000) or (i1.toInt() shl 24 and 0xff000000.toInt())
    }

    ByteOrder.LITTLE_ENDIAN -> {
      get().toInt() and 0xff or (get().toInt() shl 8 and 0xff00) or
        (get().toInt() shl 16 and 0xff0000) or (get().toInt() shl 24 and 0xff000000.toInt())
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

@Suppress("NOTHING_TO_INLINE")
private inline fun toHex(hexArray: CharArray, b: Int, ca: CharArray, index: Int) {
  ca[index] = hexArray[b ushr 4 and 0x0F]
  ca[index + 1] = hexArray[b and 0x0F]
}

fun Byte.toHexChars(
  upper: Boolean = true,
): CharArray {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(2)
  toHex(hexArray, toInt(), hexChars, 0)
  return hexChars
}

fun Char.toHexChars(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): CharArray {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(4)
  var i = 0
  toBytes(byteOrder) {
    toHex(hexArray, it.toInt(), hexChars, i)
    i += 2
  }
  return hexChars
}

fun Short.toHexChars(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): CharArray {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(4)
  var i = 0
  toBytes(byteOrder) {
    toHex(hexArray, it.toInt(), hexChars, i)
    i += 2
  }
  return hexChars
}

fun Int.toHexChars(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): CharArray {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(8)
  var i = 0
  toBytes(byteOrder) {
    toHex(hexArray, it.toInt(), hexChars, i)
    i += 2
  }
  return hexChars
}

fun Long.toHexChars(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): CharArray {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(16)
  var i = 0
  toBytes(byteOrder) {
    toHex(hexArray, it.toInt(), hexChars, i)
    i += 2
  }
  return hexChars
}

fun Float.toHexChars(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): CharArray {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(8)
  var i = 0
  toBytes(byteOrder) {
    toHex(hexArray, it.toInt(), hexChars, i)
    i += 2
  }
  return hexChars
}

fun Double.toHexChars(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): CharArray {
  val hexArray = if (upper) UPPER_HEX_ARRAY else LOWER_HEX_ARRAY
  val hexChars = CharArray(16)
  var i = 0
  toBytes(byteOrder) {
    toHex(hexArray, it.toInt(), hexChars, i)
    i += 2
  }
  return hexChars
}

fun Byte.toHexString(
  upper: Boolean = true,
): String {
  return toHexChars(upper).packageToString()
}

fun Char.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String {
  return toHexChars(upper, byteOrder).packageToString()
}

fun Short.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String {
  return toHexChars(upper, byteOrder).packageToString()
}

fun Int.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String {
  return toHexChars(upper, byteOrder).packageToString()
}

fun Long.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String {
  return toHexChars(upper, byteOrder).packageToString()
}

fun Float.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String {
  return toHexChars(upper, byteOrder).packageToString()
}

fun Double.toHexString(
  upper: Boolean = true,
  byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
): String {
  return toHexChars(upper, byteOrder).packageToString()
}

