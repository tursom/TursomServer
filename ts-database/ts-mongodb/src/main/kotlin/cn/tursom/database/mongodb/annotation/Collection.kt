package cn.tursom.database.mongodb.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Collection(val name: String)

