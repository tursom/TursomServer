package cn.tursom.core

import sun.misc.Unsafe

object Unsafe {
  val unsafe: Unsafe = Unsafe::class.java["theUnsafe"] as Unsafe

  operator fun <T> invoke(action: cn.tursom.core.Unsafe.() -> T): T = this.action()

  operator fun Any.get(name: String): Any? {
    val clazz = this::class.java
    val field = try {
      clazz.getDeclaredField(name)
    } catch (e: Exception) {
      return null
    }
    field.isAccessible = true
    return field.get(this)
  }

  operator fun Class<*>.get(name: String): Any? {
    val field = try {
      getDeclaredField(name)
    } catch (e: Exception) {
      return null
    }
    field.isAccessible = true
    return field.get(null)
  }
}