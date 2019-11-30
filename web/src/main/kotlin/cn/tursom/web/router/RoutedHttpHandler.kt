package cn.tursom.web.router

import cn.tursom.json.JsonWorkerImpl
import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpContent
import cn.tursom.web.HttpHandler
import cn.tursom.web.router.impl.SimpleRouter
import cn.tursom.web.router.mapping.*
import cn.tursom.web.utils.Chunked
import org.slf4j.LoggerFactory
import java.io.File
import java.io.RandomAccessFile
import java.lang.reflect.Method

/**
 * 自动添加路径映射的处理器
 * 会将 public 且参数仅为一个 T 的方法自动注册到路由器里
 * 例：public void Index(HttpContext context)
 *     fun Index(context: HttpContext)
 * 如果加了 @Mapping 注解，会依据注解提供的路由路径注册
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class RoutedHttpHandler<T : HttpContent, in E : ExceptionContent>(
  target: Any? = null,
  val routerMaker: () -> Router<(T) -> Unit> = { SimpleRouter() }
) : HttpHandler<T, E> {
  protected val router: Router<(T) -> Unit> = routerMaker()
  protected val routerMap: HashMap<String, Router<(T) -> Unit>> = HashMap()

  init {
    @Suppress("LeakingThis")
    addRouter(target ?: this)
  }

  override fun handle(content: T) = handle(content, getHandler(content.method, content.uri))

  open fun handle(content: T, handler: ((T) -> Unit)?) {
    if (handler != null) {
      handler(content)
    } else {
      notFound(content)
    }
  }

  open fun notFound(content: T) {
    content.finish(404)
  }

  fun addRouter(handler: Any) {
    @Suppress("LeakingThis")
    val clazz = handler.javaClass
    clazz.methods.forEach { method ->
      method.parameterTypes.let {
        if (it.size != 1 || !HttpContent::class.java.isAssignableFrom(it[0])) {
          return@forEach
        } else if (it.isNotEmpty()) {
          return@forEach
        }
      }
      insertMapping(handler, method)
    }
  }

  fun addRouter(route: String, handler: (T) -> Unit) {
    router[safeRoute(route)] = handler
  }

  fun addRouter(method: String, route: String, handler: (T) -> Unit) {
    getRouter(method)[safeRoute(route)] = handler
  }

  fun getHandler(method: String, route: String): ((T) -> Unit)? =
    getRouter(method)[route].first ?: this.router[route].first

  fun deleteRouter(route: String, method: String) {
    getRouter(method).delRoute(safeRoute(route))
  }

  protected fun insertMapping(obj: Any, method: Method) {
    method.annotations.forEach { annotation ->
      val routes: Array<out String>
      val router: Router<(T) -> Unit>
      when (annotation) {
        is Mapping -> {
          routes = annotation.route
          router = getRouter(annotation.method)
        }
        is GetMapping -> {
          routes = annotation.route
          router = getRouter("GET")
        }
        is PostMapping -> {
          routes = annotation.route
          router = getRouter("POST")
        }
        is PutMapping -> {
          routes = annotation.route
          router = getRouter("PUT")
        }
        is DeleteMapping -> {
          routes = annotation.route
          router = getRouter("DELETE")
        }
        is PatchMapping -> {
          routes = annotation.route
          router = getRouter("PATCH")
        }
        is TraceMapping -> {
          routes = annotation.route
          router = getRouter("TRACE")
        }
        is HeadMapping -> {
          routes = annotation.route
          router = getRouter("HEAD")
        }
        is OptionsMapping -> {
          routes = annotation.route
          router = getRouter("OPTIONS")
        }
        is ConnectMapping -> {
          routes = annotation.route
          router = getRouter("CONNECT")
        }
        else -> return@forEach
      }
      routes.forEach { route ->
        log?.info("method route {} mapped to {}", route, method)
        router[safeRoute(route)] = handler@{ content ->
          if (method.parameterTypes.isEmpty()) {
            val result = method(obj) ?: return@handler
            when (result) {
              is String -> content.finishHtml(result.toByteArray())
              is ByteArray -> content.finishText(result)
              is File -> content.finishFile(result)
              is RandomAccessFile -> content.finishFile(result)
              is Chunked -> content.finishChunked(result)
              else -> json?.let {
                content.finishJson(json.toJson(result)!!.toByteArray())
              } ?: content.finishText(result.toString().toByteArray())
            }
          } else {
            method(obj, content)
          }
        }
      }
    }
  }

  protected fun getRouter(method: String): Router<(T) -> Unit> = when {
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

    private fun safeRoute(route: String) = if (route.first() == '/') route else "/$route"
  }
}
