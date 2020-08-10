@file:Suppress("unused")

package cn.tursom.utils.clone

import cn.tursom.core.Unsafe
import cn.tursom.core.cast
import cn.tursom.core.datastruct.ArrayMap
import cn.tursom.core.datastruct.ReadWriteMap
import cn.tursom.core.datastruct.SoftArrayMap
import cn.tursom.core.final
import cn.tursom.utils.datastruct.KPropertyValueMap
import com.ddbes.kotlin.clone.Key
import com.ddbes.kotlin.clone.NoPropertyClone
import com.ddbes.kotlin.clone.Relation
import com.ddbes.kotlin.clone.Relations
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType


typealias Property<T> = KProperty1<T, Any?>

val Any.valueMap: Map<String, Any?>
  get() = if (this is Map<*, *>) {
    cast()
  } else {
    KPropertyValueMap(this)
  }

private val injectMapCache = ReadWriteMap<KClass<*>, ArrayMap<String, Property<*>?>>(SoftArrayMap(HashMap()))

val <T : Any> KClass<T>.injectMap: ArrayMap<String, Property<T>?>
  @Suppress("UNCHECKED_CAST")
  get() = let {
    var valueMap = injectMapCache[it] as ArrayMap<String, Property<T>?>?
    if (valueMap == null) {
      val properties = it.memberProperties
      valueMap = ArrayMap(properties.size)
      (properties as Collection<Property<T>>).forEach { property ->
        property.isAccessible = true
        valueMap[property.name] = property
      }
      injectMapCache[it] = valueMap as ArrayMap<String, Property<*>?>
    }
    valueMap.copy()
  }

private val <T : Any> T.injectMap: ArrayMap<String, Property<T>?>
  @Suppress("UNCHECKED_CAST")
  get() = (this::class as KClass<T>).injectMap

private val <T> Array<out Pair<Property<*>, Property<T>?>>.injectMap get() = iterator().injectMap
private val <T> Iterator<Pair<Property<*>, Property<T>?>>.injectMap
  get() = let {
    val valueMap = ArrayMap<String, Property<T>?>()
    @Suppress("UNCHECKED_CAST")
    forEach { (k, field) ->
      field?.isAccessible = true
      valueMap[k.name] = field
    }
    //logger.trace("Iterator.injectMap: {}", valueMap)
    valueMap
  }

private fun <T : Any> T.injectMap(clazz: KClass<*>?): ArrayMap<String, Property<T>?> = this::class
  .cast<KClass<T>>()
  .injectMap
  .also {
    if (clazz == null) return@also
    val clazzThis = this::class.java

    fun import(relation: Relation, property: Property<T>) {
      if (relation.clazz != clazz) return
      //logger.trace("relation {} to {}", relation.property, property.name)
      it[relation.property] = when {
        relation.skip -> null
        relation.handler.isEmpty() -> property
        else -> try {
          val handler = clazzThis.getDeclaredMethod(relation.handler, relation.handleClass.java)
          handler.isAccessible = true
          object : KMutableProperty1<T, Any?>, KProperty1<T, Any?> by property {
            override val setter: KMutableProperty1.Setter<T, Any?> get() = TODO()
            override fun set(receiver: T, value: Any?) =
              handler(this@injectMap, value).inject(receiver, property)
          }
        } catch (e: Exception) {
          //logger.warn("an exception caused on generate inject handler", e)
          null
        }
      }
    }

    fun parseAnnotation(annotation: Annotation, property: Property<T>) {
      when (annotation) {
        is Relation -> import(annotation, property)
        is Relations -> annotation.relations.forEach { relation -> import(relation, property) }
      }
    }

    this::class.memberProperties.cast<Collection<KProperty1<T, *>>>().forEach { property ->
      property.annotations.forEach { annotation -> parseAnnotation(annotation, property) }
      (property.javaField ?: return@forEach).annotations.forEach { annotation ->
        parseAnnotation(annotation, property)
      }
    }
  }

/**
 * 用于形式化的将List中的数据映射到实体类上
 * 使用方式类似与：
 *
 * list.clone<Entity>(
 *     unsafe = true,
 *     Entity::p1,
 *     Entity::p2,
 *     null,
 *     Entity::p3,
 *     ......
 * )
 *
 */
inline fun <reified T : Any, S : Any> List<S?>.clone(
  unsafe: Boolean = true,
  vararg relation: Pair<Property<S>, Property<T>?>
): List<T> {
  val list = ArrayList<T>(size)
  val memberMap = T::class.injectMap
  relation.forEach { (k, v) ->
    memberMap[k.toString()] = v
  }
  forEach {
    it ?: return@forEach
    val target = instance<T>(unsafe) ?: return@forEach
    list.add(it.inject(target, memberMap.iterator()))
  }
  return list
}

