package cn.tursom.channel.enhance

import java.io.Closeable

interface EnhanceChannel<Read, Write> : ChannelReader<Read>, ChannelWriter<Write>, Closeable {
  override fun close()
}