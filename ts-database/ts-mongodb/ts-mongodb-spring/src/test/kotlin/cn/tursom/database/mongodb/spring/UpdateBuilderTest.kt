package cn.tursom.database.mongodb.spring

class UpdateBuilderTest {
  data class Test(val a: String, val b: Int)

  @org.junit.Test
  fun testPush() {
    println(UpdateBuilder {
      Test::a push Test("a", 1)
      Test::b set 1
    })
  }
}