package cn.tursom.proxy

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import net.sf.cglib.proxy.MethodProxy
import org.apache.commons.lang3.StringUtils
import java.lang.reflect.Method
import java.util.*

interface ProxyContainer<T : ProxyMethod> {
  @get:Throws(Throwable::class)
  val proxy: Proxy<T>

  /**
   * will be call when proxy method invoke.
   * 在代理方法被调用时，该方法会被调用
   */
  @Throws(Throwable::class)
  fun onProxy(method: Method, args: Array<out Any?>, proxy: MethodProxy): Proxy.Result<*>? {
    var handler = ProxyContainerHandlerCache.getHandler(method)
    if (handler != null) {
      return handler(this, method, args, proxy)
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
      handler = ProxyContainerHandlerCache.callSuper
    }
    ProxyContainerHandlerCache.setHandler(method, handler)
    return handler(this, method, args, proxy)
  }

  companion object {
    private val errMsgSearchList = arrayOf("%M", "%B", "%A")

    fun onForFirstProxy(forFirstProxy: ForFirstProxy) = { o: Any, m: Method, a: Array<out Any?>, p: MethodProxy ->
      onForFirstProxy(o, m, a, p, forFirstProxy, when (forFirstProxy.value.size) {
        0 -> emptyList()
        1 -> listOf(forFirstProxy.value[0].java)
        else -> forFirstProxy.value.asSequence().map { it.java }.toSet()
      })
    }

    private fun onForFirstProxy(
      obj: Any,
      method: Method,
      args: Array<out Any?>,
      proxy: MethodProxy?,
      forFirstProxy: ForFirstProxy,
      classes: Collection<Class<*>>,
    ): Proxy.Result<*> {
      if (obj !is ProxyContainer<*>) return Proxy.failed<Any>()
      val result = obj.proxy.forFirstProxy { p ->
        if (classes.isEmpty() || classes.stream().anyMatch { c: Class<*> -> c.isInstance(p) }) {
          return@forFirstProxy p.onProxy(obj, method, args, proxy)
        } else {
          return@forFirstProxy Proxy.failed()
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
      return Proxy.failed
    }

    private fun onForeachProxy(forEachProxy: ForEachProxy) = onForeachProxy(when (forEachProxy.value.size) {
      0 -> emptyList()
      1 -> listOf(forEachProxy.value[0].java)
      else -> forEachProxy.value.asSequence().map { it.java }.toSet()
    })

    private fun onForeachProxy(
      classes: Collection<Class<*>>,
    ) = label@{ o: Any, m: Method, a: Array<out Any?>, proxy1: MethodProxy ->
      if (o !is ProxyContainer<*>) return@label Proxy.failed
      o.proxy.forEachProxy { p ->
        if (classes.isEmpty() || classes.any { c: Class<*> -> c.isInstance(p) }) {
          p.onProxy(o, m, a, proxy1)
        }
      }
      Proxy.failed<Any?>()
    }
  }
}