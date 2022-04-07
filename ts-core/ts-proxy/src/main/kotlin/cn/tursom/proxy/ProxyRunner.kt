package cn.tursom.proxy

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import net.sf.cglib.proxy.MethodProxy
import org.apache.commons.lang3.StringUtils
import java.lang.reflect.Method
import java.util.*

object ProxyRunner {
  private val errMsgSearchList = arrayOf("%M", "%B", "%A")

  /**
   * will be call when proxy method invoke.
   * 在代理方法被调用时，该方法会被调用
   */
  @Throws(Throwable::class)
  fun onProxy(obj: Any, c: ProxyContainer, method: Method, args: Array<out Any?>, proxy: MethodProxy): ProxyResult<*> {
    var handler = ProxyContainerHandlerCache.getHandler(method)
    if (handler != null) {
      return handler(obj, c, method, args, proxy)
    }
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
    ProxyContainerHandlerCache.setHandler(method, handler)
    return handler(obj, c, method, args, proxy)
  }

  fun onForFirstProxy(forFirstProxy: ForFirstProxy) =
    { o: Any, c: ProxyContainer, m: Method, a: Array<out Any?>, p: MethodProxy ->
      onForFirstProxy(o, c, m, a, p, forFirstProxy, when (forFirstProxy.value.size) {
        0 -> emptyList()
        1 -> listOf(forFirstProxy.value[0].java)
        else -> forFirstProxy.value.asSequence().map { it.java }.toSet()
      })
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
        return@forFirstProxy p.onProxy(obj, method, args, proxy)
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
      throw forFirstProxy.errClass.java.getConstructor(String::class.java).newInstance(errMsg)
    }
    return ProxyResult.failed
  }

  private fun onForeachProxy(forEachProxy: ForEachProxy) = onForeachProxy(when (forEachProxy.value.size) {
    0 -> emptyList()
    1 -> listOf(forEachProxy.value[0].java)
    else -> forEachProxy.value.asSequence().map { it.java }.toSet()
  })

  private fun onForeachProxy(
    classes: Collection<Class<*>>,
  ) = label@{ o: Any, c: ProxyContainer, m: Method, a: Array<out Any?>, proxy1: MethodProxy ->
    c.forEachProxy { p ->
      if (classes.isEmpty() || classes.any { c: Class<*> -> c.isInstance(p) }) {
        p.onProxy(o, m, a, proxy1)
      }
    }
    ProxyResult.failed<Any?>()
  }
}