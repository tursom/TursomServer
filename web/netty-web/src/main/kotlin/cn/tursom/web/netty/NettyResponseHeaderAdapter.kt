package cn.tursom.web.netty

import cn.tursom.log.lazyPrettyMap
import cn.tursom.log.traceEnabled
import cn.tursom.web.ResponseHeaderAdapter
import io.netty.handler.codec.http.HttpHeaders
import org.slf4j.LoggerFactory
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@Suppress("MemberVisibilityCanBePrivate")
open class NettyResponseHeaderAdapter : ResponseHeaderAdapter {
  val responseMap = HashMap<String, Any>()
  val responseListMap = HashMap<String, ArrayList<Any>>()

  override fun setResponseHeader(name: String, value: Any) {
    if (log.traceEnabled) {
      log?.trace("setResponseHeader {}: {}", name, value)
    }
    responseMap[name] = value
    responseListMap.remove(name)
  }

  override fun addResponseHeader(name: String, value: Any) {
    if (log.traceEnabled) {
      log?.trace("addResponseHeader {}: {}", name, value)
    }
    val list = responseListMap[name] ?: run {
      val newList = ArrayList<Any>()
      responseListMap[name] = newList
      newList
    }
    responseMap[name]?.let {
      responseMap.remove(name)
      list.add(it)
    }
    list.add(value)
  }

  protected fun HttpHeaders.addResponseListMap() {
    responseListMap.forEach { (t, u) ->
      u.forEach {
        add(t, it)
      }
    }
  }

  protected fun HttpHeaders.addHeaders(defaultHeaders: Map<out CharSequence, Any>) {
    if (log.traceEnabled) {
      log?.trace("addHeader\nheaders {}\ndefault {}", lazyPrettyMap(this), lazyPrettyMap(defaultHeaders))
    }
    addResponseListMap()

    responseMap.forEach { (t, u) ->
      set(t, u)
    }

    defaultHeaders.forEach { (t, u) ->
      if (!contains(t)) {
        set(t, u)
      }
    }
  }

  protected fun HttpHeaders.addHeaders(vararg defaultHeaders: Pair<CharSequence, Any>) {
    if (log.traceEnabled) {
      log?.trace("addHeader\nheaders {}\ndefault {}", lazyPrettyMap(this), defaultHeaders.asList())
    }
    addResponseListMap()

    responseMap.forEach { (t, u) ->
      set(t, u)
    }

    defaultHeaders.forEach { (t, u) ->
      if (!contains(t)) {
        set(t, u)
      }
    }
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyResponseHeaderAdapter::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}