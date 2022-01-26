package cn.tursom.core.delegation

import kotlin.reflect.KProperty

/**
 * 这是用来规范kotlin委托属性的一个接口，你可以用这个接口来针对属性进行硬编码级别的动态代理
 * 比如如果你需要监控属性的变化，可以使用 FieldChangeListener.listened 来定义属性
 * 如果你还需要对这个属性进行加锁，你就可以在后方加一个 .locked 即可
 * 如果还需要用指定的锁，在后面再加一个 (lock) 就玩成了
 * 使用例：
 * class XXXImpl: XXX, FieldChangeListener by FieldChangeListenerImpl() {
 *     val lock = ReentrantLock()
 *     val field by listened(0).locker(lock)
 * }
 */
interface DelegatedField<in T, out V> {
  /**
   * 用来获取值，不会发生附加调用
   */
  fun getValue(): V
  operator fun getValue(thisRef: T, property: KProperty<*>): V = getValue()

  operator fun <K> get(key: DelegatedFieldAttachmentKey<K>): K? =
    if (this is DecoratorDelegatedField<*, *>) {
      delegatedField[key]
    } else {
      null
    }

  operator fun <K> get(key: DefaultDelegatedFieldAttachmentKey<K>): K =
    get(key as DelegatedFieldAttachmentKey<K>) ?: key.default

  operator fun <V> set(key: DelegatedFieldAttachmentKey<V>, value: V): Boolean =
    if (this is DecoratorDelegatedField<*, *>) {
      delegatedField.set(key, value)
    } else {
      false
    }

  fun <V> removeAttachment(key: DelegatedFieldAttachmentKey<V>): Boolean =
    if (this is DecoratorDelegatedField<*, *>) {
      delegatedField.removeAttachment(key)
    } else {
      false
    }
}

