package cn.tursom.core.datastruct

import java.util.concurrent.atomic.AtomicLong

class LongBitSet {
  private val bitSet = AtomicLong(0)

  val trueCount: Long get() = bitSet.get().bitCount

  fun up(index: Int) = bitSet.compareAndSet(bitSet.get(), bitSet.get() or getArr[index and 63])
  fun down(index: Int) = bitSet.compareAndSet(bitSet.get(), bitSet.get() and setArr[index and 63])

  fun firstUp(): Int {
    val l = bitSet.get()
    if (l != 0L) {
      for (i in 0 until 8) {
        if (l and scanArray[i] != 0L) {
          val baseIndex = i * 8
          for (j in 0 until 8) {
            if (l and getArr[baseIndex + j] != 0L) return baseIndex + j
          }
        }
      }
    }
    return -1
  }

  fun firstDown(): Int {
    val l = bitSet.get()
    if (l != -1L) {
      for (i in 0 until 8) {
        if (l.inv() and scanArray[i] != 0L) {
          val baseIndex = i * 8
          for (j in 0 until 8) {
            if (l and getArr[baseIndex + j] == 0L) return baseIndex + j
          }
        }
      }
    }
    return -1
  }

  companion object {
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
  }
}