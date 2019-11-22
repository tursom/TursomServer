package cn.tursom.web

import cn.tursom.web.utils.CacheControl
import cn.tursom.web.utils.SameSite

interface ResponseHeaderAdapter {
  fun setResponseHeader(name: String, value: Any)
  fun addResponseHeader(name: String, value: Any)

  fun setCacheTag(tag: Any) = setResponseHeader("Etag", tag)

  fun cacheControl(
    cacheControl: CacheControl,
    maxAge: Int? = null,
    mustRevalidate: Boolean = false
  ) = setResponseHeader(
    "Cache-Control", "$cacheControl${
  if (maxAge != null && maxAge > 0) ", max-age=$maxAge" else ""}${
  if (mustRevalidate) ", must-revalidate" else ""
  }"
  )

  fun addCookie(
    name: String,
    value: Any,
    maxAge: Int = 0,
    domain: String? = null,
    path: String? = null,
    sameSite: SameSite? = null
  ) = addResponseHeader(
    "Set-Cookie",
    "$name=$value${
    if (maxAge > 0) "; Max-Age=$maxAge" else ""}${
    if (domain != null) "; Domain=$domain" else ""}${
    if (path != null) "; Path=$path" else ""}${
    if (sameSite != null) ": SameSite=$sameSite" else ""
    }"
  )

  fun setLanguage(language: String) {
    setResponseHeader("Content-Language", language)
  }
}