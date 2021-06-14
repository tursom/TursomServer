package cn.tursom.core.clone

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Relations(
  vararg val relations: Relation,
)