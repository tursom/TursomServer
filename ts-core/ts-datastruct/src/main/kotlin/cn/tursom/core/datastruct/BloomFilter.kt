package cn.tursom.core.datastruct

import cn.tursom.core.hash.MurmurHash3
import cn.tursom.core.toByteArray
import kotlin.math.pow

@Suppress("MemberVisibilityCanBePrivate")
class BloomFilter(val hash: Hash = murmur3Hash, val bitSize: Long, val hashCount: Int = 5) {
  private val bitSet = ArrayBitSet(bitSize)

  constructor(
    valueCount: Long = 1000000,
    hashCount: Int = 5,
    wrongChance: Double = 0.03,
    hash: Hash = murmur3Hash
  ) : this(
    hash,
    wrongChance.run {
      assert(wrongChance in 0.0..1.0)
      (1 / (
        1 - (
          1 - wrongChance.pow(1.0 / hashCount)
          ).pow(1.0 / valueCount / hashCount)
        )).toLong()
    },
    hashCount
  )

  fun add(obj: Any) {
    when (obj) {
      is ByteArray -> add(obj)
      is String -> add(obj)
      else -> add(obj.hashCode().toByteArray())
    }
  }

  fun add(str: String) = add(str.toByteArray())

  fun add(bytes: ByteArray) {
    val hash = hash.hash(bytes)
    repeat(hashCount) {
      val index = hash.next() % bitSize
      bitSet.up(index)
    }
  }

  fun mightContain(obj: Any): Boolean {
    return when (obj) {
      is ByteArray -> mightContain(obj)
      is String -> mightContain(obj)
      else -> mightContain(obj.hashCode().toByteArray())
    }
  }

  fun mightContain(str: String): Boolean = mightContain(str.toByteArray())

  fun mightContain(bytes: ByteArray): Boolean {
    val hash = hash.hash(bytes)
    repeat(hashCount) {
      val index = hash.next() % bitSize
      if (bitSet[index].not()) {
        return false
      }
    }
    return true
  }

  interface Hash {
    fun hash(src: ByteArray): HashContent
  }

  interface HashContent {
    fun next(): Long
  }

  override fun toString(): String {
    return "BloomFilter(hash=$hash, bitSize=$bitSize, hashCount=$hashCount, bitSet=$bitSet)"
  }

  companion object {
    val murmur3Hash = object : Hash {
      override fun hash(src: ByteArray): HashContent {
        val hash = MurmurHash3.murmurHash3_x64_128(src)
        return object : HashContent {
          var h1 = hash.val1
          val h2 = hash.val2
          override fun next(): Long {
            h1 += h2
            return h1 and Long.MAX_VALUE
          }
        }
      }

      override fun toString(): String = "Murmur3Hash"
    }

    fun need(valueCount: Long, wrongChance: Double = 0.03, hashCount: Int = 5) = (1 / (
      1 - (
        1 - wrongChance.pow(1.0 / hashCount)
        ).pow(1.0 / valueCount / hashCount)
      ))
  }
}