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


@Suppress("MemberVisibilityCanBePrivate", "unused", "DuplicatedCode")
class AsyncFile(val path: Path) {
  constructor(path: String) : this(Paths.get(path))

  interface Writer {
    suspend fun writeAsyncFile(file: AsyncFile, position: Long): Int
  }

  interface Reader {
    suspend fun readAsyncFile(file: AsyncFile, position: Long): Int
  }

  private var existsCache = exists

  val exists: Boolean
    get() {
      val exists = Files.exists(path)
      existsCache = exists
      return exists
    }

  val size get() = if (existsCache || exists) Files.size(path) else 0

  var writePosition: Long = 0
  var readPosition: Long = 0
  val writeChannel: AsynchronousFileChannel by lazy { AsynchronousFileChannel.open(path, StandardOpenOption.WRITE) }
  val readChannel: AsynchronousFileChannel by lazy { AsynchronousFileChannel.open(path, StandardOpenOption.READ) }

  suspend fun writeAndWait(buffer: ByteBuffer, position: Long = writePosition): Int {
    val writeSize = if (buffer is Writer) {
      buffer.writeAsyncFile(this, position)
    } else buffer.read {
      suspendCoroutine { cont ->
        writeChannel.write(it, position, cont, handler)
      }
    }
    writePosition += writeSize
    return writeSize
  }

  suspend fun appendAndWait(buffer: ByteBuffer, position: Long = size): Int {
    return writeAndWait(buffer, position)
  }

  suspend fun read(buffer: ByteBuffer, position: Long = readPosition): Int {
    val readSize = if (buffer is Reader) {
      buffer.readAsyncFile(this, position)
    } else buffer.write {
      suspendCoroutine { cont ->
        readChannel.read(it, position, cont, handler)
      }
    }
    readPosition += readSize
    return readSize
  }

  fun create() = if (!existsCache || !exists) {
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

    @JvmStatic
    val writePositionHandler = object : CompletionHandler<Int, AsyncFile> {
      override fun completed(result: Int, attachment: AsyncFile) {
        attachment.writePosition += result
      }

      override fun failed(exc: Throwable, attachment: AsyncFile) {
      }
    }

    @JvmStatic
    val readPositionHandler = object : CompletionHandler<Int, AsyncFile> {
      override fun completed(result: Int, attachment: AsyncFile) {
        attachment.readPosition += result
      }

      override fun failed(exc: Throwable, attachment: AsyncFile) {
      }
    }
  }
}