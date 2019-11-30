package cn.tursom.web.mapping

annotation class Mapping(
  vararg val route: String,
  val method: String = ""
)