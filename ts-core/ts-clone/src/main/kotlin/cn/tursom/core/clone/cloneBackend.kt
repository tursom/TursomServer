package cn.tursom.core.clone

import cn.tursom.core.*
import cn.tursom.core.datastruct.KPropertyValueMap
import cn.tursom.core.datastruct.SoftArrayMap
import cn.tursom.log.impl.Slf4jImpl
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

private val logger = Slf4jImpl().logger

val Any.valueMap: Map<String, Any?>
  get() = if (this is Map<*, *>) {
    uncheckedCast()
  } else {
    KPropertyValueMap(this)
  }

private val injectMapCache = SoftArrayMap<KClass<*>, Map<String, Property<*>?>>(ConcurrentHashMap())

val <T : Any> KClass<T>.injectMap: MutableMap<String, Property<T>?>
  @Suppress("UNCHECKED_CAST")
  get() {
    var injectMap = injectMapCache[this] as Map<String, Property<T>?>?
    if (injectMap == null) {
      injectMap = allMemberProperties.associateByTo(HashMap()) { property ->
        property.isAccessible = true
        property.name
      }
      injectMapCache[this] = injectMap as Map<String, Property<*>?>
    }
    return HashMap(injectMap)
  }

internal val <T : Any> T.injectMap: MutableMap<String, Property<T>?>
  get() = this::class.uncheckedCast<KClass<T>>().injectMap

internal val <T> Iterator<Pair<Property<*>, Property<T>?>>.injectMap
  get() = let {
    val valueMap = HashMap<String, Property<T>?>()
    @Suppress("UNCHECKED_CAST")
    forEach { (k, field) ->
      field?.isAccessible = true
      valueMap[k.name] = field
    }
    logger.trace("Iterator.injectMap: {}", valueMap)
    valueMap
  }

