package cn.tursom.database.ktorm.annotations

@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
annotation class KtormTableField(
  val name: String = "",
  val exist: Boolean = true,
)