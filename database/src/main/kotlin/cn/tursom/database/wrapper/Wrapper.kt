package cn.tursom.database.wrapper

interface Wrapper<T> {
  val table: String
  val where: Pair<String, Collection<Any?>>

  data class Where(val sql: String, val params: Collection<Any?>? = null)
}


