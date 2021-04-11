package cn.tursom.web.router.impl

import cn.tursom.web.router.Router
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class FilterRouter<T>(
  val matchPair: Pair<Char, Char> = '{' to '}'
) : Router<T> {
  companion object {
    val slashOnceMore = Regex("/+")
  }

  private val flower =
    Regex("\\\\${matchPair.first}[^\\\\${matchPair.first}\\\\${matchPair.second}]*\\\\${matchPair.second}")
  private val lock = ReentrantReadWriteLock()
  private val routeList = ArrayList<RouteContext<T>>()

  override fun addSubRoute(route: String, value: T?, onDestroy: ((oldValue: T) -> Unit)?) = lock.write {
    val matcher = DefaultMatcher(route, matchPair)
    val context = RouteContext(matcher.route, value, onDestroy, matcher)
    routeList.add(context)
    Unit
  }

  override fun delRoute(route: String): Unit = lock.write {
    val rRoute = route.substringBefore('?').replace(flower, "{}")
    val index = routeList.indexOfFirst { it.route == rRoute }
    if (index < 0) return
    val (_, oldValue, onDestroy, _) = routeList[index]
    routeList.removeAt(index)
    oldValue?.let { onDestroy?.invoke(it) }
  }

  override fun get(route: String): Pair<T?, List<Pair<String, String>>> = lock.read {
    routeList.forEach {
      val (match, param) = it.matcher.match(route)
      if (match) {
        return@read it.value to param
      }
    }
    null to listOf()
  }

  interface Matcher {
    fun match(route: String): Pair<Boolean, List<Pair<String, String>>>
  }

  data class RouteContext<T>(
    val route: String,
    val value: T?,
    val onDestroy: ((oldValue: T) -> Unit)?,
    val matcher: Matcher
  )

  class DefaultMatcher(route: String, matchPair: Pair<Char, Char> = '{' to '}') : Matcher {
    val route: String
    private val matchList: List<String>
    private val paramList: List<String>

    init {
      val flower = Regex("\\${matchPair.first}[^\\${matchPair.first}\\${matchPair.second}]*\\${matchPair.second}")
      this.route = route.substringBefore('?').replace(flower, "${matchPair.first}${matchPair.second}").let {
        if (it.endsWith('/')) {
          it
        } else {
          "$it/"
        }
      }.replace(slashOnceMore, "/")
      val rRoute = route.substringBefore('?').let {
        if (it.endsWith('/')) {
          it
        } else {
          "$it/"
        }
      }.replace(slashOnceMore, "/")
      this.matchList = rRoute.split(flower).toMutableList()
      matchList.add("")
      val paramList = ArrayList<String>()
      var match = matchPair.first
      var startIndex = 0
      if (startIndex >= 0) {
        while (true) {
          val endIndex = rRoute.indexOf(match, startIndex)
          if (endIndex < 0) {
            if (match == matchPair.second) {
              paramList.add(rRoute.substring(startIndex))
            }
            break
          }
          if (match == matchPair.second) {
            paramList.add(rRoute.substring(startIndex, endIndex))
          }
          startIndex = endIndex + 1
          match = when (match) {
            matchPair.first -> matchPair.second
            matchPair.second -> matchPair.first
            else -> matchPair.first
          }
        }
      }
      this.paramList = paramList
    }

    override fun match(route: String): Pair<Boolean, List<Pair<String, String>>> {
      val rRoute = if (route.endsWith('/')) {
        route
      } else {
        "$route/"
      }.replace(slashOnceMore, "/")
      val iterator = matchList.iterator()
      val paramIterator = paramList.iterator()
      var context = iterator.next()
      if (!rRoute.startsWith(context)) return false to listOf()
      var startIndex = context.length
      val paramList = LinkedList<Pair<String, String>>()
      while (iterator.hasNext()) {
        val preContext = context
        context = iterator.next()
        val endIndex = rRoute.indexOf(context, startIndex)
        if (endIndex < 0) {
          break
        }
        val paramValue = rRoute.substring(startIndex, endIndex)
        if (paramValue.contains('/')) {
          break
        }
        if (paramIterator.hasNext()) {
          paramList.add(paramIterator.next() to paramValue)
        } else if (context.isEmpty() && paramValue.isEmpty()) {
          startIndex += preContext.length - 1
          break
        } else {
          break
        }
        startIndex = endIndex + 1
      }
      return (startIndex == rRoute.length) to paramList
    }
  }
}