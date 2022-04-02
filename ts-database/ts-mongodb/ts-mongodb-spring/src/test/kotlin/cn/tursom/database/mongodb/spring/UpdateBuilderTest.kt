package cn.tursom.database.mongodb.spring

import org.springframework.data.mongodb.core.MongoTemplate

class UpdateBuilderTest {
  data class Test(val a: String, val b: Int)

  private lateinit var mongoTemplate: MongoTemplate

  @org.junit.Test
  fun testPush() {
    mongoTemplate.updateMulti<Test>({
      or {
        Test::b eq 1
        Test::b eq 2
      }
    }) {
      Test::a push Test("a", 1)
      Test::b set 1
    }
    println(UpdateBuilder {
      Test::a push Test("a", 1)
      Test::b set 1
    })
  }
}