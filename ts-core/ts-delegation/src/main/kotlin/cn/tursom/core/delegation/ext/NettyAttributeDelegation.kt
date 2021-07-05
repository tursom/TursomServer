package cn.tursom.core.delegation.ext

import cn.tursom.core.delegation.MutableDelegatedField
import cn.tursom.core.netty.LazyChannel
import io.netty.channel.Channel
import io.netty.util.AttributeKey

class NettyAttributeDelegation<T, V>(
  private val channel: Channel,
  private val attachmentKey: AttributeKey<V>,
) : MutableDelegatedField<T, V> {
  override fun getValue(): V {
    return channel.attr(attachmentKey).get()
  }

  override fun setValue(value: V) {
    channel.attr(attachmentKey).set(value)
  }

  companion object {
    fun <T, V> T.attributeDelegation(
      channel: Channel,
      attachmentKey: AttributeKey<V>,
    ): MutableDelegatedField<T, V> {
      return NettyAttributeDelegation(channel, attachmentKey)
    }

    fun <T, V> T.attributeDelegation(
      attachmentKey: AttributeKey<V>,
      channel: () -> Channel?,
    ): MutableDelegatedField<T, V> {
      return NettyAttributeDelegation(LazyChannel(channel), attachmentKey)
    }
  }
}
