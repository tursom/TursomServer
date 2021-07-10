package cn.tursom.web.utils

class Cookie(
  var name: String,
  var value: String,
  var domain: String? = null,
  var path: String? = null,
  var maxAge: Long = 0,
  var sameSite: SameSite? = null
) {
  override fun toString(): String {
    return "$name=$value${
      if (maxAge > 0) "; Max-Age=$maxAge" else ""
    }${
      if (domain != null) "; Domain=$domain" else ""
    }${
      if (path != null) "; Path=$path" else ""
    }${
      if (sameSite != null) ": SameSite=$sameSite" else ""
    }"
  }
}