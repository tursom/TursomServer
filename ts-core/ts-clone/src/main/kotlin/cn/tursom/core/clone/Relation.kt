package cn.tursom.core.clone

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Relation(
  val clazz: KClass<*>,
  val property: String = "",
  val skip: Boolean = false,
  val handler: String = "",
  val handleClass: KClass<*> = Any::class,
)


