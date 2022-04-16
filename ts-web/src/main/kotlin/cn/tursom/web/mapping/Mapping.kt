package cn.tursom.web.mapping

import cn.tursom.web.utils.MethodEnum

annotation class Mapping(
  vararg val route: String,
  val method: String = "",
  val methodEnum: MethodEnum = MethodEnum.NONE,
)