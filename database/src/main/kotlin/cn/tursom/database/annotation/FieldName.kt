package cn.tursom.database.annotation

@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class FieldName(val name: String)