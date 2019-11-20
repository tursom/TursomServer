package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.EnhanceSocket
import cn.tursom.socket.enhance.SocketReader
import cn.tursom.socket.enhance.SocketWriter

class StringSocket(
  socket: IAsyncNioSocket,
  prevReader: SocketReader<ByteBuffer> = LengthFieldBasedFrameReader(socket),
  prevWriter: SocketWriter<ByteBuffer> = LengthFieldPrependWriter(socket)
) : EnhanceSocket<String, String> by UnionEnhanceSocket(
  socket,
  StringReader(prevReader),
  StringWriter(prevWriter)
)