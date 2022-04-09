package cn.tursom.proxy.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForEachProxy(
  vararg val value: KClass<*> = [],
  val name: String = "",
  val cache: Boolean = true,
)