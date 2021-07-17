package cn.tursom.core.buffer


object NioBuffers {
  inline fun <T> Array<out ByteBuffer>.readNioBuffers(
    action: (bufferList: List<java.nio.ByteBuffer>) -> T
  ): T {
    val bufferList = getReadNioBufferList()
    try {
      return action(bufferList)
    } finally {
      finishRead(bufferList.iterator())
    }
  }

  inline fun <T> Sequence<ByteBuffer>.readNioBuffers(
    action: (bufferList: List<java.nio.ByteBuffer>) -> T
  ): T {
    val bufferList = getReadNioBufferList()
    try {
      return action(bufferList)
    } finally {
      finishRead(bufferList.iterator())
    }
  }

  inline fun <T> Iterable<ByteBuffer>.readNioBuffers(
    action: (bufferList: List<java.nio.ByteBuffer>) -> T
  ): T {
    val bufferList = getReadNioBufferList()
    try {
      return action(bufferList)
    } finally {
      finishRead(bufferList.iterator())
    }
  }

  inline fun <T> Array<out ByteBuffer>.writeNioBuffers(
    action: (bufferList: List<java.nio.ByteBuffer>) -> T
  ): T {
    val bufferList = getWriteNioBufferList()
    try {
      return action(bufferList)
    } finally {
      finishWrite(bufferList.iterator())
    }
  }

  inline fun <T> Sequence<ByteBuffer>.writeNioBuffers(
    action: (bufferList: List<java.nio.ByteBuffer>) -> T
  ): T {
    val bufferList = getWriteNioBufferList()
    try {
      return action(bufferList)
    } finally {
      finishWrite(bufferList.iterator())
    }
  }

  inline fun <T> Iterable<ByteBuffer>.writeNioBuffers(
    action: (bufferList: List<java.nio.ByteBuffer>) -> T
  ): T {
    val bufferList = getWriteNioBufferList()
    try {
      return action(bufferList)
    } finally {
      finishWrite(bufferList.iterator())
    }
  }

  fun Array<out ByteBuffer>.getReadNioBufferList(): List<java.nio.ByteBuffer> = iterator().getReadNioBufferList()
  fun Sequence<ByteBuffer>.getReadNioBufferList(): List<java.nio.ByteBuffer> = iterator().getReadNioBufferList()
  fun Iterable<ByteBuffer>.getReadNioBufferList(): List<java.nio.ByteBuffer> = iterator().getReadNioBufferList()
  fun Iterator<ByteBuffer>.getReadNioBufferList(): List<java.nio.ByteBuffer> {
    val bufferList = ArrayList<java.nio.ByteBuffer>()
    forEach {
      val nioBuffersList = it.getExtension(Sequences)
      if (nioBuffersList != null) {
        bufferList.addAll(nioBuffersList.readBufferSequence())
        return@forEach
      }

      bufferList.add(it.readBuffer())
    }

    return bufferList
  }

  fun Array<out ByteBuffer>.finishRead(iterator: Iterator<java.nio.ByteBuffer>) = iterator().finishRead(iterator)
  fun Sequence<ByteBuffer>.finishRead(iterator: Iterator<java.nio.ByteBuffer>) = iterator().finishRead(iterator)
  fun Iterable<ByteBuffer>.finishRead(iterator: Iterator<java.nio.ByteBuffer>) = iterator().finishRead(iterator)
  fun Iterator<ByteBuffer>.finishRead(iterator: Iterator<java.nio.ByteBuffer>) {
    forEach {
      val nioBuffersList = it.getExtension(Sequences)
      if (nioBuffersList != null) {
        nioBuffersList.finishRead(iterator)
        return@forEach
      }

      it.finishRead(iterator.next())
    }
  }

  fun Array<out ByteBuffer>.getWriteNioBufferList(): List<java.nio.ByteBuffer> = iterator().getWriteNioBufferList()
  fun Sequence<ByteBuffer>.getWriteNioBufferList(): List<java.nio.ByteBuffer> = iterator().getWriteNioBufferList()
  fun Iterable<ByteBuffer>.getWriteNioBufferList(): List<java.nio.ByteBuffer> = iterator().getWriteNioBufferList()
  fun Iterator<ByteBuffer>.getWriteNioBufferList(): List<java.nio.ByteBuffer> {
    val bufferList = ArrayList<java.nio.ByteBuffer>()
    forEach {
      val nioBuffersList = it.getExtension(Sequences)
      if (nioBuffersList != null) {
        bufferList.addAll(nioBuffersList.writeBufferSequence())
        return@forEach
      }

      bufferList.add(it.writeBuffer())
    }

    return bufferList
  }

