package cn.tursom.mongodb.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class Ignore(val name: String)