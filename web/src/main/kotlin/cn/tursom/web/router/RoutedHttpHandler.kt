package cn.tursom.web.router

import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpContent
import cn.tursom.web.HttpHandler
import cn.tursom.web.router.impl.SimpleRouter
import cn.tursom.web.router.mapping.*
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

abstract class RoutedHttpHandler<T : HttpContent, in E : ExceptionContent>(
  val routerMaker: () -> Router<(T) -> Unit> = { SimpleRouter() }
) : HttpHandler<T, E> {
  private val router: Router<(T) -> Unit> = routerMaker()
  private val routerMap: HashMap<String, Router<(T) -> Unit>> = HashMap()

  init {
    val clazz = this.javaClass
    clazz.methods.forEach { method ->
      method.parameterTypes.let {
        if (it.size != 1 || !HttpContent::class.java.isAssignableFrom(it[0]))
          return@forEach
      }
      insertMapping(method)
    }
  }

  override fun handle(content: T) {
    val handler = router[content.uri].first
    if (handler != null) {
      handler(content)
    } else {
      content.finish(404)
    }
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
        router[route] = { content -> method(this, content) }
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
  }
}
