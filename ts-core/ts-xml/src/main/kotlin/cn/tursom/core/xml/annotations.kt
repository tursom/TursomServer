package cn.tursom.core.xml

enum class ElementTarget {
	Attribute, ElementText, SubElement
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultTarget(val target: ElementTarget)

/**
 * Short, Int, Long
 * Float, Double
 * Boolean
 * String
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Attribute

/**
 * Short, Int, Long
 * Float, Double
 * Boolean
 * String
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ElementText

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubElement

/**
 * 指定转换函数的名称
 * fun constructor(text: String): FieldValue
 * or
 * fun constructor(element: Element): FieldValue
 * element 为根节点
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Constructor(val constructor: String)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldName(val name: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ElementName(val name: String)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CompressionXml

/**
 * fun callback(
 *   obj: FieldType,
 *   elementName: String,
 *   builder: StringBuilder,
 *   indentation: String,
 *   advanceIndentation: String
 * )
 *
 * or
 *
 * fun callback(
 *   obj: FieldType,
 *   elementName: String,
 *   builder: StringBuilder
 * )
 *
 * simplify:
 *
 * fun callback(
 *   obj: FieldType,
 *   elementName: String,
 *   indentation: String,
 *   advanceIndentation: String
 * ): Any
 *
 * or
 *
 * fun callback(
 *   obj: FieldType,
 *   elementName: String
 * ): Any
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ToXml(val callback: String)

/**
 * 数组所有的元素都同名，在同一个父节点下
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Vararg

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ignore
