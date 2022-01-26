package cn.tursom.core.delegation

interface DefaultDelegatedFieldAttachmentKey<V> : DelegatedFieldAttachmentKey<V> {
  val default: V
}