inline fun <reified T : Any> List<Any?>.clone(
  unsafe: Boolean = true,
  vararg relation: Property<T>?
): T = clone(instance<T>(unsafe)!!, relation.iterator())

fun <T : Any> List<Any?>.clone(
  target: T,
  vararg relation: Property<T>?
): T = clone(target, relation.iterator())

fun <T : Any> List<Any?>.clone(
  target: T,
  relation: Iterator<Property<T>?>
): T = relation.mapIndexed { index, kProperty1 ->
  (kProperty1 ?: return@mapIndexed null).name to this[index]
}.clone(target)


/**
 * 新建并拷贝
 * @author 王景阔
 * 创建类型 T 的实例
 * 并将对象两个的所有同名字段拷贝进新建的实例中
 * @return 新建的实例
 * @param unsafe 是否允许使用 Unsafe 创建对象，unsafe 不需调用构造函数，可以在没有默认构造函数的情况下生成对象
 */

inline fun <reified T : Any> Any.clone(unsafe: Boolean = true): T = clone(instance<T>(unsafe)!!)

fun <T : Any> Any.clone(clazz: Class<T>, unsafe: Boolean = true): T = clone(instance(unsafe, clazz)!!)

@JvmName("unsafeClone")
inline fun <reified T : Any> Any.clone(
  unsafe: Boolean = true,
  vararg relation: Pair<String, Property<T>?>
): T = clone(instance<T>(unsafe)!!, relation.iterator())

inline fun <reified T : Any, S : Any> S.clone(
  unsafe: Boolean = true,
  vararg relation: Pair<Property<S>, Property<T>?>
): T = clone(instance<T>(unsafe)!!, relation.iterator())

fun Any.cloneMap(): Map<in String, Any?> = when (this) {
  is Map<*, *> -> @Suppress("UNCHECKED_CAST") (this as Map<Any?, Any?>).mapKeys { it.key.toString() }
  is Iterator<*> -> {
    val valueMap = HashMap<String, Any?>()
    (this as Iterator<Any?>).forEach {
      when (it) {
        is Pair<*, *> -> valueMap[it.first.toString()] = it.second
        is Map.Entry<*, *> -> valueMap[it.key.toString()] = it.value
      }
    }
    valueMap
  }
  is Iterable<*> -> (this as Iterable<Any?>).iterator().cloneMap()
  else -> valueMap
}

fun <T : Any> Any.clone(target: T): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) {
  valueMap.inject(target, target.injectMap(this::class).iterator())
}


fun <T : Any, S : Any> S.clone(
  target: T,
  iterator: Iterator<Pair<Property<S>, Property<T>?>>
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) {
  valueMap
    //.also { logger.trace("clone {} into {}, value map:{}", this, target, it) }
    .clone(target, iterator.injectMap.iterator(), this::class)
}

fun <T : Any> Any.clone(
  target: T,
  vararg relation: Pair<String, Property<T>?>
): T = clone(target, relation.iterator())

@JvmName("unsafeClone")
fun <T : Any> Any.clone(
  target: T,
  iterator: Iterator<Pair<String, Property<T>?>>
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) {
  valueMap.clone(target, iterator, this::class)
}


fun <T : Any> Map<in String, Any?>.clone(
  target: T,
  iterator: Iterator<Pair<String, Property<T>?>>,
  clazz: KClass<*>? = null
): T {
  val memberMap = target.injectMap(clazz) as MutableMap<String, Property<T>?>
  iterator.forEach { (k, v) ->
    memberMap[k] = v
  }
  return inject(target, memberMap.iterator())
}

@JvmName("smartClone")
fun <T : Any> Map<in String, Any?>.clone(
  target: T,
  iterator: Iterator<Pair<Property<T>, Property<T>?>>,
  clazz: KClass<*>? = null
): T {
  val memberMap = target.injectMap(clazz) as MutableMap<String, Property<T>?>
  iterator.forEach { (k, v) ->
    memberMap[k.get(target)?.toString() ?: return@forEach] = v
  }
  return inject(target, memberMap.iterator())
}

@JvmName("cloneMap")
fun <T : Any> Map<in String, Any?>.clone(
  target: T,
  iterator: Iterator<Map.Entry<String?, Property<T>?>>,
  clazz: KClass<*>? = null
): T {
  val memberMap = target.injectMap(clazz)
  iterator.forEach { (k, v) ->
    memberMap[k ?: return@forEach] = v
  }
  //logger.trace("inject {} into {}, mapping: {}", this, target, memberMap)
  return inject(target, memberMap.iterator())
}

