@file:Suppress("unused")

package cn.tursom.core

import com.ddbes.common.monitor.annotation.POJO
import com.ddbes.kotlin.datastruck.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.Signature
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.event.Level
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import sun.reflect.Reflection
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import javax.servlet.http.HttpServletRequest
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

    internal val UPPER_HEX_ARRAY = "0123456789ABCDEF".toCharArray()
    internal val LOWER_HEX_ARRAY = "0123456789abcdef".toCharArray()
    val md5 by lazy { MessageDigest.getInstance("MD5")!! }
    val sha256 by lazy { MessageDigest.getInstance("SHA-256")!! }
    val sha by lazy { MessageDigest.getInstance("SHA")!! }
    val sha1 by lazy { MessageDigest.getInstance("SHA-1")!! }
    val sha384 by lazy { MessageDigest.getInstance("SHA-384")!! }
    val sha512 by lazy { MessageDigest.getInstance("SHA-512")!! }

    internal val DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()

    val receiverField: Field by lazy {
        kotlin.jvm.internal.CallableReference::class.java.getDeclaredField("receiver").apply { isAccessible = true }
    }
    val ownerField: Field by lazy {
        kotlin.jvm.internal.CallableReference::class.java.getDeclaredField("owner").apply { isAccessible = true }
    }

    private val bufferThreadLocal = SimpThreadLocal { ByteArray(8192) }
    val threadLocalBuffer get() = bufferThreadLocal.get()
}

fun <T> T.asResult(): Result<T> = Result.typedSuccess(this)

@OptIn(UncheckedCast::class)
val JoinPoint.method: Method
    get() = signature.cast<MethodSignature>().method

@OptIn(UncheckedCast::class)
val Signature.method: Method
    get() = cast<MethodSignature>().method

@OptIn(UncheckedCast::class)
fun getRequest(): HttpServletRequest = RequestContextHolder
    .getRequestAttributes()
    ?.resolveReference(RequestAttributes.REFERENCE_REQUEST)
    .cast()

private const val UNKNOWN = "unknown"
private const val LOCALHOST = "127.0.0.1"
fun getIpAddr(request: HttpServletRequest): String? {
    var ipAddress: String?
    try {
        ipAddress = request.getHeader("x-forwarded-for")
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equals(ipAddress, true)) {
            ipAddress = request.getHeader("Proxy-Client-IP")
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equals(ipAddress, true)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equals(ipAddress, true)) {
            ipAddress = request.remoteAddr
            if (LOCALHOST == ipAddress) {
                var inet: InetAddress? = null
                try {
                    inet = InetAddress.getLocalHost()
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }
                ipAddress = inet!!.hostAddress
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        // "***.***.***.***".length()
        if (ipAddress != null && ipAddress.length > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","))
            }
        }
    } catch (e: Exception) {
        ipAddress = ""
    }
    return ipAddress
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

inline fun <T, R : Any> Array<T>.toSetNotNull(transform: (T) -> R?): Set<R> {
    return LinkedHashSet<R>().apply { this@toSetNotNull.forEach { add(transform(it) ?: return@forEach) } }
}

