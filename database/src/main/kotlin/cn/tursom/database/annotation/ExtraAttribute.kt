package cn.tursom.database.annotation

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class ExtraAttribute(val attributes: String)