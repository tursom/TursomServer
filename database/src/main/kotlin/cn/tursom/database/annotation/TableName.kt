package cn.tursom.database.annotation

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class TableName(val name: String)