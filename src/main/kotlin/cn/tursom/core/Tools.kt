@file:Suppress("unused")

package cn.tursom.core

import sun.misc.Unsafe
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.jar.JarFile
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
      println("${System.currentTimeMillis()}: ${t.name}")
    }
  }
  println()
}

fun log(log: String) = println("${System.currentTimeMillis()}: $log")
fun logE(log: String) = System.err.println("${System.currentTimeMillis()}: $log")


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

fun ByteArray.md5(): ByteArray? {
  return try {
    //获取md5加密对象
    val instance = MessageDigest.getInstance("MD5")
    //加密，返回字节数组
    instance.digest(this)
  } catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
  }
}

fun String.md5(): String? {
  return toByteArray().md5()?.toHexString()
}

fun ByteArray.sha256(): ByteArray? {
  return try {
    //获取md5加密对象
    val instance = MessageDigest.getInstance("SHA-256")
    //加密，返回字节数组
    instance.digest(this)
  } catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
  }
}

fun String.sha256(): String? {
  return toByteArray().sha256()?.toHexString()
}

fun ByteArray.sha(): ByteArray? {
  return try {
    //获取md5加密对象
    val instance = MessageDigest.getInstance("SHA")
    //对字符串加密，返回字节数组
    instance.digest(this)
  } catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
  }
}

fun String.sha(): String? = toByteArray().sha()?.toHexString()

fun ByteArray.sha1(): ByteArray? {
  return try {
    //获取md5加密对象
    val instance = MessageDigest.getInstance("SHA-1")
    //对字符串加密，返回字节数组
    instance.digest(this)
  } catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
  }
}

fun String.sha1(): String? = toByteArray().sha1()?.toHexString()

fun ByteArray.sha384(): ByteArray? {
  return try {
    //获取md5加密对象
    val instance = MessageDigest.getInstance("SHA-384")
    //对字符串加密，返回字节数组
    instance.digest(this)
  } catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
  }
}

fun String.sha384(): String? = toByteArray().sha384()?.toHexString()

fun ByteArray.sha512(): ByteArray? {
  return try {
    //获取md5加密对象
    val instance = MessageDigest.getInstance("SHA-512")
    //对字符串加密，返回字节数组
    instance.digest(this)
  } catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
  }
}

fun String.sha512(): String? = toByteArray().sha512()?.toHexString()

fun ByteArray.toHexString(): String? {
  val sb = StringBuilder()
  forEach {
    //获取低八位有效值+
    val i: Int = it.toInt() and 0xff
    //将整数转化为16进制
    var hexString = Integer.toHexString(i)
    if (hexString.length < 2) {
      //如果是一位的话，补0
      hexString = "0$hexString"
    }
    sb.append(hexString)
  }
  return sb.toString()
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

fun randomInt(min: Int, max: Int) = Random().nextInt(max) % (max - min + 1) + min


fun getTAG(cls: Class<*>): String {
  return cls.name.split(".").last().dropLast(10)
}
