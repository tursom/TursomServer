package cn.tursom.core.delegation

import cn.tursom.core.cast

class NotNullMutableDelegatedField<in T, V : Any>(
  override val mutableDelegatedField: MutableDelegatedField<T, V?>,
) : MutableDelegatedField<T, V> by mutableDelegatedField.cast(), DecoratorMutableDelegatedField<T, V?>

