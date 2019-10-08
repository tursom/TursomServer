package cn.tursom.database.annotation

@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class ForeignKey(val target: String = "")