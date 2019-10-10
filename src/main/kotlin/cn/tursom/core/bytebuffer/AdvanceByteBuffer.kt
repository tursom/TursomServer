package cn.tursom.core.bytebuffer

import cn.tursom.core.forEachIndex
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.OutputStream
import java.nio.ByteBuffer
import kotlin.math.min

interface AdvanceByteBuffer {
    val nioBuffer: ByteBuffer
    val nioBuffers: Array<out ByteBuffer> get() = arrayOf(nioBuffer)

    /**
     * 各种位置变量
     */
    val readOnly: Boolean
    val bufferCount: Int get() = 1

    var writePosition: Int
    var limit: Int
    val capacity: Int
    val hasArray: Boolean
    val array: ByteArray
    val arrayOffset: Int
    var readPosition: Int
    val readOffset: Int get() = arrayOffset + readPosition
    val readableSize: Int
    val available: Int get() = readableSize
    val writeOffset: Int get() = arrayOffset + writePosition
    val writeableSize: Int get() = limit - writePosition
    val size: Int
    val readMode: Boolean


    fun readMode()
    fun resumeWriteMode(usedSize: Int = 0)

    fun needReadSize(size: Int) {
        if (readableSize < size) throw OutOfBufferException()
    }

    fun useReadSize(size: Int): Int {
        needReadSize(size)
        readPosition += size
        return size
    }

    fun take(size: Int): Int {
        needReadSize(size)
        val offset = readOffset
        readPosition += size
        return offset
    }

    fun push(size: Int): Int {
        val offset = writeOffset
        writePosition += size
        return offset
    }

    fun readAllSize() = useReadSize(readableSize)
    fun takeAll() = take(readableSize)

    fun clear()

    fun reset() {
        if (hasArray) {
            array.copyInto(array, arrayOffset, readOffset, arrayOffset + writePosition)
            writePosition = readableSize
            readPosition = 0
        } else {
            readMode()
            nioBuffer.compact()
            val writePosition = readPosition
            resumeWriteMode()
            readPosition = 0
            this.writePosition = writePosition
        }
    }

    fun reset(outputStream: OutputStream) {
        if (hasArray) {
            outputStream.write(array, readOffset, arrayOffset + writePosition)
        } else {
            outputStream.write(getBytes())
        }
        writePosition = 0
        readPosition = 0
    }

    fun requireAvailableSize(size: Int) {
        if (limit - readPosition < size) reset()
    }


    /*
     * 数据获取方法
     */


    fun get(): Byte = if (readMode) {
        nioBuffer.get()
    } else {
        readMode()
        val value = nioBuffer.get()
        resumeWriteMode()
        value
    }

    fun getChar(): Char = if (readMode) {
        nioBuffer.char
    } else {
        readMode()
        val value = nioBuffer.char
        resumeWriteMode()
        value
    }

    fun getShort(): Short = if (readMode) {
        nioBuffer.short
    } else {
        readMode()
        val value = nioBuffer.short
        resumeWriteMode()
        value
    }

    fun getInt(): Int = if (readMode) {
        nioBuffer.int
    } else {
        readMode()
        val value = nioBuffer.int
        resumeWriteMode()
        value
    }

    fun getLong(): Long = if (readMode) {
        nioBuffer.long
    } else {
        readMode()
        val value = nioBuffer.long
        resumeWriteMode()
        value
    }

    fun getFloat(): Float = if (readMode) {
        nioBuffer.float
    } else {
        readMode()
        val value = nioBuffer.float
        resumeWriteMode()
        value
    }

    fun getDouble(): Double = if (readMode) {
        nioBuffer.double
    } else {
        readMode()
        val value = nioBuffer.double
        resumeWriteMode()
        value
    }


    fun getBytes(): ByteArray = if (readMode) {
        val bytes = ByteArray(readableSize)
        nioBuffer.get(bytes)
        readPosition = writePosition
        bytes
    } else {
        readMode()
        val bytes = ByteArray(readableSize)
        nioBuffer.get(bytes)
        readPosition = writePosition
        resumeWriteMode()
        bytes
    }


    fun getString(size: Int = readableSize): String = String(getBytes())
    fun toString(size: Int): String {
        val rp = readPosition
        val bytes = getBytes()
        readPosition = rp
        return String(bytes)
    }

    fun writeTo(buffer: ByteArray, bufferOffset: Int = 0, size: Int = min(readableSize, buffer.size)): Int {
        val readSize = min(readableSize, size)
        if (hasArray) {
            array.copyInto(buffer, bufferOffset, readOffset, readOffset + readSize)
            readPosition += readOffset
            reset()
        } else {
            readBuffer {
                it.put(buffer, bufferOffset, readSize)
            }
        }
        return readSize
    }

    fun writeTo(os: OutputStream): Int {
        val size = readableSize
        if (hasArray) {
            os.write(array, arrayOffset + readPosition, size)
            readPosition += size
            reset()
        } else {
            val buffer = ByteArray(1024)
            readBuffer {
                while (it.remaining() > 0) {
                    it.put(buffer)
                    os.write(buffer)
                }
            }
        }
        return size
    }

