package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.core.buffer.ByteBuffer

class LengthFieldChannel(
  reader: ChannelReader<ByteBuffer>,
  writer: ChannelWriter<ByteBuffer>
) : EnhanceChannelImpl<ByteBuffer, ByteBuffer>(
  LengthFieldBasedFrameReader(reader),
  LengthFieldPrependWriter(writer)
)