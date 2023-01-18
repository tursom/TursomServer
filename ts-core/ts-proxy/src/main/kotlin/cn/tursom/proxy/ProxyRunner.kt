package cn.tursom.proxy

import cn.tursom.proxy.ProxyContainer.Companion.forEachProxy
import cn.tursom.proxy.ProxyContainer.Companion.forFirstProxy
import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import net.sf.cglib.proxy.MethodProxy
import org.apache.commons.lang3.StringUtils
import java.lang.reflect.Method
import java.util.*


object ProxyRunner {
  private val errMsgSearchList = arrayOf("%M", "%B", "%A")
  private val proxyMethodCacheKey = ProxyContainer.contextEnv.newKey<ProxyMethodCache>()
    .withDefault { ProxyMethodCache() }

  /**
   * will be call when proxy method invoke.
   * 在代理方法被调用时，该方法会被调用
   */
  @Throws(Throwable::class)
  fun onProxy(obj: Any, c: ProxyContainer, method: Method, args: Array<out Any?>, proxy: MethodProxy): ProxyResult<*> {
    val cache = c.context[proxyMethodCacheKey]
    cache(c.lastModify, obj, c, method, args, proxy)?.let {
      return it
    }

    var handler: ProxyMethodCacheFunction? = null
    for (annotation in method.annotations) when (annotation) {
      is ForEachProxy -> {
        handler = onForeachProxy(annotation)
        break
      }
      is ForFirstProxy -> {
        handler = onForFirstProxy(annotation)
        break
      }
    }
    if (handler == null) {
      //handler = ProxyContainerHandlerCache.callSuper
      handler = onForFirstProxy(ForFirstProxy.EMPTY)
    }
    return handler(obj, c, method, args, proxy)
  }

  fun onForFirstProxy(forFirstProxy: ForFirstProxy): ProxyMethodCacheFunction {
    val classes = when (forFirstProxy.value.size) {
      0 -> emptyList()
      1 -> listOf(forFirstProxy.value[0].java)
      else -> forFirstProxy.value.asSequence().map { it.java }.toSet()
    }
    return { o, c, m: Method, a, p ->
      onForFirstProxy(o, c, m, a, p, forFirstProxy, classes)
    }
  }

  private fun onForFirstProxy(
    obj: Any,
    container: ProxyContainer,
    method: Method,
    args: Array<out Any?>,
    proxy: MethodProxy,
    forFirstProxy: ForFirstProxy,
    classes: Collection<Class<*>>,
  ): ProxyResult<*> {
    val result = container.forFirstProxy { p ->
      if (classes.isEmpty() || classes.stream().anyMatch { c: Class<*> -> c.isInstance(p) }) {
        val result = p.onProxy(obj, container, method, args, proxy)
        if (forFirstProxy.cache && result.success) {
          container.context[proxyMethodCacheKey].update(container.lastModify, proxy, p::onProxy)
        }
        return@forFirstProxy result
      } else {
        return@forFirstProxy ProxyResult.failed
      }
    }
    if (result.success) {
      return result
    }

    // when request not handled
    if (forFirstProxy.must) {
      // generate error message
      var errMsg: String = forFirstProxy.errMsg
      if (errMsg.isBlank()) {
        errMsg = "no proxy handled on method %M"
      }
      val replacementList = arrayOfNulls<String>(errMsgSearchList.size)
      // todo use efficient contains
      if (errMsg.contains(errMsgSearchList[0])) {
        replacementList[0] = method.toString()
      }
      if (errMsg.contains(errMsgSearchList[1])) {
        replacementList[1] = obj.toString()
      }
      if (errMsg.contains(errMsgSearchList[2])) {
        replacementList[2] = Arrays.toString(args)
      }

      errMsg = StringUtils.replaceEach(errMsg, errMsgSearchList, replacementList)
      val exceptionConstructor = forFirstProxy.errClass.java.getConstructor(String::class.java)
      if (forFirstProxy.cache) {
        container.context[proxyMethodCacheKey].update(container.lastModify, proxy) { _, _, _, _, _ ->
          throw exceptionConstructor.newInstance(errMsg)
        }
      }
      throw exceptionConstructor.newInstance(errMsg)
    }
    if (forFirstProxy.cache) {
      container.context[proxyMethodCacheKey].update(container.lastModify, proxy)
    }
    return ProxyResult.failed
  }

  fun onForeachProxy(forEachProxy: ForEachProxy) = onForeachProxy(when (forEachProxy.value.size) {
    0 -> emptyList()
    1 -> listOf(forEachProxy.value[0].java)
    else -> forEachProxy.value.asSequence().map { it.java }.toSet()
  }, forEachProxy.cache)

  private val emptyProxyList = ArrayList<ProxyMethod>()
  private fun onForeachProxy(
    classes: Collection<Class<*>>,
    cache: Boolean,
  ): ProxyMethodCacheFunction = { o, c, m, a, proxy ->
    val proxyList = if (cache) ArrayList<ProxyMethod>() else emptyProxyList
    c.forEachProxy { p ->
      if (classes.isEmpty() || classes.any { c: Class<*> -> c.isInstance(p) }) {
        val result = p.onProxy(o, c, m, a, proxy)
        if (cache && result.success) {
          proxyList.add(p)
        }
      }
    }
    if (cache) {
      c.context[proxyMethodCacheKey].update(c.lastModify, proxy, onCachedForeachProxy((proxyList)))
    }
    ProxyResult.failed<Any?>()
  }

  private fun onCachedForeachProxy(proxyList: List<ProxyMethod>): ProxyMethodCacheFunction = { o, c, m, a, proxy ->
    proxyList.forEach { p ->
      p.onProxy(o, c, m, a, proxy)
    }
    ProxyResult.failed<Any?>()
  }
}
