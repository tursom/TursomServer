@file:Suppress("unused")

package cn.tursom.core

import sun.misc.Unsafe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
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
import java.util.zip.*
import kotlin.collections.ArrayList


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
val shA384 by lazy { MessageDigest.getInstance("SHA-384")!! }

fun ByteArray.sha384(): ByteArray = shA384.digest(this)

fun String.sha384(): String = toByteArray().sha384().toHexString()

//获取 sha-512 加密对象
val sha512 by lazy { MessageDigest.getInstance("SHA-512")!! }

fun ByteArray.sha512(): ByteArray = sha512.digest(this)

fun String.sha512(): String = toByteArray().sha512().toHexString()

private val HEX_ARRAY = "0123456789abcdef".toCharArray()
fun ByteArray.toHexString(): String {
  val hexChars = CharArray(size * 2)
  for (i in indices) {
    val b = this[i].toInt()
    hexChars[i shl 1] = HEX_ARRAY[b ushr 4 and 0x0F]
    hexChars[(i shl 1) + 1] = HEX_ARRAY[b and 0x0F]
  }
  return String(hexChars)
}

fun String.fromHexString(): ByteArray {
  val source = toLowerCase()
  val data = ByteArray(length / 2)
  for (i in 0 until length / 2) {
    data[i] = ((HEX_ARRAY.indexOf(source[i * 2]) shl 4) + HEX_ARRAY.indexOf(source[i * 2 + 1])).toByte()
  }
  return data
}

fun ByteArray.toUTF8String() = String(this, Charsets.UTF_8)

fun String.base64() = this.toByteArray().base64().toUTF8String()

fun ByteArray.base64(): ByteArray {
  return Base64.getEncoder().encode(this)
}

fun String.base64decode() = Base64.getDecoder().decode(this).toUTF8String()

fun ByteArray.base64decode(): ByteArray = Base64.getDecoder().decode(this)

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

operator fun <T> (() -> T).unaryPlus() = object {
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

@Suppress("UNCHECKED_CAST")
fun <T> Any?.cast(): T = this as T

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