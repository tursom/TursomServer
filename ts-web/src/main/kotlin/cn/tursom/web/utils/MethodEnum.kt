package cn.tursom.web.utils

enum class MethodEnum(val method: String) {
  CONNECT("CONNECT"), DELETE("DELETE"), GET("GET"), HEAD("HEAD"),
  OPTIONS("OPTIONS"), PATCH("PATCH"), POST("POST"), PUT("PUT"),
  TRACE("TRACE"), NONE("")
}