package cn.tursom.web.netty

import cn.tursom.web.HttpContent
import cn.tursom.web.utils.Cookie
import io.netty.handler.codec.DateFormatter
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import java.util.*
import kotlin.collections.HashMap


fun HttpContent.parseHttpDate(date: CharSequence, start: Int = 0, end: Int = date.length): Date = DateFormatter.parseHttpDate(date, start, end)
fun HttpContent.format(date: Date): String = DateFormatter.format(date)
fun HttpContent.append(date: Date, sb: StringBuilder): StringBuilder = DateFormatter.append(date, sb)
fun HttpContent.decodeCookie(cookie: String): Map<String, Cookie> {
    val cookieMap = HashMap<String, Cookie>()
    ServerCookieDecoder.STRICT.decode(cookie).forEach {
        cookieMap[it.name()] = Cookie(it.name(), it.value(), it.domain(), it.path(), it.maxAge())
    }
    return cookieMap
}