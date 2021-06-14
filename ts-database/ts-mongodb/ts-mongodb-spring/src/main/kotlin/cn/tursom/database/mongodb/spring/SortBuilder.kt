package cn.tursom.database.mongodb.spring

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.aggregation.SortOperation
import kotlin.reflect.KProperty

@Suppress("MemberVisibilityCanBePrivate", "unused")
class SortBuilder : MongoName, BsonConverter {
  companion object {
    operator fun invoke(action: SortBuilder.() -> Unit): Sort {
      val builder = SortBuilder()
      builder.action()
      return builder.sort!!
    }
  }

  private var sort: Sort? = null
  val sortOperation get() = SortOperation(sort!!)

  operator fun invoke(action: SortBuilder.() -> Unit) = this.action()

  infix fun String.order(direction: Sort.Direction): Sort = Sort.by(Sort.Order(direction, this)).apply {
    updateSort(this) { this.and(it) }
  }

  infix fun KProperty<*>.order(direction: Sort.Direction) = mongoName order direction

  fun by(property: String) = Sort.by(property).apply {
    updateSort(this) { this.and(it) }
  }

  fun by(property: KProperty<*>) = by(property.mongoName)

  private fun updateSort(sort: Sort, action: (Sort) -> Sort) {
    if (this.sort == null) {
      this.sort = sort
    } else {
      this.sort = action(this.sort!!)
    }
  }
}