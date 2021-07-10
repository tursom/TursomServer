package cn.tursom.channel

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.channel.enhance.EnhanceChannel
import cn.tursom.channel.enhance.impl.*
import cn.tursom.core.buffer.ByteBuffer

fun AsyncChannel.reader(): ChannelReader<ByteBuffer> = ChannelReaderImpl(this)
fun AsyncChannel.writer(): ChannelWriter<ByteBuffer> = ChannelWriterImpl(this)
fun AsyncChannel.enhance(): EnhanceChannel<ByteBuffer, ByteBuffer> = EnhanceChannel(this)

fun ChannelReader<ByteBuffer>.lengthField(): ChannelReader<ByteBuffer> = LengthFieldBasedFrameReader(this)
fun ChannelWriter<ByteBuffer>.lengthField(): ChannelWriter<ByteBuffer> = LengthFieldPrependWriter(this)
fun EnhanceChannel<ByteBuffer, ByteBuffer>.lengthField(): EnhanceChannel<ByteBuffer, ByteBuffer> =
  LengthFieldChannel(this)

fun ChannelReader<ByteBuffer>.string() = StringReader(this)
fun ChannelWriter<ByteBuffer>.string() = StringWriter(this)
fun EnhanceChannel<ByteBuffer, ByteBuffer>.string() = StringChannel(this)

fun ChannelWriter<String>.stringObjectWriter(
  toString: (obj: Any) -> String = { it.toString() }
): StringObjectWriter = StringObjectWriter(this, toString)