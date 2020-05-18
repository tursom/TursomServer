package cn.tursom.forward

import cn.tursom.utils.bytebuffer.NettyByteBuffer
import cn.tursom.core.stream.OutputStream
import io.netty.buffer.ByteBuf

interface Forward : OutputStream {
  var forward: Forward?
  fun write(byteBuf: ByteBuf) = write(NettyByteBuffer(byteBuf))
  override fun flush() {}
}