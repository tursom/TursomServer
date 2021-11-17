package cn.tursom.database.ktorm.annotations

import kotlin.reflect.KClass

/**
 * 很抱歉我暂时做不到兼容 mybatis 的 TypeHandler，只能用这种方式凑合一下了
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Getter(val getter: String, val getterType: KClass<*>)