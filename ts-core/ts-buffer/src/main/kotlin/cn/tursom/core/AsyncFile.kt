package cn.tursom.core

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Suppress("MemberVisibilityCanBePrivate")
class AsyncFile(val path: Path) {
  constructor(path: String) : this(Paths.get(path))

  private var existsCache = false

  val exists: Boolean
    get() {
      val exists = Files.exists(path)
      existsCache = exists
      return exists
    }
  val size get() = if (existsCache || exists) Files.size(path) else 0

  val writeChannel: AsynchronousFileChannel by lazy { AsynchronousFileChannel.open(path, StandardOpenOption.WRITE) }
  val readChannel: AsynchronousFileChannel by lazy { AsynchronousFileChannel.open(path, StandardOpenOption.READ) }

  fun write(buffer: ByteBuffer, position: Long = 0) {
    create()
    buffer.read { writeChannel.write(it, position) }
  }

  suspend fun writeAndWait(buffer: ByteBuffer, position: Long = 0): Int {
    create()
    return suspendCoroutine { cont ->
      buffer.read { writeChannel.write(it, position, cont, handler) }
    }
  }

  fun append(buffer: ByteBuffer, position: Long = size) {
    write(buffer, position)
  }

  suspend fun appendAndWait(buffer: ByteBuffer, position: Long = size): Int {
    return writeAndWait(buffer, position)
  }

  suspend fun read(buffer: ByteBuffer, position: Long = 0): Int {
    return suspendCoroutine { cont ->
      buffer.write { readChannel.read(it, position, cont, handler) }
    }
  }

  fun create() = if (existsCache || !exists) {
    Files.createFile(path)
    existsCache = true
    true
  } else {
    false
  }

  fun delete(): Boolean {
    existsCache = false
    return Files.deleteIfExists(path)
  }

  fun close() {
    try {
      writeChannel.close()
      readChannel.close()
    } catch (e: Exception) {
    }
  }

  companion object {
    @JvmStatic
    val handler = object : CompletionHandler<Int, Continuation<Int>> {
      override fun completed(result: Int, attachment: Continuation<Int>) = attachment.resume(result)
      override fun failed(exc: Throwable, attachment: Continuation<Int>) = attachment.resumeWithException(exc)
    }
  }
}