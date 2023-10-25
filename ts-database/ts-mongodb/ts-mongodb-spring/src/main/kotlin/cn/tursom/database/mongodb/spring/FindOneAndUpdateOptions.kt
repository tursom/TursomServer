package cn.tursom.database.mongodb.spring

import cn.tursom.core.util.uncheckedCast
import com.mongodb.client.model.FindOneAndUpdateOptions
import org.bson.conversions.Bson

inline fun FindOneAndUpdateOptions.arrayFilter(builder: QueryBuilder.() -> Unit) {
  if (arrayFilters == null) {
    arrayFilters(ArrayList())
  }
  arrayFilters.uncheckedCast<MutableList<Bson>>()
    .add(QueryBuilder queryObject builder)
}
