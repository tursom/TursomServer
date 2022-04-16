package cn.tursom.database.mongodb.spring

import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import kotlin.reflect.KProperty

@Suppress("unused")
object AggregationBuilder : MongoName {
  inline infix operator fun invoke(
    operatorBuilder: AggregationBuilder.(MutableList<AggregationOperation>) -> MutableList<AggregationOperation>,
  ): Aggregation {
    val operator = this.operatorBuilder(ArrayList())
    return Aggregation.newAggregation(operator)
  }

  infix operator fun MutableList<AggregationOperation>.plus(
    operation: AggregationOperation,
  ): MutableList<AggregationOperation> {
    add(operation)
    return this
  }

  infix fun MutableList<AggregationOperation>.match(
    builder: QueryBuilder.() -> Unit,
  ): MutableList<AggregationOperation> {
    add(Aggregation.match(QueryBuilder criteria builder))
    return this
  }

  infix fun MutableList<AggregationOperation>.group(
    builder: GroupBuilder.() -> AggregationOperation,
  ): MutableList<AggregationOperation> {
    add(GroupBuilder.builder())
    return this
  }

  infix fun MutableList<AggregationOperation>.project(
    builder: ProjectBuilder.() -> AggregationOperation,
  ): MutableList<AggregationOperation> {
    add(ProjectBuilder.builder())
    return this
  }

  infix fun MutableList<AggregationOperation>.unwind(
    field: String,
  ): MutableList<AggregationOperation> {
    add(Aggregation.unwind(field))
    return this
  }

  infix fun MutableList<AggregationOperation>.unwind(
    field: KProperty<*>,
  ): MutableList<AggregationOperation> {
    add(Aggregation.unwind(field.mongoName))
    return this
  }

  @JvmName("unwindString")
  infix fun MutableList<AggregationOperation>.unwind(
    field: () -> String,
  ): MutableList<AggregationOperation> {
    add(Aggregation.unwind(field()))
    return this
  }

  infix fun MutableList<AggregationOperation>.unwind(
    field: () -> KProperty<*>,
  ): MutableList<AggregationOperation> {
    add(Aggregation.unwind(field().mongoName))
    return this
  }

  infix fun MutableList<AggregationOperation>.sort(
    builder: SortBuilder.() -> Unit,
  ): MutableList<AggregationOperation> {
    val sortBuilder = SortBuilder()
    sortBuilder.invoke(builder)
    add(sortBuilder.sortOperation)
    return this
  }

  infix fun MutableList<AggregationOperation>.skip(
    skip: Long,
  ): MutableList<AggregationOperation> {
    add(Aggregation.skip(skip))
    return this
  }

  infix fun MutableList<AggregationOperation>.limit(
    limit: Long,
  ): MutableList<AggregationOperation> {
    add(Aggregation.limit(limit))
    return this
  }
}