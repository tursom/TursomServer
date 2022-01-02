package cn.tursom.web.router

import cn.tursom.core.allMethodsSequence
import cn.tursom.web.HttpContent
import cn.tursom.web.MutableHttpContent
import cn.tursom.web.mapping.*
import cn.tursom.web.result.*
import cn.tursom.web.router.impl.SimpleRouter
import cn.tursom.web.utils.ContextTypeEnum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

@Suppress("ProtectedInFinal", "unused", "MemberVisibilityCanBePrivate")
open class AsyncRoutedHttpHandler(
  vararg target: Any,
  routerMaker: () -> Router<Pair<Any?, (HttpContent) -> Any?>> = { SimpleRouter() },
  val asyncRouterMaker: () -> Router<Pair<Any?, suspend (HttpContent) -> Unit>> = { SimpleRouter() }
) : RoutedHttpHandler(target = target, routerMaker = routerMaker) {
  protected val asyncRouter: Router<Pair<Any?, suspend (HttpContent) -> Unit>> = asyncRouterMaker()
  protected val asyncRouterMap: HashMap<String, Router<Pair<Any?, suspend (HttpContent) -> Unit>>> = HashMap()

  override fun handle(content: HttpContent) {
    if (content is MutableHttpContent) {
      val handler = getAsyncHandler(content, content.method, content.uri)
      if (handler != null) GlobalScope.launch {
        handle(content, handler)
      } else {
        handle(content, getHandler(content, content.method, content.uri))
      }
    } else {
      val handler = getAsyncHandler(content.method, content.uri).first?.second
      if (handler != null) GlobalScope.launch {
        handle(content, handler)
      } else {
        handle(content, getHandler(content.method, content.uri).first?.second)
      }
    }
  }

  open suspend fun handle(content: HttpContent, handler: (suspend (HttpContent) -> Unit)?) {
    if (handler != null) {
      try {
        handler(content)
      } catch (e: Throwable) {
        val bos = ByteArrayOutputStream()
        @Suppress("BlockingMethodInNonBlockingContext")
        bos.write("处理时发生异常：\n".toByteArray())
        e.printStackTrace(PrintStream(bos))
        content.setContextType(ContextTypeEnum.txt.value)
        content.write(bos.toByteArray())
        content.finish(502)
      }
    } else {
      notFound(content)
    }
  }

  fun getAsyncHandler(
    method: String,
    route: String
  ): Pair<Pair<Any?, suspend (HttpContent) -> Unit>?, List<Pair<String, String>>> {
    val safeRoute = safeRoute(route)
    val router = getAsyncRouter(method)[safeRoute]
    return if (router.first != null) router else this.asyncRouter[safeRoute]
  }

  fun getAsyncHandler(content: MutableHttpContent, method: String, route: String): (suspend (HttpContent) -> Unit)? {
    val safeRoute = safeRoute(route)
    val router = getAsyncHandler(method, safeRoute)
    if (router.first != null) {
      router.second.forEach { (k, v) ->
        content.addParam(k, v)
      }
    }
    return router.first?.second ?: this.asyncRouter[safeRoute].first?.second
  }

  override fun addRouter(handler: Any) {
    super.addRouter(handler)
    handler.javaClass.allMethodsSequence.forEach { method ->
      val function = try {
        method.kotlinFunction ?: return@forEach
      } catch (e: Exception) {
        return@forEach
      }
      method.isAccessible = true
      if (function.isSuspend) {
        function.parameters.let {
          if (it.size != 1 && !(it.size == 2 && HttpContent::class.java.isAssignableFrom(it[1].type.jvmErasure.java))) {
            return@forEach
          }
        }
        log?.trace("mapping {} {}", function, function.parameters)
        insertMapping(handler, function)
      }
    }
  }

  protected fun insertMapping(obj: Any, method: KCallable<*>) {
    val mapping = obj::class.java.getAnnotation(Mapping::class.java)?.route ?: arrayOf("")
    method.annotations.forEach { annotation ->
      val (routes, router) = getAsyncRoutes(annotation) ?: return@forEach
      @Suppress("DuplicatedCode")
      log?.info("mapping {} {} => {}", getRouteMethod(annotation), routes, method)
      routes.forEach { route ->
        if (mapping.isEmpty()) {
          addRouter(obj, method, route, router)
        } else mapping.forEach {
          val base = safeRoute(it)
          addRouter(obj, method, base + route, router)
        }
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  fun addRouter(
    obj: Any,
    method: KCallable<*>,
    route: String,
    router: Router<Pair<Any?, suspend (HttpContent) -> Unit>>
  ) {
    val doLog = method.doLog
    router[safeRoute(route)] = if (method.parameters.size == 1) {
      obj to when {
        method.findAnnotation<Html>() != null -> { content ->
          finishHtml((method as suspend Any.() -> Any?)(obj), content, doLog)
        }
        method.findAnnotation<Text>() != null -> { content ->
          finishText((method as suspend Any.() -> Any?)(obj), content, doLog)
        }
        method.findAnnotation<Json>() != null -> { content ->
          finishJson((method as suspend Any.() -> Any?)(obj), content, doLog)
        }
        else -> { content ->
          autoReturn(method, (method as suspend Any.() -> Any?)(obj), content, doLog)
        }
      }
    } else obj to when (method.returnType.jvmErasure.java) {
      Void::class.java -> { content -> (method as suspend Any.(HttpContent) -> Unit)(obj, content) }
      Void.TYPE -> { content -> (method as suspend Any.(HttpContent) -> Unit)(obj, content) }
      Unit::class.java -> { content -> (method as suspend Any.(HttpContent) -> Unit)(obj, content) }
      else -> when {
        method.findAnnotation<Html>() != null -> { content ->
          finishHtml((method as suspend Any.(HttpContent) -> Any?)(obj, content), content, doLog)
        }
        method.findAnnotation<Text>() != null -> { content ->
          finishText((method as suspend Any.(HttpContent) -> Any?)(obj, content), content, doLog)
        }
        method.findAnnotation<Json>() != null -> { content ->
          finishJson((method as suspend Any.(HttpContent) -> Any?)(obj, content), content, doLog)
        }
        else -> { content ->
          autoReturn(method, (method as suspend Any.(HttpContent) -> Any?)(obj, content), content, doLog)
        }
      }
    }
  }


  protected fun getAsyncRoutes(annotation: Annotation) = when (annotation) {
    is Mapping -> annotation.route to getAsyncRouter(annotation.method.let { if (it.isEmpty()) annotation.methodEnum.method else it })
    is GetMapping -> annotation.route to getAsyncRouter("GET")
    is PostMapping -> annotation.route to getAsyncRouter("POST")
    is PutMapping -> annotation.route to getAsyncRouter("PUT")
    is DeleteMapping -> annotation.route to getAsyncRouter("DELETE")
    is PatchMapping -> annotation.route to getAsyncRouter("PATCH")
    is TraceMapping -> annotation.route to getAsyncRouter("TRACE")
    is HeadMapping -> annotation.route to getAsyncRouter("HEAD")
    is OptionsMapping -> annotation.route to getAsyncRouter("OPTIONS")
    is ConnectMapping -> annotation.route to getAsyncRouter("CONNECT")
    else -> null
  }

  protected fun getAsyncRouter(method: String): Router<Pair<Any?, suspend (HttpContent) -> Unit>> = when {
    method.isEmpty() -> asyncRouter
    else -> {
      val upperCaseMethod = method.uppercase(Locale.getDefault())
      var router = asyncRouterMap[upperCaseMethod]
      if (router == null) {
        router = asyncRouterMaker()
        asyncRouterMap[upperCaseMethod] = router
      }
      router
    }
  }

  override fun deleteRouter(handler: Any) {
    super.deleteRouter(handler)
    handler::class.members.forEach { member ->
      if (member.isSuspend) {
        member.parameters.let {
          if (it.size != 1 && !(it.size == 2 && HttpContent::class.java.isAssignableFrom(it[1].type.jvmErasure.java))) {
            return@forEach
          }
        }
        val mapping = handler::class.java.getAnnotation(Mapping::class.java)?.route ?: arrayOf("")
        member.annotations.forEach route@{ annotation ->
          val (routes, router) =
            getAsyncRoutes(annotation) ?: return@route
          @Suppress("DuplicatedCode")
          routes.forEach { route ->
            if (mapping.isEmpty()) {
              deleteRouter(handler, route, router)
            } else mapping.forEach {
              val base = safeRoute(it)
              deleteRouter(handler, base + route, router)
            }
          }
        }
      }
    }
  }

  fun deleteRouter(handler: Any, route: String, router: Router<Pair<Any?, suspend (HttpContent) -> Unit>>) {
    val (pair, _) = router[route]
    val (target, _) = pair ?: return
    if (handler == target) {
      router.delRoute(route)
    }
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(AsyncRoutedHttpHandler::class.java)
    } catch (e: Throwable) {
      null
    }

    val KCallable<*>.doLog get() = findAnnotation<NoReturnLog>() == null

    fun autoReturn(method: KCallable<*>, result: Any?, content: HttpContent, doLog: Boolean? = null) {
      method.findAnnotation<ContextType>()?.let {
        content.setContextType(it.type.value)
        log?.debug("{}: autoReturn context type auto set to {}({})", content.remoteAddress, it.type.key, it.type.value)
      }
      autoReturn(result, content, doLog ?: method.doLog)
    }
  }
}