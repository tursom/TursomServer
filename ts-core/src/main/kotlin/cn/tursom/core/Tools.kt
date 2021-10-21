@file:Suppress("unused")

package cn.tursom.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import sun.reflect.Reflection
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.Executor
import java.util.zip.Deflater
import java.util.zip.Inflater
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.and
import kotlin.jvm.internal.PropertyReference
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses


object Utils {
  const val dollar = '$'
  val random = Random(System.currentTimeMillis())

  val bufferThreadLocal = SimpThreadLocal { ByteArray(1024) }

  @Suppress("unused", "SpellCheckingInspection")
  val gson: Gson by lazy {
    GsonBuilder()
      .registerTypeAdapterFactory(GsonDataTypeAdaptor.FACTORY)
      .registerTypeAdapterFactory(EnumTypeAdapterFactory)
      .create()
  }

  @Suppress("unused", "SpellCheckingInspection")
  val prettyGson: Gson by lazy {
    GsonBuilder()
      .registerTypeAdapterFactory(GsonDataTypeAdaptor.FACTORY)
      .registerTypeAdapterFactory(EnumTypeAdapterFactory)
      .setPrettyPrinting()
      .create()
  }

  @Suppress("SpellCheckingInspection")
  internal val UPPER_HEX_ARRAY = "0123456789ABCDEF".toCharArray()

  @Suppress("SpellCheckingInspection")
  internal val LOWER_HEX_ARRAY = "0123456789abcdef".toCharArray()

  val md5 by lazy { MessageDigest.getInstance("MD5")!! }
  val sha256 by lazy { MessageDigest.getInstance("SHA-256")!! }
  val sha by lazy { MessageDigest.getInstance("SHA")!! }
  val sha1 by lazy { MessageDigest.getInstance("SHA-1")!! }
  val sha384 by lazy { MessageDigest.getInstance("SHA-384")!! }
  val sha512 by lazy { MessageDigest.getInstance("SHA-512")!! }

  @Suppress("SpellCheckingInspection")
  internal val DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()

  val receiverField: Field by lazy {
    kotlin.jvm.internal.CallableReference::class.java.getDeclaredField("receiver").apply { isAccessible = true }
  }
  val ownerField: Field by lazy {
    kotlin.jvm.internal.CallableReference::class.java.getDeclaredField("owner").apply { isAccessible = true }
  }

  val strValue = String::class.java.declaredFields.firstOrNull {
    it.type == CharArray::class.java
  }?.also {
    it.isAccessible = true
    it.final = false
  }
}

fun CharArray.packageToString(): String {
  return if (size < 64 * 1024 || Utils.strValue == null) {
    String(this)
  } else {
    val str = String()
    Utils.strValue.set(str, this)
    str
  }
}

fun String.hexStringToByteArray(): ByteArray {
  val len = length
  val data = ByteArray(len / 2)
  var i = 0
  while (i < len) {
    data[i / 2] = (Character.digit(this[i], 16) shl 4 or Character.digit(this[i + 1], 16)).toByte()
    i += 2
  }
  return data
}


fun ByteArray.toHexString(upper: Boolean = true): String = if (upper) toUpperHexString() else toLowerHexString()

fun ByteArray.toUpperHexString(): String {
  val hexChars = CharArray(size * 2)
  for (i in indices) {
    val b = this[i]
    hexChars[i shl 1] = Utils.UPPER_HEX_ARRAY[b.toInt() ushr 4 and 0x0F]
    hexChars[(i shl 1) + 1] = Utils.UPPER_HEX_ARRAY[(b and 0x0F).toInt()]
  }
  return String(hexChars)
}

fun ByteArray.toLowerHexString(): String {
  val hexChars = CharArray(size * 2)
  for (i in indices) {
    val b = this[i]
    hexChars[i shl 1] = Utils.LOWER_HEX_ARRAY[b.toInt() ushr 4 and 0x0F]
    hexChars[(i shl 1) + 1] = Utils.LOWER_HEX_ARRAY[(b and 0x0F).toInt()]
  }
  return String(hexChars)
}


