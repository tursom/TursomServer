package cn.tursom.proxy.util

import cn.tursom.core.util.uncheckedCast

class IntMap<V> {
  companion object {
    private fun upToPowerOf2(n: Int): Int {
      if (n and n - 1 == 0) {
        return n
      }

      var n = n
      n = n or (n ushr 1)
      n = n or (n ushr 2)
      n = n or (n ushr 4)
      n = n or (n ushr 8)
      n = n or (n ushr 16)

      return n + 1
    }
  }

  private var array = arrayOfNulls<Any>(32)

  fun clear() {
    array = arrayOfNulls(16)
  }

  operator fun get(index: Int): V? = if (index >= array.size) {
    null
  } else {
    array[index].uncheckedCast()
  }

  operator fun set(index: Int, value: V) {
    if (index >= array.size) {
      array = array.copyOf(upToPowerOf2(index + 1))
    }

    array[index] = value
  }
}