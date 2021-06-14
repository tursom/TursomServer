package cn.tursom.database.mongodb.spring

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

object CriteriaQuery : MongoName, BsonConverter {
  inline infix operator fun invoke(operator: CriteriaBuilder.() -> Criteria): Query = Query(CriteriaBuilder.operator())
}