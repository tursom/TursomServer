package cn.tursom.core.clone

import cn.tursom.core.*
import cn.tursom.core.datastruct.ArrayMap
import cn.tursom.core.reflect.InstantAllocator
import cn.tursom.log.impl.Slf4jImpl
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

/**
 * clone 使用的日志对象
 */
private val logger = Slf4jImpl.getLogger()

/**
 * clone 使用的对象属性类型
 */
typealias Property<T> = KProperty1<T, Any?>


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
  vararg relation: Pair<Property<S>, Property<T>?>,
): List<T> {
  val list = ArrayList<T>(size)
  val memberMap = T::class.injectMap
  relation.forEach { (k, v) ->
    memberMap[k.toString()] = v
  }
  forEach {
    it ?: return@forEach
    try {
      val target = InstantAllocator[T::class.java, unsafe]
      list.add(it.inject(target, memberMap.iterator()))
    } catch (e: Exception) {
    }
  }
  return list
}

inline fun <reified T : Any> List<Any?>.clone(
  unsafe: Boolean = true,
  vararg relation: Property<T>?,
): T = clone(InstantAllocator[T::class.java, unsafe], relation.iterator())

fun <T : Any> List<Any?>.clone(
  target: T,
  vararg relation: Property<T>?,
): T = clone(target, relation.iterator())

