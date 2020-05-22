package cn.tursom.channel.enhance

import java.io.Closeable

interface EnhanceChannel<Read, Write> : ChannelReader<Read>, ChannelWriter<Write>, Closeable {
  val reader: ChannelReader<Read>
  val writer: ChannelWriter<Write>
  override fun close()
}