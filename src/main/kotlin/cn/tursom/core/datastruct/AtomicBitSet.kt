package cn.tursom.core.datastruct

import java.io.Serializable
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicLongArray

class AtomicBitSet(beginSize: Long = 256, val defaultState: Boolean = false) : Serializable {
  @Volatile
  private var bitSet = AtomicLongArray(needSize(beginSize))
  val size
    get() = bitSet.length().toLong() shl 6
  val usedSize
    get() = bitSet.length() * 8
  val usedSizeStr: String
    get() {
      val memory = usedSize
      val b = memory % 1024
      val kb = (memory / 1024) % 1024
      val mb = (memory / 1024 / 1024) % 1024
      return "${if (mb != 0) "$mb MB " else ""}${if (kb != 0) "$kb KB " else ""} $b Byte"
    }
  val trueCount: Long
    get() {
      var count = 0L
      bitSet.array.forEach { count += it.bitCount }
      return count
    }

  init {
    val default = if (defaultState) -1L else 0L
    for (i in 0 until bitSet.length()) {
      bitSet[i] = default
    }
  }

  operator fun get(index: Long): Boolean {
    return bitSet[(index shr 6).toInt()] and getArr[index.toInt() and 63] != 0L
  }

  fun up(index: Long): Boolean {
    val arrayIndex = (index shr 6).toInt()
    //bitSet[arrayIndex] = bitSet[arrayIndex] or getArr[index.toInt() and 63]
    val expect = bitSet[arrayIndex]
    return bitSet.compareAndSet(arrayIndex, expect, expect or getArr[index.toInt() and 63])
  }

  fun down(index: Long): Boolean {
    val arrayIndex = (index shr 6).toInt()
    //bitSet[arrayIndex] = bitSet[arrayIndex] and setArr[index.toInt() and 63]
    val expect = bitSet[arrayIndex]
    return bitSet.compareAndSet(arrayIndex, expect, expect and setArr[index.toInt() and 63])
  }

  fun upAll() {
    for (i in 0 until bitSet.length()) {
      bitSet[i] = -1
    }
  }

  fun downAll() {
    for (i in 0 until bitSet.length()) {
      bitSet[i] = 0
    }
  }

  fun resize(newSIze: Long): Boolean = synchronized(this) {
    if (newSIze < this.size) return false

    val newSet = AtomicLongArray(needSize(newSIze))

    bitSet.array.copyInto(newSet.array)

    val default = if (defaultState) -1L else 0
    for (i in bitSet.length() until newSet.length()) {
      newSet[i] = default
    }

    bitSet = newSet
    return true
  }

  fun firstUp(): Long {
    bitSet.forEachIndexed { index, l ->
      if (l != 0L) {
        for (i in 0 until 8) {
          if (l and scanArray[i] != 0L) {
            val baseIndex = i * 8
            for (j in 0 until 8) {
              if (l and getArr[baseIndex + j] != 0L) return index.toLong() * 64 + baseIndex + j
            }
          }
        }
      }
    }
    return -1
  }

  fun firstDown(): Long {
    bitSet.forEachIndexed { index, l ->
      if (l != -1L) {
        for (i in 0 until 8) {
          if (l.inv() and scanArray[i] != 0L) {
            val baseIndex = i * 8
            for (j in 0 until 8) {
              if (l and getArr[baseIndex + j] == 0L) return index.toLong() * 64 + baseIndex + j
            }
          }
        }
      }
    }
    return -1
  }

  override fun toString() = "MemoryBitArray(max size=$size, true count=$trueCount, used memory=$usedSizeStr)"

  companion object {
    @JvmStatic
    fun needSize(maxIndex: Long) = ((((maxIndex - 1) shr 6) + 1) and 0xffffffff).toInt()

    private val getArr = longArrayOf(
      1L shl 0, 1L shl 1, 1L shl 2, 1L shl 3,
      1L shl 4, 1L shl 5, 1L shl 6, 1L shl 7,
      1L shl 8, 1L shl 9, 1L shl 10, 1L shl 11,
      1L shl 12, 1L shl 13, 1L shl 14, 1L shl 15,
      1L shl 16, 1L shl 17, 1L shl 18, 1L shl 19,
      1L shl 20, 1L shl 21, 1L shl 22, 1L shl 23,
      1L shl 24, 1L shl 25, 1L shl 26, 1L shl 27,
      1L shl 28, 1L shl 29, 1L shl 30, 1L shl 31,
      1L shl 32, 1L shl 33, 1L shl 34, 1L shl 35,
      1L shl 36, 1L shl 37, 1L shl 38, 1L shl 39,
      1L shl 40, 1L shl 41, 1L shl 42, 1L shl 43,
      1L shl 44, 1L shl 45, 1L shl 46, 1L shl 47,
      1L shl 48, 1L shl 49, 1L shl 50, 1L shl 51,
      1L shl 52, 1L shl 53, 1L shl 54, 1L shl 55,
      1L shl 56, 1L shl 57, 1L shl 58, 1L shl 59,
      1L shl 60, 1L shl 61, 1L shl 62, 1L shl 63
    )

    private val setArr = LongArray(64) { getArr[it].inv() }

    private val bitCountArray = longArrayOf(
      0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8
    )

    private val scanArray = longArrayOf(
      0xffL, 0xffL shl 8, 0xffL shl 16, 0xffL shl 24,
      0xffL shl 32, 0xffL shl 40, 0xffL shl 48, 0xffL shl 56
    )

    private val Long.bitCount
      get() = bitCountArray[toInt().and(0xff)] + bitCountArray[shr(8).toInt().and(0xff)] +
          bitCountArray[shr(16).toInt().and(0xff)] + bitCountArray[shr(24).toInt().and(0xff)] +
          bitCountArray[shr(32).toInt().and(0xff)] + bitCountArray[shr(40).toInt().and(0xff)] +
          bitCountArray[shr(48).toInt().and(0xff)] + bitCountArray[shr(56).toInt().and(0xff)]

    private val AtomicLongArray.size get() = length()

    private val array: Field = AtomicLongArray::class.java.getDeclaredField("array")
    private val AtomicLongArray.array get() = Companion.array.get(this) as LongArray

    init {
      array.isAccessible = true
    }

    inline fun AtomicLongArray.forEachIndexed(action: (index: Int, Long) -> Unit) {
      repeat(length()) {
        action(it, get(it))
      }
    }
  }
}