    fun writeTo(buffer: AdvanceByteBuffer): Int {
        val size = min(readableSize, buffer.writeableSize)
        if (hasArray && buffer.hasArray) {
            array.copyInto(buffer.array, buffer.writeOffset, readOffset, readOffset + size)
            buffer.writePosition += size
            readPosition += size
            reset()
        } else {
            readBuffer {
                buffer.nioBuffer.put(it)
            }
        }
        return size
    }

    fun toByteArray() = getBytes()


    /*
     * 数据写入方法
     */

    fun put(byte: Byte) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.put(byte)
            readMode()
        } else {
            nioBuffer.put(byte)
        }
    }

    fun put(char: Char) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.putChar(char)
            readMode()
        } else {
            nioBuffer.putChar(char)
        }
    }

    fun put(short: Short) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.putShort(short)
            readMode()
        } else {
            nioBuffer.putShort(short)
        }
    }

    fun put(int: Int) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.putInt(int)
            readMode()
        } else {
            nioBuffer.putInt(int)
        }
    }

    fun put(long: Long) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.putLong(long)
            readMode()
        } else {
            nioBuffer.putLong(long)
        }
    }

    fun put(float: Float) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.putFloat(float)
            readMode()
        } else {
            nioBuffer.putFloat(float)
        }
    }

    fun put(double: Double) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.putDouble(double)
            readMode()
        } else {
            nioBuffer.putDouble(double)
        }
    }

    fun put(str: String) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.put(str.toByteArray())
            readMode()
        } else {
            nioBuffer.put(str.toByteArray())
        }
    }

    fun put(byteArray: ByteArray, startIndex: Int = 0, endIndex: Int = byteArray.size) {
        if (readMode) {
            resumeWriteMode()
            nioBuffer.put(byteArray, startIndex, endIndex - startIndex)
            readMode()
        } else {
            nioBuffer.put(byteArray, startIndex, endIndex - startIndex)
        }
    }

    fun put(array: CharArray, index: Int = 0, size: Int = array.size - index) {
        if (readMode) {
            resumeWriteMode()
            array.forEachIndex(index, index + size - 1, this::put)
            readMode()
        } else {
            array.forEachIndex(index, index + size - 1, this::put)
        }
    }

    fun put(array: ShortArray, index: Int = 0, size: Int = array.size - index) {
        if (readMode) {
            resumeWriteMode()
            array.forEachIndex(index, index + size - 1, this::put)
            readMode()
        } else {
            array.forEachIndex(index, index + size - 1, this::put)
        }
    }

    fun put(array: IntArray, index: Int = 0, size: Int = array.size - index) {
        if (readMode) {
            resumeWriteMode()
            array.forEachIndex(index, index + size - 1, this::put)
            readMode()
        } else {
            array.forEachIndex(index, index + size - 1, this::put)
        }
    }

    fun put(array: LongArray, index: Int = 0, size: Int = array.size - index) {
        if (readMode) {
            resumeWriteMode()
            array.forEachIndex(index, index + size - 1, this::put)
            readMode()
        } else {
            array.forEachIndex(index, index + size - 1, this::put)
        }
    }

    fun put(array: FloatArray, index: Int = 0, size: Int = array.size - index) {
        if (readMode) {
            resumeWriteMode()
            array.forEachIndex(index, index + size - 1, this::put)
            readMode()
        } else {
            array.forEachIndex(index, index + size - 1, this::put)
        }
    }

    fun put(array: DoubleArray, index: Int = 0, size: Int = array.size - index) {
        if (readMode) {
            resumeWriteMode()
            array.forEachIndex(index, index + size - 1, this::put)
            readMode()
        } else {
            array.forEachIndex(index, index + size - 1, this::put)
        }
    }

    fun peekString(size: Int = readableSize): String {
        val readP = readPosition
        val str = getString(size)
        readPosition = readP
        return str
    }

    fun <T> readBuffer(action: (nioBuffer: ByteBuffer) -> T): T = readNioBuffer(action)
    fun <T> writeBuffer(action: (nioBuffer: ByteBuffer) -> T): T = writeNioBuffer(action)

    suspend fun <T> readSuspendBuffer(action: suspend (nioBuffer: ByteBuffer) -> T): T = readNioBuffer { action(it) }
    suspend fun <T> writeSuspendBuffer(action: suspend (nioBuffer: ByteBuffer) -> T): T = writeNioBuffer { action(it) }

    fun split(from: Int = readPosition, to: Int = writePosition): AdvanceByteBuffer {
        return if (hasArray) {
            ByteArrayAdvanceByteBuffer(array, arrayOffset + readPosition, to - from)
        } else {
            throw NotImplementedException()
        }
    }
}

inline fun <T> AdvanceByteBuffer.readNioBuffer(action: (nioBuffer: ByteBuffer) -> T): T {
    readMode()
    val buffer = nioBuffer
    val position = nioBuffer.position()
    return try {
        action(buffer)
    } finally {
        resumeWriteMode(buffer.position() - position)
    }
}

inline fun <T> AdvanceByteBuffer.writeNioBuffer(action: (nioBuffer: ByteBuffer) -> T): T {
    val buffer = nioBuffer
    val position = writePosition
    val bufferPosition = nioBuffer.position()
    return try {
        action(buffer)
    } finally {
        writePosition = position + (buffer.position() - bufferPosition)
    }
}

