package cn.tursom.database.annotation

/**
 * callback interface :
 * getter(): Any?
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class Getter(val getter: String)