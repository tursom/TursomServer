@file:Suppress("unused")

package cn.tursom.core

import sun.misc.Unsafe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.Executor
import java.util.jar.JarFile
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.Inflater
import kotlin.collections.ArrayList
import kotlin.experimental.and


inline fun <reified T> Array<out T?>.excludeNull(): List<T> {
  val list = ArrayList<T>()
  forEach { if (it != null) list.add(it) }
  return list
}

fun printNonDaemonThread() {
  val currentGroup = Thread.currentThread().threadGroup
  val noThreads = currentGroup.activeCount()
  val lstThreads = arrayOfNulls<Thread>(noThreads)
  currentGroup.enumerate(lstThreads)
  lstThreads.excludeNull().forEach { t ->
    if (!t.isDaemon) {
      log(t.name)
    }
  }
  println()
}

fun log(log: String) = println("${ThreadLocalSimpleDateFormat.standard.format(System.currentTimeMillis())}: $log")
fun logE(log: String) =
  System.err.println("${ThreadLocalSimpleDateFormat.standard.format(System.currentTimeMillis())}: $log")

val String.urlDecode: String get() = URLDecoder.decode(this, "utf-8")
val String.urlEncode: String get() = URLEncoder.encode(this, "utf-8")

inline fun <T> usingTime(action: () -> T): Long {
  val t1 = System.currentTimeMillis()
  action()
  val t2 = System.currentTimeMillis()
  return t2 - t1
}

inline fun <T> usingNanoTime(action: () -> T): Long {
  val t1 = System.nanoTime()
  action()
  val t2 = System.nanoTime()
  return t2 - t1
}

inline fun <T> Collection<T>.toString(action: (T) -> Any): String {
  val iterator = iterator()
  if (!iterator.hasNext()) return "[]"
  val sb = StringBuilder("[${action(iterator.next())}")
  iterator.forEach {
    sb.append(", ")
    sb.append(action(it))
  }
  sb.append("]")
  return sb.toString()
}

val unsafe by lazy {
  val field = Unsafe::class.java.getDeclaredField("theUnsafe")
  field.isAccessible = true
  field.get(null) as Unsafe
}

@Suppress("UNCHECKED_CAST")
fun <T> Class<T>.unsafeInstance() = unsafe.allocateInstance(this) as T

val Class<*>.actualTypeArguments: Array<out Type>
  get() = (genericSuperclass as ParameterizedType).actualTypeArguments

fun Class<*>.isInheritanceFrom(parent: Class<*>) = parent.isAssignableFrom(this)

fun getClassName(jarPath: String): List<String> {
  val myClassName = ArrayList<String>()
  for (entry in JarFile(jarPath).entries()) {
    val entryName = entry.name
    if (entryName.endsWith(".class")) {
      myClassName.add(entryName.replace("/", ".").substring(0, entryName.lastIndexOf(".")))
    }
  }
  return myClassName
}

fun <T> List<T>.binarySearch(comparison: (T) -> Int): T? {
  val index = binarySearch(0, size, comparison)
  return if (index < 0) null
  else get(index)
}

val cpuNumber = Runtime.getRuntime().availableProcessors()

fun String.simplifyPath(): String {
  if (isEmpty()) {
    return "."
  }
  val pathList = split(java.io.File.separator).dropLastWhile { it.isEmpty() }
  val list = LinkedList<String>()
  for (path in pathList) {
    if (path.isEmpty() || "." == path) {
      continue
    }
    if (".." == path) {
      list.pollLast()
      continue
    }
    list.addLast(path)
  }
  var result = ""
  while (list.size > 0) {
    result += java.io.File.separator + list.pollFirst()!!
  }
  return if (result.isNotEmpty()) result else "."
}

//获取md5加密对象
val md5 by lazy { MessageDigest.getInstance("MD5")!! }

fun ByteArray.md5(): ByteArray {
  //加密，返回字节数组
  return md5.digest(this)
}

fun String.md5(): String = toByteArray().md5().toHexString()


//获取md5加密对象
val sha256 by lazy { MessageDigest.getInstance("SHA-256")!! }

fun ByteArray.sha256(): ByteArray {
  //加密，返回字节数组
  return sha256.digest(this)
}

fun String.sha256(): String = toByteArray().sha256().toHexString()

