package cn.tursom.database.annotation

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class FieldType(val name: String)