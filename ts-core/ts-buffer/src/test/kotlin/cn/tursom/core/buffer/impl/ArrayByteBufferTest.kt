package cn.tursom.core.buffer.impl

import cn.tursom.core.ByteBufferUtil
import org.junit.Test

class ArrayByteBufferTest {
  @Test
  fun testGetString() {
    val buffer = ArrayByteBuffer(
      ByteBufferUtil.wrap("hello"),
      ByteBufferUtil.wrap(", "),
      ByteBufferUtil.wrap("world"),
      ByteBufferUtil.wrap("!"),
      HeapByteBuffer(32),
      ByteBufferUtil.wrap("!"),
      HeapByteBuffer(32),
    )
    buffer.put(" I'm tursom, this lib's author")
    buffer.put('.'.code.toByte())
    buffer.writePosition = buffer.capacity
    println(buffer)
    println(buffer.getString())
    println(buffer)
  }

  @Test
  fun testReset() {
    val buffer = ArrayByteBuffer(
      ByteBufferUtil.wrap("hello"),
      HeapByteBuffer(4),
      ByteBufferUtil.wrap(", "),
      HeapByteBuffer(4),
      ByteBufferUtil.wrap("world"),
      ByteBufferUtil.wrap("!"),
      ByteBufferUtil.wrap("!"),
    )
    buffer.reset()
    println(buffer)
    val message = buffer.getString()
    println(message)
    println(buffer)
  }
}