internal fun <T : Any> T.injectMap(targetClazz: KClass<*>?): MutableMap<String, Property<T>?> = this::class
  .uncheckedCast<KClass<T>>()
  .injectMap
  .also { injectMap ->
    if (targetClazz == null) return@also
    val clazzThis = this::class.java

    fun import(relation: Relation, property: Property<T>) {
      if (relation.clazz != targetClazz) return
      var propertyName = relation.property
      if (propertyName.isBlank()) {
        propertyName = property.name
      }
      logger.trace("relation {} to {}", propertyName, property.name)
      injectMap[propertyName] = when {
        relation.skip -> null
        relation.handler.isEmpty() -> property
        else -> try {
          val handler = clazzThis.getDeclaredMethod(relation.handler, relation.handleClass.java)
          handler.isAccessible = true
          object : KMutableProperty1<T, Any?>, KProperty1<T, Any?> by property {
            override val setter: KMutableProperty1.Setter<T, Any?>
              get() = object : KMutableProperty1.Setter<T, Any?>, KCallable<Unit> by property.uncheckedCast() {
                override val isExternal: Boolean get() = false
                override val isInfix: Boolean get() = false
                override val isInline: Boolean get() = false
                override val isOperator: Boolean get() = false
                override val isSuspend: Boolean get() = false
                override val property: KProperty<Any?> get() = property
                override fun invoke(receiver: T, value: Any?) =
                  handler(this@injectMap, value).inject(receiver, property)
              }

            override fun set(receiver: T, value: Any?) = try {
              handler(this@injectMap, value).inject(receiver, property)
            } catch (e: Exception) {
              logger.trace("", e)
            }
          }
        } catch (e: Exception) {
          logger.warn("an exception caused on generate inject handler", e)
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

    this::class.memberProperties.uncheckedCast<Collection<KProperty1<T, *>>>().forEach { property ->
      property.annotations.forEach { annotation -> parseAnnotation(annotation, property) }
      (property.javaField ?: return@forEach).annotations.forEach { annotation ->
        parseAnnotation(annotation, property)
      }
    }
  }

fun <T : Any> Any.injectWithoutProperty(target: T): T {
  fun parseAnnotation(relation: Relation, property: KProperty1<Any, *>) {
    if (relation.property.isEmpty() && relation.clazz.java.isInstance(this)) try {
      val handler = target::class.java.getDeclaredMethod(relation.handler, relation.clazz.java)
      handler.isAccessible = true
      handler(target, this)?.inject(target, property)
    } catch (e: Exception) {
      logger.trace("an exception caused on global inject", e)
    }
  }

  target::class.memberProperties
    .uncheckedCast<Collection<KProperty1<Any, *>>>()
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
  iterator: Iterator<Pair<String?, Property<T>?>>,
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) { valueMap.inject(target, iterator) }

@JvmName("injectMap")
fun <T : Any> Any.inject(
  target: T,
  iterator: Iterator<Map.Entry<String?, Property<T>?>>,
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) { valueMap.inject(target, iterator) }

fun <T : Any> Map<in String, Any?>.inject(
  target: T,
  iterator: Iterator<Pair<String, Property<T>?>>,
): T {
  iterator.forEach { (k, t) ->
    val value = this[k] ?: return@forEach
    value.inject(target, t ?: return@forEach)
  }
  return target
}

/**
 * point to point inject
 * 通过指定映射关系，从本map中取出数据并注入到目标对象中
 *
 */
fun <T : Any> Map<in String, Any?>.p2pInject(
  target: T,
  iterator: Iterator<Pair<Property<T>, Property<T>?>>,
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
  iterator: Iterator<Map.Entry<String, Property<T>?>>,
): T {
  iterator.forEach { (k, t) ->
    val value = this[k] ?: return@forEach
    value.inject(target, t ?: return@forEach)
  }
  return target
}

fun <T : Any> Any.inject(target: T, property: Property<T>) {
  logger.trace("inject {} into {}.{}", this, +{ target::class.simpleName }, property.name)
  when (property) {
    is KMutableProperty1<*, *> -> {
      @Suppress("UNCHECKED_CAST")
      property as KMutableProperty1<T, Any?>
      property.isAccessible = true
      try {
        property.set(target, cast(this, property.returnType.javaType.uncheckedCast()) ?: return)
      } catch (e: ClassCastException) {
        if (logger.isTraceEnabled) {
          logger.trace("inject {} failed", property.name, e)
        }
      } catch (e: Exception) {
        logger.error("inject {} failed", property.name, e)
      }
    }
    else -> {
      val field = property.javaField ?: return
      field.isAccessible = true
      try {
        field.set(target, cast(this, field.type) ?: return)
      } catch (e: ClassCastException) {
        if (logger.isTraceEnabled) {
          logger.trace("inject {} failed", property.name, e)
        }
      } catch (e: Exception) {
        logger.error("inject {} failed", property.name, e)
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
  java.lang.Character::class.java -> if (source is Number) source.toChar() else source.toString().toIntOrNull()
    ?.toChar()
  java.lang.Short::class.java -> if (source is Number) source.toShort() else source.toString().toShortOrNull()
  java.lang.Integer::class.java -> if (source is Number) source.toInt() else source.toString().toIntOrNull()
  java.lang.Long::class.java -> if (source is Number) source.toLong() else source.toString().toLongOrNull()
  java.lang.Float::class.java -> if (source is Number) source.toFloat() else source.toString().toFloatOrNull()
  java.lang.Double::class.java -> if (source is Number) source.toDouble() else source.toString().toDoubleOrNull()
  java.lang.Boolean::class.java -> if (source is Number) source != 0 else source.toString().toBoolean()

  String::class.java -> source.toString()

  else -> source
}

inline fun <reified T> instance(unsafe: Boolean = true) = instance(unsafe, T::class.java)

fun <T> instance(unsafe: Boolean = true, clazz: Class<T>): T = InstantAllocator[clazz, unsafe]

fun <T> unsafeInstance(clazz: Class<T>, unsafe: Boolean = true): T? = if (unsafe) {
  @Suppress("UNCHECKED_CAST")
  Unsafe.unsafe.allocateInstance(clazz) as T?
} else {
  null
}