package cn.tursom.proxy.function

import cn.tursom.proxy.annotation.ForFirstProxy
import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCache
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.MethodProxy
import org.apache.commons.lang3.StringUtils
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.*

class OnForFirstProxyImpl(
  private val ffpAnnotation: ForFirstProxy,
) : ProxyMethodCacheFunction {
  companion object {
    private val errMsgSearchList = arrayOf("%M", "%B", "%A")
  }

  private val classes: Collection<Class<*>> = when (ffpAnnotation.value.size) {
    0 -> emptyList()
    1 -> listOf(ffpAnnotation.value[0].java)
    else -> ffpAnnotation.value.asSequence().map { it.java }.toSet()
  }

  override fun invoke(
    obj: Any?,
    container: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    method!!
    proxy!!

    container.forEach { p ->
      if (classes.isNotEmpty() && classes.none { c: Class<*> -> c.isInstance(p) }) {
        return@forEach
      }

      val handler = ProxyMethod.getHandler(p, method) ?: return@forEach
      if (ffpAnnotation.cache) {
        container.ctx[ProxyMethodCache.ctxKey].update(proxy, handler)
      }
      return handler(obj, container, method, args, proxy)
    }

    // when request not handled
    if (ffpAnnotation.must) {
      // generate error message
      var errMsg: String = ffpAnnotation.errMsg
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
      val exceptionConstructor = ffpAnnotation.errClass.java.getConstructor(String::class.java)
      if (ffpAnnotation.cache) {
        container.ctx[ProxyMethodCache.ctxKey].update(
          proxy, ExceptionProxyMethodCacheFunctionImpl(exceptionConstructor, errMsg)
        )
      }
      throw exceptionConstructor.newInstance(errMsg)
    }
    if (ffpAnnotation.cache) {
      container.ctx[ProxyMethodCache.ctxKey].update(
        proxy,
        CallSuperProxyMethodCacheFunction,
      )
    }
    return proxy.invokeSuper(obj, args)
  }
}

private class ExceptionProxyMethodCacheFunctionImpl(
  private val exceptionConstructor: Constructor<out Throwable>,
  private val errMsg: String,
) : ProxyMethodCacheFunction {
  override fun invoke(
    obj: Any?,
    c: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ) = throw exceptionConstructor.newInstance(errMsg)
}
