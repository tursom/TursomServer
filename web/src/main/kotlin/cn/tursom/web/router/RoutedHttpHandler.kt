package cn.tursom.web.router

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.json.JsonWorkerImpl
import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpContent
import cn.tursom.web.HttpHandler
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
      log?.debug("try mapping {}", method)
      method.parameterTypes.let {
        if (!(it.size == 1 && HttpContent::class.java.isAssignableFrom(it[0])) && it.isNotEmpty()) {
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
            when {
              method.getAnnotation(Html::class.java) != null -> finishHtml(result, content)
              method.getAnnotation(Text::class.java) != null -> finishText(result, content)
              method.getAnnotation(Json::class.java) != null -> finishJson(result, content)
              else -> autoReturn(result, content)
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

    private fun autoReturn(result: Any, content: HttpContent) = when (result) {
      is String -> content.finishText(result.toByteArray())
      is ByteArray -> content.finishText(result)
      is File -> {
        content.autoContextType(result.name)
        content.finishFile(result)
      }
      is RandomAccessFile -> content.finishFile(result)
      is Chunked -> content.finishChunked(result)
      else -> finishJson(result, content)
    }

    private fun finishHtml(result: Any, content: HttpContent) = when (result) {
      is ByteBuffer -> content.finishHtml(result)
      is ByteArray -> content.finishHtml(result)
      is String -> content.finishHtml(result.toByteArray())
      else -> content.finishHtml(result.toString().toByteArray())
    }

    private fun finishText(result: Any, content: HttpContent) = when (result) {
      is ByteBuffer -> content.finishText(result)
      is ByteArray -> content.finishText(result)
      is String -> content.finishText(result.toByteArray())
      else -> content.finishText(result.toString().toByteArray())
    }

    private fun finishJson(result: Any, content: HttpContent) {
      when (result) {
        is ByteBuffer -> content.finishJson(result)
        is ByteArray -> content.finishJson(result)
        is String -> content.finishJson("\"$result\"".toByteArray())
        is Byte, Short, Int, Long, Float, Double, Boolean -> content.finishJson(result.toString().toByteArray())
        else -> {
          val json = json?.toJson(result)?.toByteArray()
          if (json != null) {
            content.finishJson(json)
          } else {
            content.finishText(result.toString().toByteArray())
          }
        }
      }
    }
  }
}
