package cn.tursom.core.util

import sun.misc.Unsafe

object Unsafe {
  val unsafe: Unsafe = Unsafe::class.java.getStaticField("theUnsafe") as Unsafe

  operator fun <T> invoke(action: cn.tursom.core.util.Unsafe.() -> T): T = this.action()

  fun Any.getField(name: String): Any? {
    val clazz = this::class.java
    val field = try {
      clazz.getDeclaredField(name)
    } catch (e: Exception) {
      return null
    }
    field.isAccessible = true
    return field.get(this)
  }

  fun Any.setField(name: String, value: Any?) {
    val clazz = this::class.java
    val field = try {
      clazz.getDeclaredField(name)
    } catch (e: Exception) {
      return
    }
    field.isAccessible = true
    field.set(this, value)
  }

  fun Class<*>.getStaticField(name: String): Any? {
    val field = try {
      getDeclaredField(name)
    } catch (e: Exception) {
      return null
    }
    field.isAccessible = true
    return field.get(null)
  }
}
