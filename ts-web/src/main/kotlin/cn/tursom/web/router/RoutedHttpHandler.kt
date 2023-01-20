package cn.tursom.web.router

import cn.tursom.core.allMethodsSequence
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.json.JsonWorkerImpl
import cn.tursom.core.lambda
import cn.tursom.core.regex.regex
import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpContent
import cn.tursom.web.HttpHandler
import cn.tursom.web.MutableHttpContent
import cn.tursom.web.mapping.*
import cn.tursom.web.result.*
import cn.tursom.web.router.impl.SimpleRouter
import cn.tursom.web.utils.Chunked
import cn.tursom.web.utils.ContextTypeEnum
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.io.RandomAccessFile
import java.lang.reflect.Method
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 自动添加路径映射的处理器
 * 会将 public 且参数仅为一个 T 的方法自动注册到路由器里
 * 例：public void Index(HttpContext context)
 *     fun Index(context: HttpContext)
 * 如果加了 @Mapping 注解，会依据注解提供的路由路径注册
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class RoutedHttpHandler(
  vararg target: Any,
  val routerMaker: () -> Router<Pair<Any?, (HttpContent) -> Any?>> = { SimpleRouter() },
) : HttpHandler<HttpContent, ExceptionContent> {
  protected val router: Router<Pair<Any?, (HttpContent) -> Any?>> = routerMaker()
  protected val routerMap: HashMap<String, Router<Pair<Any?, (HttpContent) -> Any?>>> = HashMap()
  private val threadNumber = AtomicInteger(0)
  val workerThread = ThreadPoolExecutor(
    Runtime.getRuntime().availableProcessors() * 4,
    Runtime.getRuntime().availableProcessors() * 4,
    0L, TimeUnit.MILLISECONDS,
    LinkedBlockingQueue(),
    ThreadFactory {
      Thread(it, "TreeDiagramWorker-${threadNumber.incrementAndGet()}")
    }
  )

  init {
    @Suppress("LeakingThis")
    if ((target.isEmpty())) {
      addRouter(this)
    } else {
      target.forEach {
        addRouter(it)
      }
    }
  }

  override fun handle(content: HttpContent) {
    if (content is MutableHttpContent) {
      handle(content, getHandler(content, content.method, content.uri))
    } else {
      handle(content, getHandler(content.method, content.uri).first?.second)
    }
  }

  open fun handle(content: HttpContent, handler: ((HttpContent) -> Any?)?) {
    if (handler != null) {
      try {
        handler(content)
      } catch (e: Throwable) {
        val bos = ByteArrayOutputStream()
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

  open fun notFound(content: HttpContent) {
    content.finish(404)
  }

  open fun addRouter(handler: Any) {
    log?.info("add router {}", handler)
    handler.javaClass.allMethodsSequence.forEach { method ->
      method.isAccessible = true
      method.parameterTypes.let {
        if (!(it.size == 1 && HttpContent::class.java.isAssignableFrom(it[0])) && it.isNotEmpty()) {
          return@forEach
        }
      }
      log?.trace("mapping {} {}", method, method.parameterTypes)
      insertMapping(handler, method)
    }
  }

  fun addRouter(route: String, handler: (HttpContent) -> Any?) = addRouter(route, null, handler)
  fun addRouter(route: String, obj: Any?, handler: (HttpContent) -> Any?) {
    router[safeRoute(route)] = obj to handler
  }

  fun addRouter(method: String, route: String, handler: (HttpContent) -> Any?) = addRouter(method, route, null, handler)
  fun addRouter(method: String, route: String, obj: Any?, handler: (HttpContent) -> Any?) {
    getRouter(method)[safeRoute(route)] = obj to handler
  }

  fun getHandler(content: MutableHttpContent, method: String, route: String): ((HttpContent) -> Any?)? {
    val safeRoute = safeRoute(route)
    val router = getHandler(method, safeRoute)
    if (router.first != null) {
      router.second.forEach { (k, v) ->
        content.addParam(k, v)
      }
    }
    return router.first?.second ?: this.router[safeRoute].first?.second
  }

  fun getHandler(method: String, route: String): Pair<Pair<Any?, (HttpContent) -> Any?>?, List<Pair<String, String>>> {
    val safeRoute = safeRoute(route)
    val router = getRouter(method)[safeRoute]
    return if (router.first != null) router else this.router[safeRoute]
  }

  protected fun insertMapping(obj: Any, method: Method) {
    val mapping = obj::class.java.getAnnotation(Mapping::class.java)?.route ?: arrayOf("")
    method.annotations.forEach { annotation ->
      val (routes, router) = getRoutes(annotation) ?: return@forEach
      log?.info("mapping {} {} => {}", getRouteMethod(annotation), routes, method)
      routes.forEach { route ->
        if (mapping.isEmpty()) {
          addRouter(obj, method, route, router)
        } else mapping.forEach {
          addRouter(obj, method, safeRoute(it) + route, router)
        }
      }
    }
  }

  @Suppress("UNREACHABLE_CODE")
  fun addRouter(obj: Any, method: Method, route: String, router: Router<Pair<Any?, (HttpContent) -> Any?>>) {
    val doLog = method.doLog
    router[safeRoute(route)] = obj to (if (method.parameterTypes.isNotEmpty()) when (method.returnType) {
      Void::class.java -> lambda { content: HttpContent -> method(obj, content) }
      Void.TYPE -> lambda { content: HttpContent -> method(obj, content) }
      Unit::class.java -> lambda { content: HttpContent -> method(obj, content) }
      else -> when {
        method.getAnnotation(Html::class.java) != null -> lambda { content: HttpContent ->
          finishHtml(method(obj, content), content, doLog)
        }
        method.getAnnotation(Text::class.java) != null -> lambda { content: HttpContent ->
          finishText(method(obj, content), content, doLog)
        }
        method.getAnnotation(Json::class.java) != null -> lambda { content: HttpContent ->
          finishJson(method(obj, content), content, doLog)
        }
        else -> lambda { content: HttpContent ->
          autoReturn(method, method(obj, content), content, doLog)
        }
      }
    } else when {
      method.getAnnotation(Html::class.java) != null -> lambda { content: HttpContent ->
        finishHtml(method(obj), content, doLog)
      }
      method.getAnnotation(Text::class.java) != null -> lambda { content: HttpContent ->
        finishText(method(obj), content, doLog)
      }
      method.getAnnotation(Json::class.java) != null -> lambda { content: HttpContent ->
        finishJson(method(obj), content, doLog)
      }
      else -> lambda { content: HttpContent ->
        autoReturn(method, method(obj), content, doLog)
      }
    }).let {
      if (method.getAnnotation(BlockHandler::class.java) != null) lambda { content ->
        workerThread.execute { handle(content, it) }
      } else
        it
    }
  }

  fun deleteRouter(route: String, method: String) {
    getRouter(method).delRoute(safeRoute(route))
  }

  open fun deleteRouter(handler: Any) {
    log?.info("delete router {}", handler)
    @Suppress("LeakingThis")
    val clazz = handler.javaClass
    clazz.methods.forEach { method ->
      method.parameterTypes.let {
        if (!(it.size == 1 && HttpContent::class.java.isAssignableFrom(it[0])) && it.isNotEmpty()) {
          return@forEach
        }
      }
      log?.debug("delete mapping {}", method)
      deleteMapping(handler, method)
    }
  }

  protected fun deleteMapping(obj: Any, method: Method) {
    val mapping = obj::class.java.getAnnotation(Mapping::class.java)?.route ?: arrayOf("")
    method.annotations.forEach { annotation ->
      val (routes, router) = getRoutes(annotation) ?: return@forEach
      routes.forEach { route ->
        log?.info("delete route {} {} => {}", getRouteMethod(annotation), route, method)
        if (mapping.isEmpty()) {
          deleteMapping(obj, route, router)
        } else mapping.forEach {
          deleteMapping(obj, safeRoute(it) + route, router)
        }
      }
    }
  }

  protected fun deleteMapping(obj: Any, route: String, router: Router<Pair<Any?, (HttpContent) -> Any?>>) {
    val handler = router[safeRoute(route)].first
    if (handler?.first == obj) {
      router.delRoute(safeRoute(route))
    }
  }

  protected fun getRouteMethod(annotation: Annotation): String? = when (annotation) {
    is Mapping -> annotation.method.ifEmpty { annotation.methodEnum.method }
    is GetMapping -> "GET"
    is PostMapping -> "POST"
    is PutMapping -> "PUT"
    is DeleteMapping -> "DELETE"
    is PatchMapping -> "PATCH"
    is TraceMapping -> "TRACE"
    is HeadMapping -> "HEAD"
    is OptionsMapping -> "OPTIONS"
    is ConnectMapping -> "CONNECT"
    else -> null
  }

  protected fun getRoutes(annotation: Annotation) = when (annotation) {
    is Mapping -> annotation.route to getRouter(annotation.method.ifEmpty { annotation.methodEnum.method })
    is GetMapping -> annotation.route to getRouter("GET")
    is PostMapping -> annotation.route to getRouter("POST")
    is PutMapping -> annotation.route to getRouter("PUT")
    is DeleteMapping -> annotation.route to getRouter("DELETE")
    is PatchMapping -> annotation.route to getRouter("PATCH")
    is TraceMapping -> annotation.route to getRouter("TRACE")
    is HeadMapping -> annotation.route to getRouter("HEAD")
    is OptionsMapping -> annotation.route to getRouter("OPTIONS")
    is ConnectMapping -> annotation.route to getRouter("CONNECT")
    else -> null
  }

  protected fun getRouter(method: String): Router<Pair<Any?, (HttpContent) -> Any?>> = when {
    method.isEmpty() -> router
    else -> {
      val upperCaseMethod = method.uppercase()
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

    val Method.doLog get() = getAnnotation(NoReturnLog::class.java) == null

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

    fun autoReturn(method: Method, result: Any?, content: HttpContent, doLog: Boolean? = null) {
      if (content.finished || result is Unit) return
      method.getAnnotation(ContextType::class.java)?.let {
        content.setContextType(it.type.value)
        log?.debug("{}: autoReturn context type auto set to {}({})", content.remoteAddress, it.type.key, it.type.value)
      }
      autoReturn(result, content, doLog ?: method.doLog)
    }

    fun autoReturn(result: Any?, content: HttpContent, doLog: Boolean = true) {
      if (content.finished || result is Unit) return
      if (doLog) log?.debug("{}: autoReturn: {}", content.remoteAddress, result)
      when (result) {
        null -> content.finish(404)
        is ByteBuffer -> content.finishText(result)
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

    fun finishHtml(result: Any?, content: HttpContent, doLog: Boolean = true) {
      if (content.finished || result is Unit) return
      if (doLog) log?.debug("{} finishHtml {}", content.remoteAddress, result)
      when (result) {
        null -> content.finish(404)
        is ByteBuffer -> content.finishHtml(result)
        is ByteArray -> content.finishHtml(result)
        is String -> content.finishHtml(result.toByteArray())
        is StringBuffer -> content.finishHtml(result.toString().toByteArray())
        is StringBuilder -> content.finishHtml(result.toString().toByteArray())
        is Chunked -> {
          content.responseHtml()
          content.finishChunked(result)
        }
        else -> content.finishHtml(result.toString().toByteArray())
      }
    }

    fun finishText(result: Any?, content: HttpContent, doLog: Boolean = true) {
      if (content.finished || result is Unit) return
      if (doLog) log?.debug("{} finishText {}", content.remoteAddress, result)
      when (result) {
        null -> content.finish(404)
        is ByteBuffer -> content.finishText(result)
        is ByteArray -> content.finishText(result)
        is String -> content.finishText(result.toByteArray())
        is StringBuffer -> content.finishHtml(result.toString().toByteArray())
        is StringBuilder -> content.finishHtml(result.toString().toByteArray())
        is Chunked -> {
          content.responseText()
          content.finishChunked(result)
        }
        else -> content.finishText(result.toString().toByteArray())
      }
    }

    fun finishJson(result: Any?, content: HttpContent, doLog: Boolean = true) {
      if (content.finished || result is Unit) return
      if (doLog) log?.debug("{} finishJson {}", content.remoteAddress, result)
      when (result) {
        null -> content.finish(404)
        is ByteBuffer -> content.finishJson(result)
        is ByteArray -> content.finishJson(result)
        is String -> content.finishJson("\"$result\"".toByteArray())
        is StringBuffer -> content.finishHtml(result.toString().toByteArray())
        is java.lang.StringBuilder -> content.finishHtml(result.toString().toByteArray())
        is Number, Boolean -> content.finishJson(result.toString().toByteArray())
        is Chunked -> {
          content.responseJson()
          content.finishChunked(result)
        }
        else -> {
          val json = json?.toJson(result)
          if (doLog) log?.debug("{} finishJson: generate json {}", content.remoteAddress, json)
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
