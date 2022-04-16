package cn.tursom.web.router

import java.lang.reflect.Method

data class MappedMethod(
  val method: Method,
  val aspect: List<WebAspect>,
)