fun <T : Any> Any.checkPropertyClone(target: T, ifClone: () -> T): T =
  if (target::class.findAnnotation<NoPropertyClone>()?.classList?.contains(this::class) != true) {
    ifClone()
  } else {
    target
  }

//fun <T> Any.checkPropertyClone(targetClass: KClass<out Any>, ifClone: () -> T): T {
//    if (targetClass.findAnnotation<NoPropertyClone>()?.classList?.contains(this::class) != true)
//        ifClone()
//}

fun <T : Any> Any.injectWithoutProperty(target: T): T {
  fun parseAnnotation(relation: Relation, property: KProperty1<Any, *>) {
    if (relation.property.isEmpty() && relation.clazz.java.isInstance(this)) try {
      val handler = target::class.java.getDeclaredMethod(relation.handler, relation.clazz.java)
      handler.isAccessible = true
      handler(target, this)?.inject(target, property)
    } catch (e: Exception) {
      //logger.warn("an exception caused on global inject", e)
    }
  }

  target::class.memberProperties
    .cast<Collection<KProperty1<Any, *>>>()
    .forEach { property ->
      property.annotations.forEach { annotation ->
        when (annotation) {
          is Relation -> parseAnnotation(annotation, property)
          is Relations -> annotation.relations.forEach { parseAnnotation(it, property) }
        }
      }
      property.javaField?.annotations?.forEach { annotation ->
        when (annotation) {
          is Relation -> parseAnnotation(annotation, property)
          is Relations -> annotation.relations.forEach { parseAnnotation(it, property) }
        }
      }
    }
  return target
}

fun <T : Any> Any.inject(
  target: T,
  iterator: Iterator<Pair<String?, Property<T>?>>
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) { valueMap.inject(target, iterator) }

@JvmName("injectMap")
fun <T : Any> Any.inject(
  target: T,
  iterator: Iterator<Map.Entry<String?, Property<T>?>>
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) { valueMap.inject(target, iterator) }

fun <T : Any> Map<in String, Any?>.inject(
  target: T,
  iterator: Iterator<Pair<String, Property<T>?>>
): T {
  iterator.forEach { (k, t) ->
    val value = this[k] ?: return@forEach
    value.inject(target, t ?: return@forEach)
  }
  return target
}

fun <T : Any> Map<in String, Any?>.p2pInject(
  target: T,
  iterator: Iterator<Pair<Property<T>, Property<T>?>>
): T {
  iterator.forEach { (k, t) ->
    val value = this[k(target)?.toString() ?: return@forEach] ?: return@forEach
    value.inject(target, t ?: return@forEach)
  }
  return target
}

@JvmName("injectMap")
fun <T : Any> Map<in String, Any?>.inject(
  target: T,
  iterator: Iterator<Map.Entry<String, Property<T>?>>
): T {
  iterator.forEach { (k, t) ->
    val value = this[k] ?: return@forEach
    value.inject(target, t ?: return@forEach)
  }
  return target
}

fun <T : Any> Any.inject(target: T, property: Property<T>) {
  //logger.trace("inject {} into {}.{}", this, +{ target::class.simpleName }, property.name)
  when (property) {
    is KMutableProperty1<*, *> -> {
      @Suppress("UNCHECKED_CAST")
      property as KMutableProperty1<T, Any?>
      property.isAccessible = true
      try {
        property.set(target, cast(this, property.returnType.javaType.cast()) ?: return)
      } catch (e: ClassCastException) {
        //logger.trace("inject failed", e)
      }
    }
    else -> {
      val field = property.javaField ?: return
      field.isAccessible = true
      field.final = false
      try {
        field.set(target, cast(this, field.type) ?: return)
      } catch (e: Exception) {
        //logger.trace("inject failed", e)
      }
    }
  }
}

fun cast(source: Any, target: Class<*>): Any? = if (target.isInstance(source)) {
  source
} else when (target) {
  Byte::class.java -> if (source is Number) source.toByte() else source.toString().toByteOrNull()
  Char::class.java -> if (source is Number) source.toChar() else source.toString().toIntOrNull()?.toChar()
  Short::class.java -> if (source is Number) source.toShort() else source.toString().toShortOrNull()
  Int::class.java -> if (source is Number) source.toInt() else source.toString().toIntOrNull()
  Long::class.java -> if (source is Number) source.toLong() else source.toString().toLongOrNull()
  Float::class.java -> if (source is Number) source.toFloat() else source.toString().toFloatOrNull()
  Double::class.java -> if (source is Number) source.toDouble() else source.toString().toDoubleOrNull()
  Boolean::class.java -> if (source is Number) source != 0 else source.toString().toBoolean()

  java.lang.Byte::class.java -> if (source is Number) source.toByte() else source.toString().toByteOrNull()
  java.lang.Character::class.java -> if (source is Number) source.toChar() else source.toString().toIntOrNull()?.toChar()
  java.lang.Short::class.java -> if (source is Number) source.toShort() else source.toString().toShortOrNull()
  java.lang.Integer::class.java -> if (source is Number) source.toInt() else source.toString().toIntOrNull()
  java.lang.Long::class.java -> if (source is Number) source.toLong() else source.toString().toLongOrNull()
  java.lang.Float::class.java -> if (source is Number) source.toFloat() else source.toString().toFloatOrNull()
  java.lang.Double::class.java -> if (source is Number) source.toDouble() else source.toString().toDoubleOrNull()
  java.lang.Boolean::class.java -> if (source is Number) source != 0 else source.toString().toBoolean()

  String::class.java -> source.toString()

  else -> source
}

