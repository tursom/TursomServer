package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.IOStream
import cn.tursom.core.stream.SuspendInputStream

class ByteBufferIOStream(
  private val buffer: ByteBuffer
) : IOStream, SuspendInputStream {
  @Volatile
  private var handler: (() -> Unit)? = null

  override fun skip(n: Long, handler: () -> Unit) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun skip(n: Long) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(handler: (Int) -> Unit) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(buffer: ByteArray, handler: () -> Unit) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(buffer: ByteArray, offset: Int, len: Int, handler: () -> Unit) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(buffer: ByteBuffer, handler: () -> Unit) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(): Int {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(buffer: ByteArray) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(buffer: ByteArray, offset: Int, len: Int) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(buffer: ByteBuffer) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun close() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun write(byte: Byte) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun write(buffer: ByteArray) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun write(buffer: ByteArray, offset: Int, len: Int) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun write(buffer: ByteBuffer) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun flush() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}