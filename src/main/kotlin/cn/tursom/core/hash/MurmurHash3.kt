package cn.tursom.core.hash

import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.toHexString
import cn.tursom.core.toUTF8String
import java.io.File


@Suppress("unused", "FunctionName")
object MurmurHash3 {
  private fun fmix32(h: Int): Int {
    var h = h
    h = h xor (h ushr 16)
    h *= -0x7a143595
    h = h xor (h ushr 13)
    h *= -0x3d4d51cb
    h = h xor (h ushr 16)
    return h
  }

  private fun fmix64(k: Long): Long {
    var k = k
    k = k xor (k ushr 33)
    k *= -0xae502812aa7333L
    k = k xor (k ushr 33)
    k *= -0x3b314601e57a13adL
    k = k xor (k ushr 33)
    return k
  }

  /** Gets a long from a byte buffer in little endian byte order.  */
  private fun getLongLittleEndian(buf: ByteArray, offset: Int): Long {
    return (buf[offset + 7].toLong() shl 56 // no mask needed
      or (buf[offset + 6].toLong() and 0xffL shl 48)
      or (buf[offset + 5].toLong() and 0xffL shl 40)
      or (buf[offset + 4].toLong() and 0xffL shl 32)
      or (buf[offset + 3].toLong() and 0xffL shl 24)
      or (buf[offset + 2].toLong() and 0xffL shl 16)
      or (buf[offset + 1].toLong() and 0xffL shl 8)
      or (buf[offset].toLong() and 0xffL)) // no shift needed
  }

  /** Returns the MurmurHash3_x86_32 hash.  */
  fun murmurHash3_x86_32(data: ByteArray, offset: Int, len: Int, seed: Int): Int {
    val c1 = -0x3361d2af
    val c2 = 0x1b873593
    var h1 = seed
    val roundedEnd = offset + (len and -0x4) // round down to 4 byte block
    var i = offset
    while (i < roundedEnd) {
      // little endian load order
      var k1: Int = data[i].toInt() and 0xff or (data[i + 1].toInt() and 0xff shl 8) or (data[i + 2].toInt() and 0xff shl 16) or (data[i + 3].toInt() shl 24)
      k1 *= c1
      k1 = k1 shl 15 or (k1 ushr 17) // ROTL32(k1,15);
      k1 *= c2
      h1 = h1 xor k1
      h1 = h1 shl 13 or (h1 ushr 19) // ROTL32(h1,13);
      h1 = h1 * 5 + -0x19ab949c
      i += 4
    }
    // tail
    var k1 = 0
    val p = len and 0x03
    if (p >= 3) {
      k1 = data[roundedEnd + 2].toInt() and 0xff shl 16
    }
    if (p >= 2) {
      k1 = k1 or (data[roundedEnd + 1].toInt() and 0xff) shl 8
    }
    if (p >= 1) {
      k1 = k1 or (data[roundedEnd].toInt() and 0xff)
      k1 *= c1
      k1 = k1 shl 15 or (k1 ushr 17)
      k1 *= c2
      h1 = h1 xor k1
    }
    // finalization
    return fmix32(h1 xor len)
  }

  /** Returns the MurmurHash3_x86_32 hash of the UTF-8 bytes of the String without actually encoding
   * the string to a temporary buffer.  This is more than 2x faster than hashing the result
   * of String.getBytes().
   */
  fun murmurHash3_x86_32(data: CharSequence, offset: Int, len: Int, seed: Int): Int {
    val c1 = -0x3361d2af
    val c2 = 0x1b873593
    var h1 = seed
    var pos = offset
    val end = offset + len
    var k1 = 0
    var k2 = 0
    var shift = 0
    var bits = 0
    var nBytes = 0 // length in UTF8 bytes
    while (pos < end) {
      val code = data[pos++].toInt()
      if (code < 0x80) {
        k2 = code
        bits = 8
      } else if (code < 0x800) {
        k2 = (0xC0 or (code shr 6)
          or (0x80 or (code and 0x3F) shl 8))
        bits = 16
      } else if (code < 0xD800 || code > 0xDFFF || pos >= end) {
        // we check for pos>=end to encode an unpaired surrogate as 3 bytes.
        k2 = (0xE0 or (code shr 12)
          or (0x80 or (code shr 6 and 0x3F) shl 8)
          or (0x80 or (code and 0x3F) shl 16))
        bits = 24
      } else {
        // surrogate pair
        // int utf32 = pos < end ? (int) data.charAt(pos++) : 0;
        var utf32 = data[pos++].toInt()
        utf32 = (code - 0xD7C0 shl 10) + (utf32 and 0x3FF)
        k2 = (0xff and (0xF0 or (utf32 shr 18))
          or (0x80 or (utf32 shr 12 and 0x3F) shl 8
          ) or (0x80 or (utf32 shr 6 and 0x3F) shl 16
          ) or (0x80 or (utf32 and 0x3F) shl 24))
        bits = 32
      }
      k1 = k1 or k2 shl shift
      // int used_bits = 32 - shift;  // how many bits of k2 were used in k1.
      // int unused_bits = bits - used_bits; //  (bits-(32-shift)) == bits+shift-32  == bits-newshift
      shift += bits
      if (shift >= 32) {
        // mix after we have a complete word
        k1 *= c1
        k1 = k1 shl 15 or (k1 ushr 17) // ROTL32(k1,15);
        k1 *= c2
        h1 = h1 xor k1
        h1 = h1 shl 13 or (h1 ushr 19) // ROTL32(h1,13);
        h1 = h1 * 5 + -0x19ab949c
        shift -= 32
        // unfortunately, java won't let you shift 32 bits off, so we need to check for 0
        k1 = if (shift != 0) {
          k2 ushr bits - shift // bits used == bits - newshift
        } else {
          0
        }
        nBytes += 4
      }
    }
    // inner
    // handle tail
    if (shift > 0) {
      nBytes += shift shr 3
      k1 *= c1
      k1 = k1 shl 15 or (k1 ushr 17) // ROTL32(k1,15);
      k1 *= c2
      h1 = h1 xor k1
    }
    // finalization
    return fmix32(h1 xor nBytes)
  }

