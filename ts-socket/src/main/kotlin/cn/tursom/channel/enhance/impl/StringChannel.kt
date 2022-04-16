package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.channel.enhance.EnhanceChannel
import cn.tursom.core.buffer.ByteBuffer

class StringChannel(
  prevReader: ChannelReader<ByteBuffer>,
  prevWriter: ChannelWriter<ByteBuffer>,
) : EnhanceChannelImpl<String, String>(
  StringReader(prevReader),
  StringWriter(prevWriter)
) {
  constructor(enhanceChannel: EnhanceChannel<ByteBuffer, ByteBuffer>) : this(
    enhanceChannel.reader,
    enhanceChannel.writer
  )
}