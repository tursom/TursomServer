package cn.tursom.database.mongodb.spring

import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.StringOperators
import kotlin.reflect.KProperty

class ConcatBuilder {
  companion object : MongoName, BsonConverter {
    inline infix operator fun invoke(operator: ConcatBuilder.() -> Unit): StringOperators.Concat {
      val setBuilder = ConcatBuilder()
      setBuilder.operator()
      return when (setBuilder.list.size) {
        0 -> StringOperators.Concat.stringValue("")
        1 -> getConcat(setBuilder.list[0])
        else -> {
          val iterator = setBuilder.list.iterator()
          var concat = getConcat(iterator.next())
          iterator.forEach {
            concat = when (it) {
              is KProperty<*> -> concat.concatValueOf(it.mongoName)
              is FieldReference -> concat.concatValueOf(it.field)
              is AggregationExpression -> concat.concatValueOf(it)
              else -> concat.concat(it.toString())
            }
          }
          concat
        }
      }
    }

    fun getConcat(value: Any?) = when (value) {
      is KProperty<*> -> StringOperators.Concat.valueOf(value.mongoName)
      is FieldReference -> StringOperators.Concat.valueOf(value.field)
      is AggregationExpression -> StringOperators.Concat.valueOf(value)
      else -> StringOperators.Concat.stringValue(value.toString())
    }
  }

  data class FieldReference(val field: String)

  val list = ArrayList<Any?>()

  operator fun Any?.unaryPlus() {
    list.add(this)
  }

  operator fun AggregationExpression.unaryPlus() {
    list.add(this)
  }

  fun field(field: String) {
    list.add(FieldReference(field))
  }
}