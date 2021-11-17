package cn.tursom.database.mybatisplus

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnhanceField(
  val field: String,
)
