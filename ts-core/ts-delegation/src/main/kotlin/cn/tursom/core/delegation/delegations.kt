@file:Suppress("unused")

package cn.tursom.core.delegation

import cn.tursom.core.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun getDelegate(obj: Any?, field: String): DelegatedField<*, *>? {
  obj ?: return null
  val kProperty1 = obj.javaClass.kotlin.memberProperties.firstOrNull { it.name == field }
  kProperty1?.isAccessible = true
  val delegate = kProperty1?.getDelegate(obj)
  return delegate.castOrNull()
}

val <V> KProperty0<V>.getOwnerDelegated: DelegatedField<*, V>?
  get() = getDelegate(receiver, name).castOrNull()

/**
 * 获取委托过后的值
 * 不会触发getter方法
 */
val <V> KProperty0<V>.getDelegatedValue: V?
  get() {
    val delegate = getDelegate() ?: getOwnerDelegated
    return delegate?.castOrNull<DelegatedField<*, V>>()?.getValue()
  }

fun <V> KProperty0<*>.getDelegatedAttachmentValue(key: DelegatedFieldAttachmentKey<V>): V? {
  val delegated = getOwnerDelegated ?: return null
  return delegated[key]
}

operator fun <V> KProperty0<*>.get(key: DelegatedFieldAttachmentKey<V>): V? = getDelegatedAttachmentValue(key)

fun <V> KProperty0<V>.setDelegatedValue(value: V): Boolean {
  val delegate = getDelegate() ?: getOwnerDelegated
  delegate.castOrNull<MutableDelegatedField<*, V>>()?.setValue(value) ?: return false
  return true
}

/**
 * 将可空属性委托为不可空属性
 */
fun <T, V> T.delegated(value: V): MutableDelegatedField<T, V> = MutableDelegatedFieldValue(value)
fun <T, V> T.delegated(@Suppress("UNUSED_PARAMETER") target: T, value: V): MutableDelegatedField<T, V> =
  MutableDelegatedFieldValue(value)

val <V> KProperty0<V>.delegated: DelegatedField<Any, V> get() = KPropertyDelegatedField(this)
val <V> KMutableProperty0<V>.delegated: MutableDelegatedField<Any, V> get() = KPropertyMutableDelegatedField(this)
val <V : Any> KProperty0<V?>.delegatedNotNull get() = delegated.notNull
val <V : Any> KMutableProperty0<out V?>.delegatedNotNull: MutableDelegatedField<Any, V> get() = delegated.notNull
fun <V : Any> KProperty0<V?>.delegatedNotNull(ifNull: () -> Nothing) = delegated.notNull(ifNull)
fun <V : Any> KMutableProperty0<out V?>.delegatedNotNull(ifNull: () -> Nothing): MutableDelegatedField<Any, V> =
  delegated.notNull(ifNull)

val <T, V : Any> DelegatedField<T, V?>.notNull: DelegatedField<T, V> get() = NotNullDelegatedField(this)
val <T, V : Any> MutableDelegatedField<T, out V?>.notNull: MutableDelegatedField<T, V>
  get() = NotNullMutableDelegatedField(uncheckedCast())

fun <T, V : Any> DelegatedField<T, V?>.notNull(ifNull: () -> Nothing): DelegatedField<T, V> =
  NotNullDelegatedField(this, ifNull)

fun <T, V : Any> MutableDelegatedField<T, out V?>.notNull(ifNull: () -> Nothing): MutableDelegatedField<T, V> =
  NotNullMutableDelegatedField(uncheckedCast(), ifNull)

val <T, V> MutableDelegatedField<T, V>.locked: MutableDelegatedField<T, V> get() = LockMutableDelegatedField(this)
fun <T, V> MutableDelegatedField<T, V>.locked(lock: Lock = ReentrantLock()): MutableDelegatedField<T, V> =
  LockMutableDelegatedField(this, lock)

val <T, V> MutableDelegatedField<T, V>.readWriteLocked: MutableDelegatedField<T, V>
  get() = ReadWriteLockMutableDelegatedField(this)

fun <T, V> MutableDelegatedField<T, V>.readWriteLocked(readWriteLock: ReadWriteLock = ReentrantReadWriteLock()): MutableDelegatedField<T, V> =
  ReadWriteLockMutableDelegatedField(this, readWriteLock)


fun <T, V> ThreadLocal<V?>.delegated(): MutableDelegatedField<T, V?> = ThreadLocalMutableDelegatedField(this)
fun <T, V> T.delegated(
  threadLocal: ThreadLocal<V?>,
): MutableDelegatedField<T, V?> = ThreadLocalMutableDelegatedField(threadLocal)

fun <T, V> T.threadLocalDelegated(): MutableDelegatedField<T, V?> = ThreadLocalMutableDelegatedField()
fun <T, V> T.threadLocalDelegated(type: V?): MutableDelegatedField<T, V?> = ThreadLocalMutableDelegatedField()
fun <T, V> T.threadLocalDelegated(type: Class<out V?>): MutableDelegatedField<T, V?> =
  ThreadLocalMutableDelegatedField()

fun <T, V : Any> T.threadLocalDelegated(type: KClass<V>): MutableDelegatedField<T, V?> =
  ThreadLocalMutableDelegatedField()

