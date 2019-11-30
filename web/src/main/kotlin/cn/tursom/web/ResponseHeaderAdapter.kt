package cn.tursom.web

import cn.tursom.web.utils.CacheControl
import cn.tursom.web.utils.ContextTypeMap
import cn.tursom.web.utils.Cookie
import cn.tursom.web.utils.SameSite

interface ResponseHeaderAdapter {
  fun setResponseHeader(name: String, value: Any)
  fun addResponseHeader(name: String, value: Any)

  fun setCacheTag(tag: Any) = setResponseHeader("Etag", tag)

  fun noCache() = cacheControl(CacheControl.NoCache)
  fun noStore() = cacheControl(CacheControl.NoStore)
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

  fun addCookie(cookie: Cookie) = addCookie(cookie.name, cookie.value, cookie.maxAge, cookie.domain, cookie.path, cookie.sameSite)
  fun addCookie(
    name: String,
    value: Any,
    maxAge: Long = 0,
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

  fun acceptRanges(unit: String) = setResponseHeader("Accept-Ranges", unit)
  fun acceptBytesRanges() = acceptRanges("bytes")
  fun notAcceptRanges() = acceptRanges("none")

  fun range(start: Int, end: Int) = setResponseHeader("Content-Range", "$start-$end/*")
  fun range(start: Int, end: Int, resourceSize: Int) = setResponseHeader("Content-Range", "$start-$end/$resourceSize")
  fun range(resourceSize: Int) = setResponseHeader("Content-Range", "*/$resourceSize")

  fun setContextType(type: Any) = setResponseHeader("Content-Type", type)

  fun responseHtml() = setResponseHeader("content-type", "text/html; charset=UTF-8")
  fun responseText() = setResponseHeader("content-type", "text/plain; charset=UTF-8")
  fun responseJson() = setResponseHeader("content-type", "application/json; charset=UTF-8")

  fun autoContextType(type: String) = setContextType(ContextTypeMap[type] ?: "application/octet-stream")
}