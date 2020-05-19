package cn.tursom.web.router.impl

import cn.tursom.web.router.Router
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.ArrayList
import kotlin.concurrent.read
import kotlin.concurrent.write

class FilterRouter<T> : Router<T> {
  companion object {
    val flower = Regex("\\{[^{}]*\\}")
  }

  private val lock = ReentrantReadWriteLock()
  private val routeList = ArrayList<RouteContext<T>>()

  override fun addSubRoute(route: String, value: T?, onDestroy: ((oldValue: T) -> Unit)?) = lock.write {
    val matcher = DefaultMatcher(route)
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

  class DefaultMatcher(route: String) : Matcher {
    val route: String = route.substringBefore('?').replace(flower, "{}")
    private val matchList = route.split(flower).toMutableList()
    private val paramList: List<String>

    init {
      matchList.add("")
      val rRoute = route.substringBefore('?')
      val paramList = ArrayList<String>()
      var match = '{'
      var startIndex = 0
      if (startIndex >= 0) {
        while (true) {
          val endIndex = rRoute.indexOf(match, startIndex)
          if (endIndex < 0) {
            if (match == '}') {
              paramList.add(rRoute.substring(startIndex))
            }
            break
          }
          if (match == '}') {
            paramList.add(rRoute.substring(startIndex, endIndex))
          }
          startIndex = endIndex + 1
          match = when (match) {
            '{' -> '}'
            '}' -> '{'
            else -> '{'
          }
        }
      }
      this.paramList = paramList
    }

    override fun match(route: String): Pair<Boolean, List<Pair<String, String>>> {
      val iterator = matchList.iterator()
      val paramIterator = paramList.iterator()
      var context = iterator.next()
      if (!route.startsWith(context)) return false to listOf()
      var startIndex = context.length
      val paramList = LinkedList<Pair<String, String>>()
      while (iterator.hasNext()) {
        val preContext = context
        context = iterator.next()
        val endIndex = route.indexOf(context, startIndex)
        if (endIndex < 0) {
          break
        }
        val paramValue = route.substring(startIndex, endIndex)
        if (paramValue.contains('/')) {
          break
        }
        if (paramIterator.hasNext()) {
          paramList.add(paramIterator.next() to paramValue)
        } else {
          startIndex += preContext.length - 1
          break
        }
        startIndex = endIndex + 1
      }
      return (startIndex == route.length) to paramList
    }
  }
}