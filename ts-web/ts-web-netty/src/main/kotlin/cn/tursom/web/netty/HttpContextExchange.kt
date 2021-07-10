@file:Suppress("unused")

package cn.tursom.web.netty

import io.netty.handler.codec.DateFormatter
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import java.util.*


fun parseHttpDate(date: CharSequence, start: Int = 0, end: Int = date.length): Date =
  DateFormatter.parseHttpDate(date, start, end)

fun format(date: Date): String = DateFormatter.format(date)
fun append(date: Date, sb: StringBuilder): StringBuilder = DateFormatter.append(date, sb)
fun decodeCookie(cookie: String): Map<String, String> {
  val cookieMap = HashMap<String, String>()
  ServerCookieDecoder.STRICT.decode(cookie).forEach {
    cookieMap[it.name()] = it.value()
  }
  return cookieMap
}