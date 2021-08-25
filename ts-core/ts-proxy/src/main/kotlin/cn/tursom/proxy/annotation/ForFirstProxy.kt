package cn.tursom.proxy.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForFirstProxy(
  vararg val value: KClass<*> = [],
  val name: String = "",
  /**
   * proxy objs must handle this method
   * or will throw an exception
   */
  val must: Boolean = false,
  val errMsg: String = "",
  val errClass: KClass<out RuntimeException> = RuntimeException::class,
)