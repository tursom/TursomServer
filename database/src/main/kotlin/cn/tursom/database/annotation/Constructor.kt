package cn.tursom.database.annotation


/**
 * callback interface:
 *
 * constructor(value: String): FieldType
 *
 * or :
 *
 * for AsyncSqlAdapter:
 * |- constructor(value: JsonArray, index: Int): FieldValue?
 *
 * for SqlAdapter:
 * |- constructor(resultSet: ResultSet): FieldValue?
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class Constructor(val constructor: String)