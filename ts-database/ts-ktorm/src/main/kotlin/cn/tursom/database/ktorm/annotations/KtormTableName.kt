package cn.tursom.database.ktorm.annotations

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtormTableName(
  val name: String = "",
)