inline fun <T, R : Any> Iterable<T>.toSetNotNull(transform: (T) -> R?): Set<R> {
  return HashSet<R>().apply { this@toSetNotNull.forEach { add(transform(it) ?: return@forEach) } }
}


@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
//@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class UncheckedCast

@UncheckedCast
@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
inline fun <T> Any?.cast() = this as T

@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
inline fun <T> Any?.uncheckedCast() = this as T

@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
inline fun <T> Any?.uncheckedCastNullable() = this as? T

@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
inline fun <reified T> Any?.castOrNull() = if (this is T) this else null

inline fun <T> T?.checkNull(ifNull: () -> Exception): T {
  if (this == null) {
    throw ifNull()
  } else {
    return this
  }
}

fun String.emptyToNull() = ifEmpty { null }

inline fun <reified T> getClazz() = T::class.java

fun Class<*>.isInheritanceFrom(parent: Class<*>) = parent.isAssignableFrom(this)

operator fun <T> (() -> T).unaryPlus() = object : () -> T {
  override operator fun invoke() = this@unaryPlus()
  override fun toString(): String = this@unaryPlus().toString()
}

fun String.toLowerCase(vararg indexes: Int): String {
  val charArray = toCharArray()
  indexes.forEach { index ->
    charArray[index] = charArray[index].toLowerCase()
  }
  return String(charArray)
}

fun String.toUpperCase(vararg indexes: Int): String {
  val charArray = toCharArray()
  indexes.forEach { index ->
    charArray[index] = charArray[index].toUpperCase()
  }
  return String(charArray)
}

fun String.toLowerCase(indexes: IntRange): String {
  val charArray = toCharArray()
  indexes.forEach { index ->
    charArray[index] = charArray[index].toLowerCase()
  }
  return String(charArray)
}

fun String.toUpperCase(indexes: IntRange): String {
  val charArray = toCharArray()
  indexes.forEach { index ->
    charArray[index] = charArray[index].toUpperCase()
  }
  return String(charArray)
}

fun ByteArray.md5(): ByteArray {
  return Utils.md5.digest(this)
}

fun String.md5(): String = toByteArray().md5().toHexString()


fun ByteArray.sha256(): ByteArray {
  return Utils.sha256.digest(this)
}

fun String.sha256(): String = toByteArray().sha256().toHexString()


fun ByteArray.sha(): ByteArray = Utils.sha.digest(this)

fun String.sha(): String = toByteArray().sha().toHexString()


fun ByteArray.sha1(): ByteArray = Utils.sha1.digest(this)

fun String.sha1(): String = toByteArray().sha1().toHexString()


fun ByteArray.sha384(): ByteArray = Utils.sha384.digest(this)

fun String.sha384(): String = toByteArray().sha384().toHexString()

fun ByteArray.sha512(): ByteArray = Utils.sha512.digest(this)

fun String.sha512(): String = toByteArray().sha512().toHexString()

fun String.fromHexString(): ByteArray {
  val source = toLowerCase()
  val data = ByteArray(length / 2)
  for (i in 0 until length / 2) {
    data[i] =
      ((Utils.UPPER_HEX_ARRAY.indexOf(source[i * 2]) shl 4) + Utils.UPPER_HEX_ARRAY.indexOf(source[i * 2 + 1])).toByte()
  }
  return data
}

fun ByteArray.toUTF8String() = String(this, Charsets.UTF_8)

fun String.base64() = this.toByteArray().base64().toUTF8String()
fun ByteArray.base64(): ByteArray = Base64.getEncoder().encode(this)
fun String.base64Url(): String = this.toByteArray().base64Url().toUTF8String()
fun ByteArray.base64Url(): ByteArray = Base64.getUrlEncoder().encode(this)
fun String.base64Mime(): String = this.toByteArray().base64Mime().toUTF8String()
fun ByteArray.base64Mime(): ByteArray = Base64.getMimeEncoder().encode(this)

