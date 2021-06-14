package cn.tursom.database.mongodb.spring

import cn.tursom.core.uncheckedCast
import com.mongodb.client.model.Collation
import com.mongodb.client.model.UpdateOptions
import org.bson.conversions.Bson

@Suppress("MemberVisibilityCanBePrivate", "unused")
class UpdateOption {
  companion object {
    operator fun invoke(builder: UpdateOption.() -> Unit): UpdateOptions = UpdateOption().apply(builder).updateOptions
  }

  val updateOptions = UpdateOptions()
  var arrayFilters: MutableList<Bson>?
    @JvmName("arrayFiltersGetter")
    get() = updateOptions.arrayFilters.uncheckedCast()
    set(value) {
      updateOptions.arrayFilters(value)
    }

  fun getArrayFilters(): MutableList<Bson> {
    var list = arrayFilters
    if (list == null) {
      list = ArrayList()
      arrayFilters = list
    }
    return list
  }

  fun arrayFilter(builder: QueryBuilder.() -> Unit) = getArrayFilters().add(QueryBuilder queryObject builder)

  var upsert: Boolean
    get() = updateOptions.isUpsert
    set(value) {
      updateOptions.upsert(value)
    }
  var bypassDocumentValidation: Boolean?
    get() = updateOptions.bypassDocumentValidation
    set(value) {
      updateOptions.bypassDocumentValidation(value)
    }
  var collation: Collation?
    get() = updateOptions.collation
    set(value) {
      updateOptions.collation(value)
    }
}