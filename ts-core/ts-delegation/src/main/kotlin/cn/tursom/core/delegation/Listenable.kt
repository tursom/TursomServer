package cn.tursom.core.delegation

/**
 * 标识一个属性可以被指定的 FieldChangeListener 监听
 * 属性的实现者应该实现相应的逻辑
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Listenable