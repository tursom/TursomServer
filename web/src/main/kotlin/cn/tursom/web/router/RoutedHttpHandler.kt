package cn.tursom.web.router

import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpContent
import cn.tursom.web.HttpHandler
import cn.tursom.web.router.impl.SimpleRouter
import org.slf4j.LoggerFactory

abstract class RoutedHttpHandler<T : HttpContent, in E : ExceptionContent>(
  val router: Router<(T) -> Unit> = SimpleRouter()
) : HttpHandler<T, E> {
  override fun handle(content: T) {
    val handler = router[content.uri].first
    if (handler != null) {
      handler(content)
    } else {
      content.finish(404)
    }
  }

  init {
    val clazz = this.javaClass
    clazz.methods.forEach { method ->
      method.parameterTypes.let {
        if (it.size != 1 || !HttpContent::class.java.isAssignableFrom(it[0]))
          return@forEach
      }
      val routes = method.getAnnotation(Mapping::class.java)?.route ?: return@forEach
      routes.forEach { route ->
        log?.info("method {} mapped to route {}", method, route)
        router[route] = { content -> method(this, content) }
      }
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