fun <T : Any> T.read(source: Any?): T = source?.clone(this) ?: this

fun <T : Any, S : Any> T.read(
  source: S?,
  vararg relation: Pair<Property<S>, Property<T>?>
): T = source?.clone(this, relation.iterator()) ?: this

fun <T : Any> T.read(
  source: Map<in String, Any?>?,
  vararg relation: Pair<String, Property<T>?>
): T = source?.clone(this, relation.iterator()) ?: this


fun <T : Any> T.p2pRead(
  source: Map<in String, Any?>?,
  vararg relation: Pair<Property<T>, Property<T>?>
): T {
  //logger.trace("p2p read, source:{}, relation: {}", source, relation)
  return source?.p2pInject(this, relation.iterator()) ?: this
}

fun <T : Any> T.p2pRead(
  source: Map<in String, Any?>?
): T {
  source ?: return this
  val properties = this::class.memberProperties.cast<Collection<KProperty1<T, *>>>()
  val relation = properties.mapNotNull { property ->
    val key = property.javaField?.getAnnotation(Key::class.java)?.key ?: return@mapNotNull null
    val keyProperty = properties.find { it.name == key } ?: return@mapNotNull null
    keyProperty to property
  }
  //logger.trace("p2p read, source:{}, relation: {}", source, relation)
  return source.p2pInject(this, relation.iterator())
}

fun <T : Any> T.p2pRead(
  source: Any?
): T {
  source ?: return this
  val properties = this::class.memberProperties.cast<Collection<KProperty1<T, *>>>()
  properties.forEach { property ->
    val keyAnnotation = property.javaField?.getAnnotation(Key::class.java) ?: return@forEach
    if (keyAnnotation.handler.isEmpty()) return@forEach
    if (keyAnnotation.clazz.isInstance(source).not()) return@forEach
    val handler = this::class.java.getDeclaredMethod(keyAnnotation.handler, keyAnnotation.clazz.java)
    (handler(this, source) ?: return@forEach).inject(this, property)
  }
  return this
}

fun <T : Any, S : Any> List<T>.read(
  source: List<S>,
  vararg relation: Pair<Property<S>, Property<T>?>
): List<T> {
  val memberMap = this[0].injectMap as MutableMap<String, Property<T>?>
  relation.forEach { (k, v) ->
    memberMap[k.name] = v
  }
  return source.mapIndexed { index, it -> it.inject(this[index], memberMap.iterator()) }
}

inline fun <reified T : Any> read(vararg values: Any, unsafe: Boolean = true): T {
  val instance = instance<T>(unsafe)!!
  values.forEach {
    it.clone(instance)
  }
  return instance
}

inline fun <reified T : Any> read(value: Any, unsafe: Boolean = true): T = value.clone(unsafe)

fun <T : Any> T.readWithoutProperty(vararg values: Any): T {
  values.forEach {
    it.injectWithoutProperty(this)
  }
  return this
}

inline fun <reified T> instance(unsafe: Boolean = true) = instance(unsafe, T::class.java)

fun <T> instance(unsafe: Boolean = true, clazz: Class<T>): T? = try {
  clazz.newInstance()!!
} catch (e: Exception) {
  if (unsafe) {
    @Suppress("UNCHECKED_CAST")
    Unsafe.unsafe.allocateInstance(clazz) as T?
  } else {
    null
  }
}

inline fun <T, K : Comparable<K>, V> Iterator<T>.mapIndexed(action: (Int, T) -> Pair<K, V>?): Map<K, V> {
  val map = ArrayMap<K, V>()
  var index = 0
  forEach {
    action(index, it)?.let { (k, v) -> map[k] = v }
    index++
  }
  return map
}

fun <T : Any> T.mapRead(map: Map<in String, Any?>, key: KProperty1<T, *>): T {
  return read(map[key.get(this)?.toString() ?: return this] ?: return this)
}