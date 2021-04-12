package cn.tursom.core.clone

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class NoPropertyClone(
  vararg val classList: KClass<*>,
)