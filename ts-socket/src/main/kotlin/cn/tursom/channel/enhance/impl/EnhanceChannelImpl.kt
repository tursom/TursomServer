package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.channel.enhance.EnhanceChannel

open class EnhanceChannelImpl<Read, Write>(
  override val reader: ChannelReader<Read>,
  override val writer: ChannelWriter<Write>
) : EnhanceChannel<Read, Write>, ChannelReader<Read> by reader, ChannelWriter<Write> by writer {
  override fun close() {
    reader.close()
    writer.close()
  }
}