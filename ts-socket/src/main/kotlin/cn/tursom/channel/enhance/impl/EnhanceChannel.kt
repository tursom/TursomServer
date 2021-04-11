package cn.tursom.channel.enhance.impl

import cn.tursom.channel.AsyncChannel
import cn.tursom.core.buffer.ByteBuffer

class EnhanceChannel<Channel : AsyncChannel>(
  val channel: Channel
) : EnhanceChannelImpl<ByteBuffer, ByteBuffer>(
  ChannelReaderImpl(channel),
  ChannelWriterImpl(channel)
)