package cn.tursom.socket

import cn.tursom.core.buffer.write
import cn.tursom.socket.niothread.INioThread
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

interface IAsyncNioSocket : AsyncSocket {
  val channel: SocketChannel
  val key: SelectionKey
  val nioThread: INioThread

  fun waitMode() {
    if (Thread.currentThread() == nioThread.thread) {
      if (key.isValid) key.interestOps(SelectionKey.OP_WRITE)
    } else {
      nioThread.execute { if (key.isValid) key.interestOps(0) }
      nioThread.wakeup()
    }
  }

  fun readMode() {
    //logE("readMode()")
    if (Thread.currentThread() == nioThread.thread) {
      if (key.isValid) key.interestOps(SelectionKey.OP_WRITE)
    } else {
      nioThread.execute {
        //logE("readMode() interest")
        if (key.isValid) key.interestOps(SelectionKey.OP_READ)
        //logE("readMode interestOps ${key.isValid} ${key.interestOps()}")
      }
      nioThread.wakeup()
    }
  }

  fun writeMode() {
    if (Thread.currentThread() == nioThread.thread) {
      if (key.isValid) key.interestOps(SelectionKey.OP_WRITE)
    } else {
      nioThread.execute { if (key.isValid) key.interestOps(SelectionKey.OP_WRITE) }
      nioThread.wakeup()
    }
  }

  suspend fun read(buffer: ByteBuffer): Int = read(arrayOf(buffer)).toInt()
  suspend fun write(buffer: ByteBuffer): Int = write(arrayOf(buffer)).toInt()
  suspend fun read(buffer: Array<out ByteBuffer>): Long
  suspend fun write(buffer: Array<out ByteBuffer>): Long
  /**
   * 如果通道已断开则会抛出异常
   */
  suspend fun recv(buffer: ByteBuffer): Int {
    if (buffer.remaining() == 0) return emptyBufferCode
    val readSize = read(buffer)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }

  suspend fun recv(buffer: ByteBuffer, timeout: Long): Int {
    if (buffer.remaining() == 0) return emptyBufferCode
    val readSize = read(buffer, timeout)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }

  suspend fun recv(buffers: Array<out ByteBuffer>, timeout: Long): Long {
    if (buffers.isEmpty()) return emptyBufferLongCode
    val readSize = read(buffers, timeout)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }

  suspend fun recv(buffer: cn.tursom.core.buffer.ByteBuffer, timeout: Long = 0): Int {
    return buffer.write {
      recv(it, timeout)
    }
  }

  companion object {
    const val emptyBufferCode = 0
    const val emptyBufferLongCode = 0L
  }
}