fun String.base64decode() = Base64.getDecoder().decode(this).toUTF8String()
fun ByteArray.base64decode(): ByteArray = Base64.getDecoder().decode(this)
fun String.base64UrlDecode(): String = Base64.getUrlDecoder().decode(this).toUTF8String()
fun ByteArray.base64UrlDecode(): ByteArray = Base64.getUrlDecoder().decode(this)
fun String.base64MimeDecode(): String = Base64.getMimeDecoder().decode(this).toUTF8String()
fun ByteArray.base64MimeDecode(): ByteArray = Base64.getMimeDecoder().decode(this)

fun String.digest(type: String) = toByteArray().digest(type)?.toHexString()

fun ByteArray.digest(type: String) = try {
  val instance = MessageDigest.getInstance(type)
  instance.digest(this)
} catch (e: NoSuchAlgorithmException) {
  e.printStackTrace()
  null
}

fun <A : Annotation, V : Any> A.changeAnnotationValue(field: KProperty1<A, V>, value: V): Boolean {
  return try {
    val h = Proxy.getInvocationHandler(this)
    val memberValuesField = h.javaClass.getDeclaredField("memberValues")
    memberValuesField.isAccessible = true
    val memberValues = memberValuesField[h].uncheckedCast<MutableMap<String, Any>>()
    memberValues[field.name] = value
    true
  } catch (e: Exception) {
    false
  }
}

/**
 * 向数据库提交一项任务并获取返回值
 */
suspend fun <T> Executor.runWith(action: () -> T): T = suspendCoroutine { cont ->
  execute {
    try {
      cont.resume(action())
    } catch (e: Exception) {
      cont.resumeWithException(e)
    }
  }
}

inline fun <reified T : Any> Gson.fromJson(json: String): T = fromJson(json, T::class.java)

inline fun usingTime(action: () -> Unit): Long {
  val t1 = System.currentTimeMillis()
  action()
  val t2 = System.currentTimeMillis()
  return t2 - t1
}

inline fun usingNanoTime(action: () -> Unit): Long {
  val t1 = System.nanoTime()
  action()
  val t2 = System.nanoTime()
  return t2 - t1
}

inline fun Class<*>.forAllFields(action: (Field) -> Unit) {
  allFieldsSequence.forEach(action)
}

val Class<*>.allFields: List<Field>
  get() {
    val fieldList = ArrayList<Field>()
    forAllFields(fieldList::add)
    return fieldList
  }

val Class<*>.allFieldsSequence: Sequence<Field>
  get() = sequence {
    var clazz = this@allFieldsSequence
    while (clazz != Any::class.java) {
      clazz.declaredFields.forEach { field ->
        yield(field)
      }
      clazz = clazz.superclass
    }
  }

fun Class<*>.getFieldForAll(name: String): Field? {
  forAllFields {
    if (it.name == name) return it
  }
  return null
}

inline fun Class<*>.forAllMethods(action: (Method) -> Unit) {
  allMethodsSequence.forEach(action)
}

fun Class<*>.getMethodForAll(name: String, vararg parameterTypes: Class<*>?): Method? {
  forAllMethods {
    if (it.name == name && parameterTypes.contentEquals(it.parameterTypes)) return it
  }
  return null
}

val Class<*>.allMethods: List<Method>
  get() {
    val fieldList = ArrayList<Method>()
    forAllMethods(fieldList::add)
    return fieldList
  }

val Class<*>.allMethodsSequence: Sequence<Method>
  get() = sequence {
    var clazz = this@allMethodsSequence
    while (clazz != Any::class.java) {
      clazz.declaredMethods.forEach {
        yield(it)
      }
      clazz = clazz.superclass
    }
    clazz.declaredMethods.forEach {
      yield(it)
    }
  }

