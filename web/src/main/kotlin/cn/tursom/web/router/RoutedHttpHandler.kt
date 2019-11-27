package cn.tursom.web.router

import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpContent
import cn.tursom.web.HttpHandler
import cn.tursom.web.router.impl.SimpleRouter
import cn.tursom.web.router.mapping.*
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class RoutedHttpHandler<T : HttpContent, in E : ExceptionContent>(
  target: Any? = null,
  val routerMaker: () -> Router<(T) -> Unit> = { SimpleRouter() }
) : HttpHandler<T, E> {

  private val router: Router<(T) -> Unit> = routerMaker()
  private val routerMap: HashMap<String, Router<(T) -> Unit>> = HashMap()

  init {
    @Suppress("LeakingThis")
    addRouter(target ?: this)
  }

  override fun handle(content: T) {
    val router = getRouter(content.method)
    var handler = router[content.uri].first
    if (handler == null) {
      handler = this.router[content.uri].first
    }
    if (handler != null) {
      handler(content)
    } else {
      content.finish(404)
    }
  }

  fun addRouter(handler: Any) {
    @Suppress("LeakingThis")
    val clazz = handler.javaClass
    clazz.methods.forEach { method ->
      method.parameterTypes.let {
        if (it.size != 1 || !HttpContent::class.java.isAssignableFrom(it[0]))
          return@forEach
      }
      insertMapping(method)
    }
  }

  fun addRouter(route: String, handler: (T) -> Unit) {
    router[safeRoute(route)] = handler
  }

  fun addRouter(method: String, route: String, handler: (T) -> Unit) {
    getRouter(method)[safeRoute(route)] = handler
  }

  fun deleteRouter(route: String, method: String) {
    getRouter(method).delRoute(safeRoute(route))
  }

  private fun insertMapping(method: Method) {
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
        log?.info("method {} mapped to route {}", method, route)
        router[safeRoute(route)] = { content -> method(this, content) }
      }
    }
  }

  private fun getRouter(method: String): Router<(T) -> Unit> = when {
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
    } catch (e: Exception) {
      null
    }

    private fun safeRoute(route: String) = if (route.first() == '/') route else "/$route"
  }
}
