package com.ddbes.kotlin.clone

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class NoPropertyClone(
    vararg val classList: KClass<*>
)