/**
 * 获取一个 KProperty<*> 对应的对象
 */
val KProperty<*>.receiver: Any?
  get() = if (this is PropertyReference) {
    boundReceiver
  } else try {
    Utils.receiverField.get(this)
  } catch (e: Exception) {
    null
  } ?: javaClass.getFieldForAll("receiver")?.let {
    it.isAccessible = true
    it.get(this)
  }

val KProperty<*>.owner: Class<*>?
  get() = try {
    Utils.ownerField.get(this)?.uncheckedCast<Class<*>>()
  } catch (e: Exception) {
    null
  } ?: javaClass.getFieldForAll("owner")?.let {
    it.isAccessible = true
    it.get(this)?.castOrNull()
  }

tailrec fun Long.base62(sBuilder: StringBuilder = StringBuilder()): String {
  return if (this == 0L) {
    sBuilder.reverse().toString()
  } else {
    val remainder = (this % 62).toInt()
    sBuilder.append(Utils.DIGITS[remainder])
    (this / 62).base62(sBuilder)
  }
}

fun String.base62Decode(): Long {
  var sum: Long = 0
  val len = length
  var base = 1L
  for (i in 0 until len) {
    sum += Utils.DIGITS.indexOf(this[len - i - 1]) * base
    base *= 62
  }
  return sum
}

fun Any.toJson(): String = Utils.gson.toJson(this)
fun Any.toPrettyJson(): String = Utils.prettyGson.toJson(this)

inline fun <reified T : Any> String.fromJson(): T = Utils.gson.fromJson(this, T::class.java)

fun Any.serialize(): ByteArray {
  val outputStream = ByteArrayOutputStream()
  ObjectOutputStream(outputStream).writeObject(this)
  return outputStream.toByteArray()
}

/**
 * 使用 condition 做条件判断，如果返回 true 则使用 then 生成结果，否则范湖自身
 */
inline fun <T> T.ifThen(condition: T.() -> Boolean, then: () -> T) = if (condition()) then() else this

@JvmName("ifThenNullable")
inline fun <T> T.ifThen(condition: T.() -> Boolean, then: () -> T?) = if (condition()) then() else this

inline fun <T> Any.wait(action: () -> T) = synchronized(this) {
  val t = action()
  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  (this as Object).wait()
  t
}

inline fun <T> Any.notify(action: () -> T) = synchronized(this) {
  val t = action()
  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  (this as Object).notify()
  t
}

inline fun <T> Any.notifyAll(action: () -> T) = synchronized(this) {
  val t = action()
  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  (this as Object).notifyAll()
  t
}

inline val KClass<*>.companionObjectInstanceOrNull: Any?
  get() = try {
    companionObjectInstance
  } catch (e: Exception) {
    null
  }

inline val <K : Any, V> Map<K?, V>.notNullKey get() = uncheckedCast<Map<K, V>>()
inline val <K, V : Any> Map<K, V?>.notNullValue get() = uncheckedCast<Map<K, V>>()
inline val <K : Any, V : Any> Map<K?, V?>.notNullEntry get() = uncheckedCast<Map<K, V>>()

inline val <K : Any, V> Map<K?, V>.filterNullKey get() = filter { it.key != null }.notNullKey
inline val <K, V : Any> Map<K, V?>.filterNullValue get() = filter { it.value != null }.notNullValue

val <T : Any> KClass<T>.allMemberProperties: List<KProperty1<T, *>>
  get() {
    val propertiesList = memberProperties.toMutableList()
    var superClass = superclasses.firstOrNull {
      !it.java.isInterface
    }
    while (superClass != null) {
      propertiesList.addAll(superClass.memberProperties.uncheckedCast())
      superClass = superClass.superclasses.firstOrNull {
        !it.java.isInterface
      }
    }
    return propertiesList
  }

