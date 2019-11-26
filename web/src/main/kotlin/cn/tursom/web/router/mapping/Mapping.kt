package cn.tursom.web.router.mapping

annotation class Mapping(
  vararg val route: String,
  val method: String = ""
)