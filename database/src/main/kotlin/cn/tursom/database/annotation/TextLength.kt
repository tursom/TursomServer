package cn.tursom.database.annotation

/**
 * only for string
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class TextLength(val length: Int)