fun <T, V : Any> SimpThreadLocal<V>.delegated(): MutableDelegatedField<T, V> =
  SimpThreadLocalMutableDelegatedField(this)

fun <T, V : Any> T.threadLocalDelegated(
  threadLocal: ThreadLocal<V?> = ThreadLocal(),
  new: () -> V,
): MutableDelegatedField<T, V> = SimpThreadLocalMutableDelegatedField(SimpThreadLocal(threadLocal, new))
//fun <T, V> T.threadLocalDelegated(simpThreadLocal: SimpThreadLocal<V>): MutableDelegatedField<T, V> =
//    SimpThreadLocalMutableDelegatedField(simpThreadLocal)

fun <T, V> MutableDelegatedField<T, V>.filter(filter: T.(old: V, new: V) -> Boolean): MutableDelegatedField<T, V> =
  FilterDelegatedField(this, filter)

fun <T, V> DelegatedField<T, V>.setter(setter: DelegatedField<T, V>.(value: V) -> Unit): DelegatedField<T, V> =
  SetterDelegatedField(this, setter)

fun <T, V> MutableDelegatedField<T, V>.setter(setter: MutableDelegatedField<T, V>.(thisRef: T, property: KProperty<*>, value: V) -> Unit): MutableDelegatedField<T, V> =
  SetterMutableDelegatedField(this, setter)

fun <T, V> DelegatedField<T, V>.getter(getter: DelegatedField<T, V>.(thisRef: T, property: KProperty<*>) -> V): DelegatedField<T, V> =
  GetterDelegatedField(this, getter)

fun <T, V> MutableDelegatedField<T, V>.getter(getter: MutableDelegatedField<T, V>.(thisRef: T, property: KProperty<*>) -> V): MutableDelegatedField<T, V> =
  GetterMutableDelegatedField(this, getter)

fun <T, V> MutableDelegatedField<T, V>.withExecutor(executor: Executor): MutableDelegatedField<T, V> =
  ExecutorMutableDelegatedField(this, executor)

@Listenable
fun <V> KMutableProperty0<V>.listenable(): MutableDelegatedField<Any, V> {
  isAccessible = true
  val delegate = getDelegate()
  return if (delegate is MutableDelegatedField<*, *> && delegate[ListenableMutableDelegatedField] == true) {
    @OptIn(UncheckedCast::class)
    delegate.cast()
  } else {
    @OptIn(UncheckedCast::class)
    (ListenableMutableDelegatedField(KPropertyMutableDelegatedField(cast())))
  }
}

@Listenable
fun <V> listenable(initValue: V): MutableDelegatedField<Any, V> = ListenableMutableDelegatedField(
  MutableDelegatedFieldValue(initValue)
)

@Listenable
fun <T, V> MutableDelegatedField<T, V>.listenable(): MutableDelegatedField<T, V> =
  ListenableMutableDelegatedField(this)

@JvmName("listenable1")
@Listenable
fun <T, V> listenable(delegatedField: MutableDelegatedField<T, V>): MutableDelegatedField<T, V> =
  ListenableMutableDelegatedField(delegatedField)


infix operator fun <T, V> ListenableListener<T, V>.plus(listener: T.(old: V, new: V) -> Unit): Listener<T, V> =
  addListener(listener)

@OptIn(Listenable::class)
fun <V> KProperty0<V>.getListenableMutableDelegatedField(): ListenableMutableDelegatedField<Any, V>? {
  isAccessible = true
  var delegate = getDelegate() ?: getDelegate(receiver, name)
  if (delegate is DelegatedField<*, *>) {
    while (true) {
      if (delegate is ListenableMutableDelegatedField<*, *>) {
        @OptIn(UncheckedCast::class)
        return delegate.cast()
      }
      if (delegate is DecoratorDelegatedField<*, *>) {
        delegate = delegate.delegatedField
      } else {
        break
      }
    }
  } else if (delegate is KProperty0<*> && delegate != this) {
    @OptIn(UncheckedCast::class)
    return delegate.cast<KProperty0<V>>().getListenableMutableDelegatedField()
  }
  return null
}

inline fun <T, V> T.addChangeListener(
  property: T.() -> KProperty0<V>,
): ListenableListener<T, V> {
  val kProperty0 = property()

  @OptIn(Listenable::class, UncheckedCast::class)
  val delegatedField = kProperty0
    .getListenableMutableDelegatedField()
    .cast<ListenableMutableDelegatedField<T, V>?>()
    ?: throw UnmonitoredFieldException(kProperty0.toString())
  return object : ListenableListener<T, V> {
    private val selfList = ConcurrentLinkedQueue<Listener<T, V>>()
    override fun addListener(listener: T.(old: V, new: V) -> Unit): Listener<T, V> {
      @OptIn(Listenable::class)
      val listener1 = delegatedField.addListener(listener)
      selfList.add(listener1)
      return listener1
    }

    override fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): ListenableListener<T, V> {
      selfList.forEach {
        it.catch(handler)
      }
      return this
    }

    override fun cancel(): Boolean {
      selfList.forEach {
        it.cancel()
      }
      return true
    }

    override fun finally(handler: T.(old: V, new: V) -> Unit): Listener<T, V> {
      selfList.forEach {
        it.finally(handler)
      }
      return this
    }
  }
}