fun <T : Any> List<Any?>.clone(
  target: T,
  relation: Iterator<Property<T>?>,
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

inline fun <reified T : Any> Any.clone(unsafe: Boolean = true): T = clone(InstantAllocator[T::class.java, unsafe])

fun <T : Any> Any.clone(clazz: Class<T>, unsafe: Boolean = true): T = clone(InstantAllocator[clazz, unsafe])

@JvmName("unsafeClone")
inline fun <reified T : Any> Any.clone(
  unsafe: Boolean = true,
  vararg relation: Pair<String, Property<T>?>,
): T = clone(InstantAllocator[T::class.java, unsafe], relation.iterator())

inline fun <reified T : Any, S : Any> S.clone(
  unsafe: Boolean = true,
  vararg relation: Pair<Property<S>, Property<T>?>,
): T = clone(InstantAllocator[T::class.java, unsafe], relation.iterator())

fun Any.cloneMap(): Map<in String, Any?> = when (this) {
  is Map<*, *> -> uncheckedCast<Map<Any?, Any?>>().mapKeys { it.key.toString() }
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
  iterator: Iterator<Pair<Property<S>, Property<T>?>>,
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) {
  valueMap
    .also { logger.trace("clone {} into {}, value map:{}", this, target, it) }
    .clone(target, iterator.injectMap.iterator(), this::class)
}

fun <T : Any> Any.clone(
  target: T,
  vararg relation: Pair<String, Property<T>?>,
): T = clone(target, relation.iterator())

@JvmName("unsafeClone")
fun <T : Any> Any.clone(
  target: T,
  iterator: Iterator<Pair<String, Property<T>?>>,
): T = apply { injectWithoutProperty(target) }.checkPropertyClone(target) {
  valueMap.clone(target, iterator, this::class)
}


fun <T : Any> Map<in String, Any?>.clone(
  target: T,
  iterator: Iterator<Pair<String, Property<T>?>>,
  clazz: KClass<*>? = null,
): T {
  val memberMap = target.injectMap(clazz)
  iterator.forEach { (k, v) ->
    memberMap[k] = v
  }
  return inject(target, memberMap.iterator())
}

@JvmName("smartClone")
fun <T : Any> Map<in String, Any?>.clone(
  target: T,
  iterator: Iterator<Pair<Property<T>, Property<T>?>>,
  clazz: KClass<*>? = null,
): T {
  val memberMap = target.injectMap(clazz)
  iterator.forEach { (k, v) ->
    memberMap[k.get(target)?.toString() ?: return@forEach] = v
  }
  return inject(target, memberMap.iterator())
}

@JvmName("cloneMap")
fun <T : Any> Map<in String, Any?>.clone(
  target: T,
  iterator: Iterator<Map.Entry<String?, Property<T>?>>,
  clazz: KClass<*>? = null,
): T {
  val memberMap = target.injectMap(clazz)
  iterator.forEach { (k, v) ->
    memberMap[k ?: return@forEach] = v
  }
  logger.trace("inject {} into {}, mapping: {}", this, target, memberMap)
  return inject(target, memberMap.iterator())
}

fun <T : Any> Any.checkPropertyClone(target: T, ifClone: () -> T): T =
  if (target::class.findAnnotation<NoPropertyClone>()?.classList?.contains(this::class) != true) {
    ifClone()
  } else {
    target
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

fun <T : Any, F : Any?> T.set(field: KProperty1<T, F>, value: F): T {
  (value as Any?)?.inject(this, field as Property<T>)
  return this
}

fun <T : Any> T.deepClone(): T = when (this) {
  is Char, is Boolean, is Byte, is Short, is Int, is Long, is Float, is Double, is Class<*> -> this
  is CharArray -> copyOf().uncheckedCast()
  is BooleanArray -> copyOf().uncheckedCast()
  is ByteArray -> copyOf().uncheckedCast()
  is ShortArray -> copyOf().uncheckedCast()
  is IntArray -> copyOf().uncheckedCast()
  is LongArray -> copyOf().uncheckedCast()
  is FloatArray -> copyOf().uncheckedCast()
  is DoubleArray -> copyOf().uncheckedCast()
  else -> deepClone(HashMap())
}

private fun <T : Any> T.deepClone(clonedMap: MutableMap<Any, Any>): T =
  clonedMap[this]?.uncheckedCast<T>() ?: when (this) {
    is Char, is Boolean, is Byte, is Short, is Int, is Long, is Float, is Double, is Class<*> -> this
    is CharArray -> copyOf().uncheckedCast()
    is BooleanArray -> copyOf().uncheckedCast()
    is ByteArray -> copyOf().uncheckedCast()
    is ShortArray -> copyOf().uncheckedCast()
    is IntArray -> copyOf().uncheckedCast()
    is LongArray -> copyOf().uncheckedCast()
    is FloatArray -> copyOf().uncheckedCast()
    is DoubleArray -> copyOf().uncheckedCast()
    is Array<*> -> {
      val instance = java.lang.reflect.Array.newInstance(javaClass.componentType, size).uncheckedCast<Array<Any?>>()
      forEachIndexed { index, it ->
        instance[index] = it?.deepClone(clonedMap)
      }
      instance.uncheckedCast()
    }
    else -> {
      val clazz = javaClass
      val newInstance = unsafeInstance(clazz)!!
      clonedMap[this] = newInstance
      clazz.forAllFields { field ->
        if (field.isStatic()) return@forAllFields
        field.isAccessible = true
        field.set(newInstance, field.get(this)?.deepClone(clonedMap))
      }
      newInstance
    }
  }

fun Any?.details(name: String = "", skipStatic: Boolean = true) = buildString {
  this@details.details(HashMap(), this, "", name, skipStatic)
}

private fun Any?.details(
  map: MutableMap<Any, Int>,
  builder: StringBuilder,
  indentation: String,
  name: String = "",
  skipStatic: Boolean = true,
  directValue: String = "|- ",
  objectValue: String = "/- ",
  hint: String = "${TextColor.red}|${TextColor.reset} ",
  tmpIndentation: String = "",
  type: String? = null,
) {
  when (this) {
    null, is Char, is Boolean, is Byte, is Short, is Int, is Long, is Float, is Double, is Class<*>, is String ->
      builder.append("$indentation$tmpIndentation$directValue$name(${type ?: this?.javaClass?.name ?: "null"}): $this\n")
    is CharArray -> builder.append("$indentation$tmpIndentation$directValue$name(CharArray): ${String(this)}\n")
    is BooleanArray -> builder.append("$indentation$tmpIndentation$directValue$name(BooleanArray): ${asList()}\n")
    is ByteArray -> builder.append("$indentation$tmpIndentation$directValue$name(ByteArray): 0x${toHexString()}\n")
    is ShortArray -> builder.append("$indentation$tmpIndentation$directValue$name(ShortArray): ${asList()}\n")
    is IntArray -> builder.append("$indentation$tmpIndentation$directValue$name(IntArray): ${asList()}\n")
    is LongArray -> builder.append("$indentation$tmpIndentation$directValue$name(LongArray): ${asList()}\n")
    is FloatArray -> builder.append("$indentation$tmpIndentation$directValue$name(FloatArray): ${asList()}\n")
    is DoubleArray -> builder.append("$indentation$tmpIndentation$directValue$name(DoubleArray): ${asList()}\n")
    is Array<*> -> {
      builder.append("$indentation$tmpIndentation$objectValue$name: (Array<${javaClass.componentType.name}>, ${hashCode()})\n")
      if (this !in map) {
        map[this] = hashCode()
        val newIndentation = "$indentation$hint"
        forEachIndexed { index, it ->
          val newTmpIndentation = "|- ${name.ifEmpty { "array" }}[$index]"
          it.details(
            map, builder, newIndentation,
            skipStatic = skipStatic,
            directValue = " = ",
            objectValue = " = /- ",
            hint = "${TextColor.red}|${TextColor.reset} ${" ".repeat(newTmpIndentation.length + 1)}${TextColor.red}|${TextColor.reset} ",
            tmpIndentation = newTmpIndentation
          )
        }
      }
    }
    in map -> builder.append("$indentation$tmpIndentation$directValue$name${if (name.isNotEmpty()) ": " else ""}(${javaClass.name}, ${map[this]}, generated)\n")
    else -> {
      map[this] = this.hashCode()
      builder.append("$indentation$tmpIndentation$objectValue$name${if (name.isNotEmpty()) ": " else ""}(${javaClass.name}, ${hashCode()})\n")
      val newIndentation = "$indentation$hint"
      javaClass.forAllFields { field ->
        if (skipStatic && field.isStatic()) {
          return@forAllFields
        }
        field.isAccessible = true
        val value = field.get(this)
        value.details(map, builder, newIndentation, field.name, skipStatic, type = field.type.name)
      }
    }
  }
}