//获取sha加密对象
val sha by lazy { MessageDigest.getInstance("SHA")!! }

fun ByteArray.sha(): ByteArray = sha.digest(this)

fun String.sha(): String = toByteArray().sha().toHexString()

//获取sha1加密对象
val sha1 by lazy { MessageDigest.getInstance("SHA-1")!! }

fun ByteArray.sha1(): ByteArray = sha1.digest(this)

fun String.sha1(): String = toByteArray().sha1().toHexString()

//获取sha384加密对象
val sha384 by lazy { MessageDigest.getInstance("SHA-384")!! }

fun ByteArray.sha384(): ByteArray = sha384.digest(this)

fun String.sha384(): String = toByteArray().sha384().toHexString()

//获取 sha-512 加密对象
val sha512 by lazy { MessageDigest.getInstance("SHA-512")!! }

fun ByteArray.sha512(): ByteArray = sha512.digest(this)

fun String.sha512(): String = toByteArray().sha512().toHexString()


fun ByteArray.toHexString(upper: Boolean = true): String = if (upper) toUpperHexString() else toLowerHexString()

private val UPPER_HEX_ARRAY = "0123456789ABCDEF".toCharArray()
fun ByteArray.toUpperHexString(): String {
  val hexChars = CharArray(size * 2)
  for (i in indices) {
    val b = this[i]
    hexChars[i shl 1] = UPPER_HEX_ARRAY[b.toInt() ushr 4 and 0x0F]
    hexChars[(i shl 1) + 1] = UPPER_HEX_ARRAY[(b and 0x0F).toInt()]
  }
  return String(hexChars)
}

private val LOWER_HEX_ARRAY = "0123456789abcdef".toCharArray()
fun ByteArray.toLowerHexString(): String {
  val hexChars = CharArray(size * 2)
  for (i in indices) {
    val b = this[i]
    hexChars[i shl 1] = LOWER_HEX_ARRAY[b.toInt() ushr 4 and 0x0F]
    hexChars[(i shl 1) + 1] = LOWER_HEX_ARRAY[(b and 0x0F).toInt()]
  }
  return String(hexChars)
}

fun String.fromHexString(): ByteArray {
  val source = toLowerCase()
  val data = ByteArray(length / 2)
  for (i in 0 until length / 2) {
    data[i] = ((LOWER_HEX_ARRAY.indexOf(source[i * 2]) shl 4) + LOWER_HEX_ARRAY.indexOf(source[i * 2 + 1])).toByte()
  }
  return data
}

fun ByteArray.toUTF8String() = String(this, Charsets.UTF_8)

fun String.base64(): String = this.toByteArray().base64().toUTF8String()
fun ByteArray.base64(): ByteArray = Base64.getEncoder().encode(this)
fun String.base64Url(): String = this.toByteArray().base64Url().toUTF8String()
fun ByteArray.base64Url(): ByteArray = Base64.getUrlEncoder().encode(this)
fun String.base64Mime(): String = this.toByteArray().base64Mime().toUTF8String()
fun ByteArray.base64Mime(): ByteArray = Base64.getMimeEncoder().encode(this)

fun String.base64decode(): String = Base64.getDecoder().decode(this).toUTF8String()
fun ByteArray.base64decode(): ByteArray = Base64.getDecoder().decode(this)
fun String.base64UrlDecode(): String = Base64.getUrlDecoder().decode(this).toUTF8String()
fun ByteArray.base64UrlDecode(): ByteArray = Base64.getUrlDecoder().decode(this)
fun String.base64MimeDecode(): String = Base64.getMimeDecoder().decode(this).toUTF8String()
fun ByteArray.base64MimeDecode(): ByteArray = Base64.getMimeDecoder().decode(this)

fun String.digest(type: String) = toByteArray().digest(type)?.toHexString()

fun ByteArray.digest(type: String) = try {
  //获取加密对象
  val instance = MessageDigest.getInstance(type)
  //加密，返回字节数组
  instance.digest(this)
} catch (e: NoSuchAlgorithmException) {
  e.printStackTrace()
  null
}

val random = Random()
fun randomInt() = random.nextInt()
fun randomInt(min: Int, max: Int): Int =
  if (min > max) randomInt(max, min) else (random.nextInt() and Int.MAX_VALUE) % (max - min + 1) + min

