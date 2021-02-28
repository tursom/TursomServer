package cn.tursom.delegation

class SetterDelegatedField<T, V>(
  override val delegatedField: DelegatedField<T, V>,
  val setter: DelegatedField<T, V>.(value: V) -> Unit,
) : MutableDelegatedField<T, V>, DelegatedField<T, V> by delegatedField, DecoratorDelegatedField<T, V> {
  override fun setValue(value: V) = delegatedField.setter(value)
}