  fun murmurHash3_x64_128(key: ByteArray, offset: Int = 0, len: Int = key.size - offset, seed: Int, out: LongPair = LongPair()): LongPair {
    return murmurHash3_x64_128(key, offset, len, seed.toLong() and 0x00000000FFFFFFFFL, out)
  }

  /** Returns the MurmurHash3_x64_128 hash, placing the result in "out".  */
  fun murmurHash3_x64_128(key: ByteArray, offset: Int = 0, len: Int = key.size - offset, seed: Long = 0, out: LongPair = LongPair()): LongPair {
    // The original algorithm does have a 32 bit unsigned seed.
    // We have to mask to match the behavior of the unsigned types and prevent sign extension.
    var h1 = seed
    var h2 = seed
    val c1 = -0x783c846eeebdac2bL
    val c2 = 0x4cf5ad432745937fL
    val roundedEnd = offset + (len and -0x10) // round down to 16 byte block
    var i = offset
    while (i < roundedEnd) {
      var k1 = getLongLittleEndian(key, i)
      var k2 = getLongLittleEndian(key, i + 8)
      k1 *= c1
      k1 = java.lang.Long.rotateLeft(k1, 31)
      k1 *= c2
      h1 = h1 xor k1
      h1 = java.lang.Long.rotateLeft(h1, 27)
      h1 += h2
      h1 = h1 * 5 + 0x52dce729
      k2 *= c2
      k2 = java.lang.Long.rotateLeft(k2, 33)
      k2 *= c1
      h2 = h2 xor k2
      h2 = java.lang.Long.rotateLeft(h2, 31)
      h2 += h1
      h2 = h2 * 5 + 0x38495ab5
      i += 16
    }
    var k1: Long = 0
    var k2: Long = 0
    val p = len and 15
    if (p >= 15) k2 = key[roundedEnd + 14].toLong() and 0xffL shl 48
    if (p >= 14) k2 = k2 or ((key[roundedEnd + 13].toLong() and 0xffL) shl 40)
    if (p >= 13) k2 = k2 or ((key[roundedEnd + 12].toLong() and 0xffL) shl 32)
    if (p >= 12) k2 = k2 or ((key[roundedEnd + 11].toLong() and 0xffL) shl 24)
    if (p >= 11) k2 = k2 or ((key[roundedEnd + 10].toLong() and 0xffL) shl 16)
    if (p >= 10) k2 = k2 or ((key[roundedEnd + 9].toLong() and 0xffL) shl 8)
    if (p >= 9) {
      k2 = k2 or (key[roundedEnd + 8].toLong() and 0xffL)
      k2 *= c2
      k2 = java.lang.Long.rotateLeft(k2, 33)
      k2 *= c1
      h2 = h2 xor k2
    }
    if (p >= 8) k1 = key[roundedEnd + 7].toLong() shl 56
    if (p >= 7) k1 = k1 or ((key[roundedEnd + 6].toLong() and 0xffL) shl 48)
    if (p >= 6) k1 = k1 or ((key[roundedEnd + 5].toLong() and 0xffL) shl 40)
    if (p >= 5) k1 = k1 or ((key[roundedEnd + 4].toLong() and 0xffL) shl 32)
    if (p >= 4) k1 = k1 or ((key[roundedEnd + 3].toLong() and 0xffL) shl 24)
    if (p >= 3) k1 = k1 or ((key[roundedEnd + 2].toLong() and 0xffL) shl 16)
    if (p >= 2) k1 = k1 or ((key[roundedEnd + 1].toLong() and 0xffL) shl 8)
    if (p >= 1) {
      k1 = k1 or (key[roundedEnd].toLong() and 0xffL)
      k1 *= c1
      k1 = java.lang.Long.rotateLeft(k1, 31)
      k1 *= c2
      h1 = h1 xor k1
    }
    //----------
    // finalization
    h1 = h1 xor len.toLong()
    h2 = h2 xor len.toLong()
    h1 += h2
    h2 += h1
    h1 = fmix64(h1)
    h2 = fmix64(h2)
    h1 += h2
    h2 += h1
    out.val1 = h1
    out.val2 = h2
    return out
  }

  /** 128 bits of state  */
  data class LongPair(var val1: Long = 0, var val2: Long = 0) {
    fun toByteArray(): ByteArray {
      val buffer = HeapByteBuffer(16)
      buffer.put(val1)
      buffer.put(val2)
      return buffer.array
    }
  }
}

fun main() {
  val data = File("build.gradle").readText().toByteArray()
  println(data.toUTF8String())
  val hash = MurmurHash3.murmurHash3_x64_128(data, seed = 100)
  println(hash)
  println(hash.toByteArray().toHexString())
}