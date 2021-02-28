package cn.tursom.delegation

interface DecoratorMutableDelegatedField<in T, V> : DecoratorDelegatedField<T, V> {
  val mutableDelegatedField: MutableDelegatedField<T, V>
  override val delegatedField: DelegatedField<T, V> get() = mutableDelegatedField
}