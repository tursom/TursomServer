package cn.tursom.database.mongodb.spring

import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext
import org.springframework.data.mongodb.core.aggregation.ExposedFields
import org.springframework.data.mongodb.core.aggregation.Field

object DirectAggregationOperationContext : AggregationOperationContext {
  override fun getMappedObject(document: Document, type: Class<*>?): Document = document

  private class DirectAggregationOperationReference(name: String) : ExposedFields.FieldReference {
    private val name = "$$name"
    override fun getRaw(): String? = null
    override fun getReferenceValue(): Any? = null
    override fun toString(): String = name
  }

  override fun getReference(field: Field): ExposedFields.FieldReference =
    DirectAggregationOperationReference(field.name)

  override fun getReference(name: String): ExposedFields.FieldReference =
    DirectAggregationOperationReference(name)
}
