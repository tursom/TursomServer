package cn.tursom.database.annotation

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class Default(val default: String)