fun randomLong() = random.nextLong()
fun randomLong(min: Long, max: Long) = (random.nextLong() and Long.MAX_VALUE) % (max - min + 1) + min
fun randomBoolean() = random.nextBoolean()
fun randomFloat() = random.nextFloat()
fun randomDouble() = random.nextDouble()
fun randomGaussian() = random.nextGaussian()
fun randomBytes(bytes: ByteArray) = random.nextBytes(bytes)

fun getTAG(cls: Class<*>): String {
  return cls.name.split(".").last().dropLast(10)
}

operator fun <T> (() -> T).unaryPlus() = object : () -> T {
  override fun invoke(): T = this@unaryPlus()
  override fun toString() = this@unaryPlus().toString()
}

operator fun Executor.invoke(action: () -> Unit) {
  execute(action)
}

operator fun Executor.invoke(action: Runnable) = this(action::run)

inline fun <reified T : Annotation> Field.getAnnotation(): T? = getAnnotation(T::class.java)

inline fun <reified T : Annotation> Class<*>.getAnnotation(): T? = getAnnotation(T::class.java)

fun process(size: Int, vararg actions: () -> Unit) {
  actions.forEachIndexed { index, function ->
    if (actions.size - index <= size) function()
  }
}

fun <T> process(value: T, vararg actions: Pair<T, () -> Unit>) {
  var checked = false
  actions.forEach { (v, function) ->
    if (checked || value == v) {
      checked = true
      function()
    }
  }
}

fun <T> T.println(): T {
  println(this)
  return this
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.cast(): T = this as T

inline fun loop(`continue`: () -> Boolean = { true }, action: () -> Unit) {
  while (`continue`()) action()
}

inline fun <reified T> getClazz() = T::class.java

@Suppress("NOTHING_TO_INLINE")
inline fun <R, T : Function<R>> lambda(lambda: T) = lambda

fun ByteArray.gz(): ByteArray {
  val os = ByteArrayOutputStream()
  GZIPOutputStream(os).use {
    it.write(this)
  }
  return os.toByteArray()
}

fun ByteArray.ungz(): ByteArray {
  return GZIPInputStream(ByteArrayInputStream(this)).readBytes()
}

fun ByteArray.undeflate(): ByteArray {
  var len = 0
  val infl = Inflater()
  infl.setInput(this)
  val bos = ByteArrayOutputStream()
  val outByte = ByteArray(1024)
  bos.use {
    while (!infl.finished()) {
      // 解压缩并将解压缩后的内容输出到字节输出流bos中
      len = infl.inflate(outByte)
      if (len == 0) {
        break
      }
      bos.write(outByte, 0, len)
    }
    infl.end()
  }
  return bos.toByteArray()
}


fun ByteArray.deflate(): ByteArray {
  var len = 0
  val defl = Deflater()
  defl.setInput(this)
  defl.finish()
  val bos = ByteArrayOutputStream()
  val outputByte = ByteArray(1024)
  bos.use {
    while (!defl.finished()) {
      // 压缩并将压缩后的内容输出到字节输出流bos中
      len = defl.deflate(outputByte)
      bos.write(outputByte, 0, len)
    }
    defl.end()
  }
  return bos.toByteArray()
}

//fun ByteArray.deflate(): ByteArray {
//  val os = ByteArrayOutputStream()
//  DeflaterOutputStream(os).use {
//    it.write(this)
//  }
//  return os.toByteArray()
//}
//
//fun ByteArray.undeflate(): ByteArray {
//  return DeflaterInputStream(ByteArrayInputStream(this)).readBytes()
//}

inline fun <reified T : Any?> Any.assert(action: T.() -> Unit): Boolean {
  return if (this is T) {
    action()
    true
  } else {
    false
  }
}

val Class<*>.allFields: List<Field>
  get() {
    var clazz = this
    val list = ArrayList<Field>()
    while (clazz != Any::class.java) {
      list.addAll(clazz.declaredFields)
      clazz = clazz.superclass
    }
    list.addAll(clazz.declaredFields)
    return list
  }

fun Class<*>.forAllFields(action: (Field) -> Unit) {
  var clazz = this
  while (clazz != Any::class.java) {
    clazz.declaredFields.forEach(action)
    clazz = clazz.superclass
  }
  clazz.declaredFields.forEach(action)
}