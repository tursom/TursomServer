package cn.tursom.web.router

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.regex.regex
import cn.tursom.json.JsonWorkerImpl
import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpContent
import cn.tursom.web.HttpHandler
import cn.tursom.web.MutableHttpContent
import cn.tursom.web.router.impl.SimpleRouter
import cn.tursom.web.mapping.*
import cn.tursom.web.result.Html
import cn.tursom.web.result.Json
import cn.tursom.web.result.Text
import cn.tursom.web.utils.Chunked
import org.slf4j.LoggerFactory
import java.io.File
import java.io.RandomAccessFile
import java.lang.reflect.Method
import kotlin.reflect.KCallable

/**
 * 自动添加路径映射的处理器
 * 会将 public 且参数仅为一个 T 的方法自动注册到路由器里
 * 例：public void Index(HttpContext context)
 *     fun Index(context: HttpContext)
 * 如果加了 @Mapping 注解，会依据注解提供的路由路径注册
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class RoutedHttpHandler(
  target: Any? = null,
  val routerMaker: () -> Router<Pair<Any?, (HttpContent) -> Unit>> = { SimpleRouter() }
) : HttpHandler<HttpContent, ExceptionContent> {
  protected val router: Router<Pair<Any?, (HttpContent) -> Unit>> = routerMaker()
  protected val routerMap: HashMap<String, Router<Pair<Any?, (HttpContent) -> Unit>>> = HashMap()

  init {
    @Suppress("LeakingThis")
    addRouter(target ?: this)
  }

  override fun handle(content: HttpContent) = if (content is MutableHttpContent) {
    handle(content, getHandler(content, content.method, content.uri))
  } else {
    handle(content, getHandler(content.method, content.uri).first?.second)
  }

  open fun handle(content: HttpContent, handler: ((HttpContent) -> Unit)?) {
    if (handler != null) {
      handler(content)
    } else {
      notFound(content)
    }
  }

  open fun notFound(content: HttpContent) {
    content.finish(404)
  }

  open fun addRouter(handler: Any) {
    @Suppress("LeakingThis")
    val clazz = handler.javaClass
    clazz.methods.forEach { method ->
      log?.debug("try mapping {}({})", method, method.parameterTypes)
      method.parameterTypes.let {
        if (!(it.size == 1 && HttpContent::class.java.isAssignableFrom(it[0])) && it.isNotEmpty()) {
          return@forEach
        }
      }
      insertMapping(handler, method)
    }
  }

  fun addRouter(route: String, handler: (HttpContent) -> Unit) = addRouter(route, null, handler)
  fun addRouter(route: String, obj: Any?, handler: (HttpContent) -> Unit) {
    router[safeRoute(route)] = obj to handler
  }

  fun addRouter(method: String, route: String, handler: (HttpContent) -> Unit) = addRouter(method, route, null, handler)
  fun addRouter(method: String, route: String, obj: Any?, handler: (HttpContent) -> Unit) {
    getRouter(method)[safeRoute(route)] = obj to handler
  }

  fun getHandler(content: MutableHttpContent, method: String, route: String): ((HttpContent) -> Unit)? {
    val safeRoute = safeRoute(route)
    val router = getHandler(method, safeRoute)
    if (router.first != null) {
      router.second.forEach { (k, v) ->
        content.addParam(k, v)
      }
    }
    return router.first?.second ?: this.router[safeRoute].first?.second
  }

  fun getHandler(method: String, route: String): Pair<Pair<Any?, (HttpContent) -> Unit>?, List<Pair<String, String>>> {
    val safeRoute = safeRoute(route)
    val router = getRouter(method)[safeRoute]
    return if (router.first != null) router else this.router[safeRoute]
  }

  protected fun insertMapping(obj: Any, method: Method) {
    val mapping = obj::class.java.getAnnotation(Mapping::class.java)?.route ?: arrayOf("")
    method.annotations.forEach { annotation ->
      log?.info("method route {} annotation {}", method, annotation)
      val (routes, router) = getRoutes(annotation) ?: return@forEach
      log?.info("method route {} mapped to {}", method, routes)
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

  fun addRouter(obj: Any, method: Method, route: String, router: Router<Pair<Any?, (HttpContent) -> Unit>>) {
    router[safeRoute(route)] = if (method.parameterTypes.isEmpty()) {
      obj to when {
        method.getAnnotation(Html::class.java) != null -> { content ->
          method(obj)?.let { result -> finishHtml(result, content) }
        }
        method.getAnnotation(Text::class.java) != null -> { content ->
          method(obj)?.let { result -> finishText(result, content) }
        }
        method.getAnnotation(Json::class.java) != null -> { content ->
          method(obj)?.let { result -> finishJson(result, content) }
        }
        else -> { content ->
          method(obj)?.let { result -> autoReturn(result, content) }
        }
      }
    } else obj to when (method.returnType) {
      Void::class.java -> { content -> method(obj, content) }
      Void.TYPE -> { content -> method(obj, content) }
      Unit::class.java -> { content -> method(obj, content) }
      else -> when {
        method.getAnnotation(Html::class.java) != null -> { content ->
          method(obj, content)?.let { result -> finishHtml(result, content) }
        }
        method.getAnnotation(Text::class.java) != null -> { content ->
          method(obj, content)?.let { result -> finishText(result, content) }
        }
        method.getAnnotation(Json::class.java) != null -> { content ->
          method(obj, content)?.let { result -> finishJson(result, content) }
        }
        else -> { content ->
          method(obj, content)?.let { result -> autoReturn(result, content) }
        }
      }
    }
  }

  fun deleteRouter(route: String, method: String) {
    getRouter(method).delRoute(safeRoute(route))
  }

  fun deleteRouter(handler: Any) {
    @Suppress("LeakingThis")
    val clazz = handler.javaClass
    clazz.methods.forEach { method ->
      log?.debug("try mapping {}", method)
      method.parameterTypes.let {
        if (!(it.size == 1 && HttpContent::class.java.isAssignableFrom(it[0])) && it.isNotEmpty()) {
          return@forEach
        }
      }
      deleteMapping(handler, method)
    }
  }

  protected fun deleteMapping(obj: Any, method: Method) {
    method.annotations.forEach { annotation ->
      val (routes, router) = getRoutes(annotation) ?: return@forEach
      routes.forEach { route ->
        log?.info("delete route {} mapped to {}", route, method)
        val handler = router[safeRoute(route)].first
        if (handler?.first == obj) {
          router.delRoute(safeRoute(route))
        }
      }
    }
  }

  protected fun getRoutes(annotation: Annotation) = when (annotation) {
    is Mapping -> {
      annotation.route to getRouter(annotation.method)
    }
    is GetMapping -> {
      annotation.route to getRouter("GET")
    }
    is PostMapping -> {
      annotation.route to getRouter("POST")
    }
    is PutMapping -> {
      annotation.route to getRouter("PUT")
    }
    is DeleteMapping -> {
      annotation.route to getRouter("DELETE")
    }
    is PatchMapping -> {
      annotation.route to getRouter("PATCH")
    }
    is TraceMapping -> {
      annotation.route to getRouter("TRACE")
    }
    is HeadMapping -> {
      annotation.route to getRouter("HEAD")
    }
    is OptionsMapping -> {
      annotation.route to getRouter("OPTIONS")
    }
    is ConnectMapping -> {
      annotation.route to getRouter("CONNECT")
    }
    else -> null
  }

  protected fun getRouter(method: String): Router<Pair<Any?, (HttpContent) -> Unit>> = when {
    method.isEmpty() -> router
    else -> {
      val upperCaseMethod = method.toUpperCase()
      var router = routerMap[upperCaseMethod]
      if (router == null) {
        router = routerMaker()
        routerMap[upperCaseMethod] = router
      }
      router
    }
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(RoutedHttpHandler::class.java)
    } catch (e: Throwable) {
      null
    }

    private val json = try {
      JsonWorkerImpl()
    } catch (e: Throwable) {
      null
    }

    fun <T> T.repeatUntil(state: (T) -> Boolean, block: (T) -> T): T {
      var result = this
      while (state(result)) {
        result = block(result)
      }
      return result
    }

    val slashRegex = regex { (-"/").onceMore }

    fun safeRoute(route: String) = (
      if (route.startsWith('/')) route else "/$route").let {
      if (it.endsWith('/')) it.dropLast(1) else it
    }.repeatUntil({ it.contains("//") }) { it.replace(slashRegex, "/") }

    fun autoReturn(result: Any, content: HttpContent) {
      log?.debug("{}: autoReturn: {}", content.clientIp, result)
      when (result) {
        is String -> content.finishText(result.toByteArray())
        is StringBuilder -> content.finishText(result.toString().toByteArray())
        is StringBuffer -> content.finishText(result.toString().toByteArray())
        is ByteArray -> content.finishText(result)
        is File -> {
          content.autoContextType(result.name)
          content.finishFile(result)
        }
        is RandomAccessFile -> {
          content.finishFile(result)
        }
        is Chunked -> content.finishChunked(result)
        else -> finishJson(result, content)
      }
    }

    fun finishHtml(result: Any, content: HttpContent) {
      log?.debug("{}: finishHtml: {}", content.clientIp, result)
      when (result) {
        is ByteBuffer -> content.finishHtml(result)
        is ByteArray -> content.finishHtml(result)
        is String -> content.finishHtml(result.toByteArray())
        else -> content.finishHtml(result.toString().toByteArray())
      }
    }

    fun finishText(result: Any, content: HttpContent) {
      log?.debug("{}: finishText: {}", content.clientIp, result)
      when (result) {
        is ByteBuffer -> content.finishText(result)
        is ByteArray -> content.finishText(result)
        is String -> content.finishText(result.toByteArray())
        else -> content.finishText(result.toString().toByteArray())
      }
    }

    fun finishJson(result: Any, content: HttpContent) {
      log?.debug("{}: finishJson: {}", content.clientIp, result)
      when (result) {
        is ByteBuffer -> content.finishJson(result)
        is ByteArray -> content.finishJson(result)
        is String -> content.finishJson("\"$result\"".toByteArray())
        is Byte, Short, Int, Long, Float, Double, Boolean -> content.finishJson(result.toString().toByteArray())
        else -> {
          val json = json?.toJson(result)
          log?.debug("{}: finishJson: generate json: {}", content.clientIp, json)
          if (json != null) {
            content.finishJson(json.toByteArray())
          } else {
            content.finishText(result.toString().toByteArray())
          }
        }
      }
    }
  }
}