  fun Array<out ByteBuffer>.finishWrite(iterator: Iterator<java.nio.ByteBuffer>) = iterator().finishWrite(iterator)
  fun Sequence<ByteBuffer>.finishWrite(iterator: Iterator<java.nio.ByteBuffer>) = iterator().finishWrite(iterator)
  fun Iterable<ByteBuffer>.finishWrite(iterator: Iterator<java.nio.ByteBuffer>) = iterator().finishWrite(iterator)
  fun Iterator<ByteBuffer>.finishWrite(iterator: Iterator<java.nio.ByteBuffer>) {
    forEach {
      val nioBuffersList = it.getExtension(Sequences)
      if (nioBuffersList != null) {
        nioBuffersList.finishWrite(iterator)
        return@forEach
      }

      it.finishRead(iterator.next())
    }
  }

  interface Sequences {
    companion object : ByteBufferExtensionKey<Sequences> {
      override val extensionClass: Class<Sequences> = Sequences::class.java

      inline fun <T> Sequences.readSequences(action: (nioBuffers: Sequence<java.nio.ByteBuffer>) -> T): T {
        val sequence = readBufferSequence()
        val ret = action(sequence)
        finishRead(sequence.iterator())
        return ret
      }

      inline fun <T> Sequences.writeSequences(action: (nioBuffers: Sequence<java.nio.ByteBuffer>) -> T): T {
        val sequence = writeBufferSequence()
        val ret = action(sequence)
        finishWrite(sequence.iterator())
        return ret
      }
    }

    fun readBufferSequence(): Sequence<java.nio.ByteBuffer>
    fun finishRead(buffers: Iterator<java.nio.ByteBuffer>)

    fun writeBufferSequence(): Sequence<java.nio.ByteBuffer>
    fun finishWrite(buffers: Iterator<java.nio.ByteBuffer>)
  }

  interface Lists : Sequences {
    companion object : ByteBufferExtensionKey<Lists> {
      override val extensionClass: Class<Lists> = Lists::class.java

      inline fun <T> Lists.readLists(action: (nioBuffers: List<java.nio.ByteBuffer>) -> T): T {
        val list = readBufferList()
        val ret = action(list)
        finishRead(list.iterator())
        return ret
      }

      inline fun <T> Lists.writeLists(action: (nioBuffers: List<java.nio.ByteBuffer>) -> T): T {
        val list = writeBufferList()
        val ret = action(list)
        finishWrite(list.iterator())
        return ret
      }
    }

    override fun readBufferSequence(): Sequence<java.nio.ByteBuffer> = readBufferList().asSequence()
    override fun writeBufferSequence(): Sequence<java.nio.ByteBuffer> = writeBufferList().asSequence()

    fun readBufferList(): List<java.nio.ByteBuffer>
    override fun finishRead(buffers: Iterator<java.nio.ByteBuffer>)

    fun writeBufferList(): List<java.nio.ByteBuffer>
    override fun finishWrite(buffers: Iterator<java.nio.ByteBuffer>)
  }

  interface Arrays : Lists {
    companion object : ByteBufferExtensionKey<Arrays> {
      override val extensionClass: Class<Arrays> = Arrays::class.java

      inline fun <T> Arrays.readArrays(action: (nioBuffers: Array<out java.nio.ByteBuffer>) -> T): T {
        val arrayOfByteBuffers = readBufferArray()
        val ret = action(arrayOfByteBuffers)
        finishRead(arrayOfByteBuffers.iterator())
        return ret
      }

      inline fun <T> Arrays.writeArrays(action: (nioBuffers: Array<out java.nio.ByteBuffer>) -> T): T {
        val arrayOfByteBuffers = writeBufferArray()
        val ret = action(arrayOfByteBuffers)
        finishWrite(arrayOfByteBuffers.iterator())
        return ret
      }
    }

    override fun readBufferSequence(): Sequence<java.nio.ByteBuffer> = readBufferArray().asSequence()
    override fun writeBufferSequence(): Sequence<java.nio.ByteBuffer> = writeBufferArray().asSequence()

    override fun readBufferList(): List<java.nio.ByteBuffer> = readBufferArray().asList()
    override fun writeBufferList(): List<java.nio.ByteBuffer> = writeBufferArray().asList()

    fun readBufferArray(): Array<out java.nio.ByteBuffer>
    override fun finishRead(buffers: Iterator<java.nio.ByteBuffer>)

    fun writeBufferArray(): Array<out java.nio.ByteBuffer>
    override fun finishWrite(buffers: Iterator<java.nio.ByteBuffer>)
  }
}