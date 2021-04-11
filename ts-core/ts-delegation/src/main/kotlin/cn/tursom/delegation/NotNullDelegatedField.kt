package cn.tursom.delegation

import cn.tursom.core.cast

class NotNullDelegatedField<in T, out V : Any>(
  override val delegatedField: DelegatedField<T, V?>,
) : DelegatedField<T, V> by delegatedField.cast(), DecoratorDelegatedField<T, V?>