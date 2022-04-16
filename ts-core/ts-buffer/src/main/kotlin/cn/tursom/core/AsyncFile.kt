package cn.tursom.core

import cn.tursom.core.AsyncFile.Reader
import cn.tursom.core.AsyncFile.Writer
import cn.tursom.core.buffer.*
import cn.tursom.core.buffer.NioBuffers.readNioBuffers
import cn.tursom.core.buffer.NioBuffers.writeNioBuffers
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

  fun interface Writer {
    companion object : ByteBufferExtensionKey<Writer> {
      override val extensionClass: Class<Writer> = Writer::class.java

      override tailrec fun get(buffer: ByteBuffer): Writer? {
        return when {
          buffer is MultipleByteBuffer -> Writer { file, position ->
            var writePosition = position
            run {
              buffer.readNioBuffers {
                it.forEach { readBuf ->
                  while (readBuf.position() < readBuf.limit()) {
                    val writeSize = file.write(readBuf, writePosition)
                    if (writeSize > 0) {
                      writePosition += writeSize
                    } else {
                      return@run
                    }
                  }
                }
              }
            }
            (writePosition - position).toInt()
          }
          buffer is ProxyByteBuffer -> get(buffer.agent)
          else -> null
        }
      }
    }

    suspend fun writeAsyncFile(file: AsyncFile, position: Long): Int
  }

  fun interface Reader {
    companion object : ByteBufferExtensionKey<Reader> {
      override val extensionClass: Class<Reader> = Reader::class.java

      override tailrec fun get(buffer: ByteBuffer): Reader? {
        return when {
          buffer is MultipleByteBuffer -> Reader { file, position ->
            var readPosition = position
            run {
              buffer.writeNioBuffers {
                it.forEach { nioBuf ->
                  while (nioBuf.position() < nioBuf.limit()) {
                    val readSize = file.read(nioBuf, readPosition)
                    if (readSize > 0) {
                      readPosition += readSize
                    } else {
                      return@run
                    }
                  }
                }
              }
            }
            (readPosition - position).toInt()
          }
          buffer is ProxyByteBuffer -> get(buffer.agent)
          else -> null
        }
      }
    }

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
    val writeSize = buffer.getExtension(Writer)?.writeAsyncFile(this, position)
      ?: buffer.read {
        write(it, position)
      }
    writePosition += writeSize
    return writeSize
  }

  private suspend fun write(nioBuf: java.nio.ByteBuffer, position: Long): Int = suspendCoroutine { cont ->
    writeChannel.write(nioBuf, position, cont, handler)
  }

  suspend fun appendAndWait(buffer: ByteBuffer, position: Long = size): Int {
    return writeAndWait(buffer, position)
  }

  suspend fun read(buffer: ByteBuffer, position: Long = readPosition): Int {
    val readSize = buffer.getExtension(Reader)?.readAsyncFile(this, position)
      ?: buffer.write {
        read(it, position)
      }
    readPosition += readSize
    return readSize
  }

  private suspend fun read(nioBuf: java.nio.ByteBuffer, position: Long): Int = suspendCoroutine { cont ->
    readChannel.read(nioBuf, position, cont, handler)
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