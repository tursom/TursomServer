package cn.tursom.reflect

import cn.tursom.core.UncheckedCast
import cn.tursom.core.Unsafe
import cn.tursom.core.cast
import cn.tursom.core.uncheckedCast
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object InstantAllocator {
    enum class AllocateFunction { INSTANCE, UNSAFE, KOBJECT, NONE }

    private val allocateFunctionMap = ConcurrentHashMap<Class<*>, AllocateFunction>()

    @Throws(NoSuchMethodException::class)
    operator fun <T> invoke(clazz: Class<out T>, unsafe: Boolean = true): T = get(clazz, unsafe)

    @Throws(NoSuchMethodException::class)
    inline operator fun <reified T : Any> invoke(unsafe: Boolean = true): T = get(T::class.java, unsafe)

    @Throws(NoSuchMethodException::class)
    operator fun <T : Any> get(clazz: KClass<out T>, unsafe: Boolean = true): T = get(clazz.java, unsafe)

    @Throws(NoSuchMethodException::class)
    operator fun <T> get(clazz: Class<out T>, unsafe: Boolean = true): T {
        return when (allocateFunctionMap[clazz]) {
            null -> try {
                val newInstance = clazz.newInstance()
                allocateFunctionMap[clazz] = AllocateFunction.INSTANCE
                newInstance
            } catch (e: Exception) {
                val kClass = clazz.kotlin
                val objectInstance = kClass.objectInstance
                if (objectInstance != null) {
                    allocateFunctionMap[clazz] = AllocateFunction.KOBJECT
                    objectInstance
                } else if (unsafe) try {
                    allocateFunctionMap[clazz] = AllocateFunction.UNSAFE
                    @OptIn(UncheckedCast::class)
                    Unsafe.unsafe.allocateInstance(clazz).cast<T>()
                } catch (e: Exception) {
                    allocateFunctionMap[clazz] = AllocateFunction.NONE
                    throw NoSuchMethodException("${clazz.name}:<init>()")
                } else {
                    throw NoSuchMethodException("${clazz.name}:<init>()")
                }
            }
            AllocateFunction.INSTANCE -> clazz.newInstance()
            AllocateFunction.UNSAFE -> if (unsafe) {
                Unsafe.unsafe.allocateInstance(clazz).uncheckedCast<T>()
            } else {
                throw NoSuchMethodException("${clazz.name}:<init>()")
            }
            AllocateFunction.KOBJECT -> clazz.kotlin.objectInstance!!
            AllocateFunction.NONE -> throw NoSuchMethodException("${clazz.name}:<init>()")
        }
    }
}