fun String.toStartWith(prefix: String) = if (startsWith(prefix)) this else prefix + this
fun String.toStartWith(prefix: Char) = if (startsWith(prefix)) this else prefix + this


fun mongoLegal(value: Any?) = when {
  value == null -> true
  value is Number -> true
  value is Boolean -> true
  value is Char -> true
  value is String -> true
  value is Serializable -> true
  value.javaClass.kotlin.isData -> true
  value.javaClass.name.endsWith("DTO") -> true
  value.javaClass.name.endsWith("VO") -> true
  else -> false
}

fun getCallerClass(thisClassName: List<String>): Class<*>? {
  var clazz: Class<*>?
  var callStackDepth = 1
  do {
    clazz = getCallerClass(callStackDepth++)
    val clazzName = clazz?.name
    if (clazzName != "cn.tursom.core.ToolsKt" && clazzName !in thisClassName) {
      break
    }
  } while (clazz != null)
  return clazz
}

fun getCallerClassName(thisClassName: List<String>): String? {
  return getCallerClass(thisClassName)?.name
}

fun getCallerClass(callStackDepth: Int): Class<*>? {
  @Suppress("DEPRECATION")
  return Reflection.getCallerClass(callStackDepth)
}

fun getCallerClassName(callStackDepth: Int): String? {
  return getCallerClass(callStackDepth)?.name
}

@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNotNullOrEmpty(): Boolean {
  contract {
    returns(true) implies (this@isNotNullOrEmpty != null)
  }

  return this != null && this.isNotEmpty()
}

@OptIn(ExperimentalContracts::class)
fun Collection<*>?.isNotNullOrEmpty(): Boolean {
  contract {
    returns(true) implies (this@isNotNullOrEmpty != null)
  }

  return this != null && this.isNotEmpty()
}

//@OptIn(ExperimentalContracts::class)
//fun main() {
//    val s: String? = ""
//    if (s.isNotNullAndEmpty()) {
//        println(s.length)
//    }
//}


inline fun <reified T> Any?.assert(ifMatch: T.() -> Unit) = if (this is T) {
  ifMatch()
  true
} else {
  false
}

val cpuNumber get() = Runtime.getRuntime().availableProcessors()

@Suppress("NOTHING_TO_INLINE")
inline fun <R, T : Function<R>> lambda(lambda: T) = lambda

val String.urlDecode: String get() = URLDecoder.decode(this, "utf-8")
val String.urlEncode: String get() = URLEncoder.encode(this, "utf-8")

fun <T> List<T>.binarySearch(comparison: (T) -> Int): T? {
  val index = binarySearch(0, size, comparison)
  return if (index < 0) null
  else get(index)
}

fun ByteArray.undeflate(): ByteArray {
  val inf = Inflater()
  inf.setInput(this)
  val bos = ByteArrayOutputStream()
  val outByte = Utils.bufferThreadLocal.get()
  bos.use {
    while (!inf.finished()) {
      val len = inf.inflate(outByte)
      if (len == 0) {
        break
      }
      bos.write(outByte, 0, len)
    }
    inf.end()
  }
  return bos.toByteArray()
}

fun ByteArray.deflate(): ByteArray {
  val def = Deflater()
  def.setInput(this)
  def.finish()
  val bos = ByteArrayOutputStream()
  val outputByte = Utils.bufferThreadLocal.get()
  bos.use {
    while (!def.finished()) {
      val len = def.deflate(outputByte)
      bos.write(outputByte, 0, len)
    }
    def.end()
  }
  return bos.toByteArray()
}

fun StringBuilder.removeLastChars(count: Int) {
  setLength(length - count)
}

inline operator fun <reified T> Array<out T>.plus(other: Array<out T>): Array<T> {
  val array = arrayOfNulls<T>(size + other.size)
  System.arraycopy(this, 0, array, 0, size)
  System.arraycopy(other, 0, array, size, other.size)
  return array.uncheckedCast()
}
