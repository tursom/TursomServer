package cn.tursom.core.clone

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Key(
  val key: String = "",
  val clazz: KClass<*> = Any::class,
  val handler: String = "",
)
