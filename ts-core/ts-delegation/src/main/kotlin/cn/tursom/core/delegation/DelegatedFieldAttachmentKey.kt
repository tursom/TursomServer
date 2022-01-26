package cn.tursom.core.delegation

import kotlin.reflect.KProperty0

interface DelegatedFieldAttachmentKey<V> {
  operator fun get(delegatedField: DelegatedField<*, *>) = delegatedField[this]
  operator fun get(property0: KProperty0<*>) = property0.getDelegatedAttachmentValue(this)
}