inline fun <T, R : Any> Iterable<T>.toSetNotNull(transform: (T) -> R?): Set<R> {
    return LinkedHashSet<R>().apply { this@toSetNotNull.forEach { add(transform(it) ?: return@forEach) } }
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

fun Logger.isEnabled(level: Level) = when (level) {
    Level.ERROR -> isErrorEnabled
    Level.WARN -> isWarnEnabled
    Level.INFO -> isInfoEnabled
    Level.DEBUG -> isDebugEnabled
    Level.TRACE -> isTraceEnabled
}

fun Logger.log(level: Level, msg: String, args: Array<*>) = when (level) {
    Level.ERROR -> error(msg, *args)
    Level.WARN -> warn(msg, *args)
    Level.INFO -> info(msg, *args)
    Level.DEBUG -> debug(msg, *args)
    Level.TRACE -> trace(msg, *args)
}

@JvmName("friendlyLog")
fun Logger.log(level: Level, msg: String, vararg args: Any?) = when (level) {
    Level.ERROR -> error(msg, *args)
    Level.WARN -> warn(msg, *args)
    Level.INFO -> info(msg, *args)
    Level.DEBUG -> debug(msg, *args)
    Level.TRACE -> trace(msg, *args)
}

fun String.toLowerCase(vararg indexes: Int): String {
    val charArray = toCharArray()
    indexes.forEach { index ->
        charArray[index] = charArray[index].lowercaseChar()
    }
    return String(charArray)
}

fun String.toUpperCase(vararg indexes: Int): String {
    val charArray = toCharArray()
    indexes.forEach { index ->
        charArray[index] = charArray[index].uppercaseChar()
    }
    return String(charArray)
}

fun String.toLowerCase(indexes: IntRange): String {
    val charArray = toCharArray()
    indexes.forEach { index ->
        charArray[index] = charArray[index].lowercaseChar()
    }
    return String(charArray)
}

fun String.toUpperCase(indexes: IntRange): String {
    val charArray = toCharArray()
    indexes.forEach { index ->
        charArray[index] = charArray[index].uppercaseChar()
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
            ((Utils.UPPER_HEX_ARRAY.indexOf(source[i * 2]) shl 4) + Utils.UPPER_HEX_ARRAY.indexOf(
                source[i * 2 + 1]
            )).toByte()
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
    val instance = MessageDigest.getInstance(type)
    instance.digest(this)
} catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
    null
}

fun Logger.logCaller() = logCaller(Level.DEBUG)

fun Logger.logCaller(level: Level) {
    if (isEnabled(level)) {
        val e = Throwable()
        val stackTraceElement = e.stackTrace[1]
        log(
            level,
            "calling {}.{}({}:{})",
            stackTraceElement.className,
            stackTraceElement.methodName,
            stackTraceElement.fileName,
            stackTraceElement.lineNumber
        )
    }
}

fun <A : Annotation, V : Any> A.changeAnnotationValue(field: KProperty1<A, V>, value: V): Boolean {
    return try {
        val h = Proxy.getInvocationHandler(this)
        val memberValuesField = h.javaClass.getDeclaredField("memberValues")
        memberValuesField.isAccessible = true
        @OptIn(UncheckedCast::class)
        val memberValues = memberValuesField[h].cast<MutableMap<String, Any>>()
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
            clazz = clazz.superclass ?: break
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
            clazz = clazz.superclass ?: return@sequence
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
        @OptIn(UncheckedCast::class)
        Utils.ownerField.get(this)?.cast<Class<*>>()
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

operator fun <E> List<E>.get(startIndex: Int = 0, endIndex: Int = size, step: Int = 1): List<E> {
    if (step <= 0) throw IllegalArgumentException("step($step) is negative or zero")
    val fromIndex = when {
        startIndex < 0 -> size + startIndex
        startIndex >= size -> size
        else -> startIndex
    }
    val toIndex = when {
        endIndex < 0 -> size + endIndex + 1
        endIndex >= size -> size
        else -> endIndex
    }
    var targetList = if (fromIndex > toIndex) ReversedList(subList(toIndex, fromIndex)) else subList(fromIndex, toIndex)
    if (step != 1) targetList = targetList step step
    return targetList
}

operator fun <E> List<E>.get(intProgression: IntProgression): List<E> {
    val first = intProgression.first
    val last = intProgression.last
    val step = intProgression.step
    return when {
        step == 0 -> get(first, last + if (last < 0) 0 else 1, 1)
        step < 0 -> get(first + if (last > 0 && first >= 0) 1 else 0, last, -step)
        else -> get(first, last + if (last < 0) 0 else 1, step)
    }
}

infix fun <E> List<E>.step(step: Int): List<E> = StepList(this, step)

inline fun <C : R, R : CharSequence> C.ifNotEmpty(defaultValue: () -> R): R =
    if (isNotEmpty()) defaultValue() else this

inline fun <C : R, R : CharSequence> C.ifNotBlank(defaultValue: () -> R): R =
    if (isNotBlank()) defaultValue() else this

@JvmName("ifNotEmptyNullable")
inline fun <C : R, R : CharSequence> C.ifNotEmpty(defaultValue: () -> R?): R? =
    if (isNotEmpty()) defaultValue() else this

@JvmName("ifNotBlankNullable")
inline fun <C : R, R : CharSequence> C.ifNotBlank(defaultValue: () -> R?): R? =
    if (isNotBlank()) defaultValue() else this

/**
 * 使用 condition 做条件判断，如果返回 true 则使用 then 生成结果，否则返回自身
 */
inline fun <R, T : R> T.ifThen(condition: T.() -> Boolean, then: T.() -> R): R = if (condition()) then() else this

inline infix fun Boolean.ifThen(then: () -> Unit): Boolean {
    if (this) then()
    return this
}

inline infix fun Boolean.ifNotThen(then: () -> Unit): Boolean {
    if (this.not()) then()
    return this
}

inline fun <T> Any.wait(action: () -> T) = synchronized(this) {
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    (this as Object).wait()
    action()
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

@UncheckedCast
inline val <K : Any, V> Map<K?, V>.notNullKey
    get() = cast<Map<K, V>>()

@UncheckedCast
inline val <K, V : Any> Map<K, V?>.notNullValue
    get() = cast<Map<K, V>>()

@UncheckedCast
inline val <K : Any, V : Any> Map<K?, V?>.notNullEntry
    get() = cast<Map<K, V>>()

@OptIn(UncheckedCast::class)
inline val <K : Any, V> Map<K?, V>.filterNullKey
    get() = filter { it.key != null }.notNullKey

@OptIn(UncheckedCast::class)
inline val <K, V : Any> Map<K, V?>.filterNullValue
    get() = filter { it.value != null }.notNullValue

val <T : Any> KClass<T>.allMemberPropertiesSequence: Sequence<KProperty1<T, *>>
    get() = sequence {
        yieldAll(memberProperties)
        var superClass = superclasses.firstOrNull {
            !it.java.isInterface
        }
        while (superClass != null) {
            yieldAll(superClass.memberProperties.uncheckedCast<Collection<KProperty1<T, *>>>())
            superClass = superClass.superclasses.firstOrNull {
                !it.java.isInterface
            }
        }
    }

val <T : Any> KClass<T>.allMemberProperties: List<KProperty1<T, *>>
    get() = allMemberPropertiesSequence.toList()

fun String.toStartWith(prefix: String) = if (startsWith(prefix)) this else prefix + this
fun String.toStartWith(prefix: Char) = if (startsWith(prefix)) this else prefix + this

fun mongoLegal(value: Any?) = when {
    value == null -> true
    value.javaClass.getAnnotation(POJO::class.java) != null -> true
    //value is BindingResult -> false
    value is Enum<*> -> true
    value is Number -> true
    value is Boolean -> true
    value is Char -> true
    value is String -> true
    //value is Serializable -> true
    value.javaClass.kotlin.isData -> true
    value.javaClass.name.endsWith("DTO") -> true
    value.javaClass.name.endsWith("VO") -> true
    else -> false
}

fun getCallerClass(thisClassName: List<String>): Class<*>? {
    var clazz: Class<*>?
    var callStackDepth = 1
    try {
        do {
            clazz = getCallerClass(callStackDepth++)
            if (clazz?.name !in thisClassName) {
                break
            }
        } while (clazz != null)
    } catch (e: Throwable) {
        val stackTrace = Throwable().stackTrace
        callStackDepth = 1
        do {
            clazz = Class.forName(stackTrace[callStackDepth++].className)
            if (clazz?.name !in thisClassName) {
                break
            }
        } while (callStackDepth < stackTrace.size)
    }
    return clazz
}

fun getCallerClassName(thisClassName: List<String>): String? {
    return getCallerClass(thisClassName + "com.ddbes.kotlin.util.UtilsKt")?.name
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

fun String.ifEndWith(suffix: String, ignoreCase: Boolean = false, defaultValue: String.() -> String) =
    if (endsWith(suffix, ignoreCase)) {
        this.defaultValue()
    } else {
        this
    }

inline operator fun <K, V> MutableMap<K, V>.get(key: K, ifNull: (K) -> V): V {
    var v = get(key)
    return if (v == null) {
        v = ifNull(key)
        set(key, v)
        v
    } else {
        v
    }
}

fun StringBuilder.removeLastChars(count: Int) {
    setLength(length - count)
}

inline fun <T> tryOrNull(getter: () -> T) = try {
    getter()
} catch (e: Exception) {
    null
}

fun String.trimBase64(): String {
    var end = length
    while (this[end - 1] == '=') {
        end--
    }
    return substring(0, end)
}

fun String.resumeBase64Trim(): String {
    val i = length % 4
    return if (i != 0) {
        this + String(CharArray(4 - i) { '=' })
    } else {
        this
    }
}

fun StringBuilder(vararg strings: String): StringBuilder {
    val builder = kotlin.text.StringBuilder(strings.sumOf { it.length })
    builder.append(value = strings)
    return builder
}

inline fun <T> Iterable<T>.forEachPart(size: UInt, action: (List<T>) -> Unit) {
    val list = ArrayList<T>(size.toInt() + 10)
    forEach {
        list.add(it)
        if (list.size >= size.toInt()) {
            action(list)
            list.clear()
        }
    }
    if (list.isNotEmpty()) {
        action(list)
    }
}

inline fun <T> Collection<T>.forEachPart(size: UInt, action: (List<T>) -> Unit) {
    if (isEmpty()) return
    (this as Iterable<T>).forEachPart(size, action)
}

inline fun <T> Queue<T>.forEachRemainingPart(size: UInt, action: (List<T>) -> Unit) {
    if (isEmpty()) return
    val list = ArrayList<T>(size.toInt() + 10)
    var element = poll()
    while (element != null) {
        list.add(element)
        element = poll()

        if (list.size >= size.toInt()) {
            action(list)
            list.clear()
        }
    }
    if (list.isNotEmpty()) {
        action(list)
    }
}

fun SimpleDateFormat.now() = format(Date())!!

val currentTimeMillis get() = System.currentTimeMillis()

inline operator fun <reified T> Array<out T>.plus(other: Array<out T>): Array<T> {
    val array = arrayOfNulls<T>(size + other.size)
    System.arraycopy(this, 0, array, 0, size)
    System.arraycopy(other, 0, array, size, other.size)
    return array.uncheckedCast()
}
