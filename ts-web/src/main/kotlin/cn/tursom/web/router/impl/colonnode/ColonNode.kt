package cn.tursom.web.router.impl.colonnode

import cn.tursom.core.util.binarySearch

@Suppress("MemberVisibilityCanBePrivate")
open class ColonNode<T>(
  var routeList: List<String>,
  var index: Int,
  override var value: T? = null,
) : IColonNode<T> {
  val route: String = routeList[index]
  var wildSubRouter: AnyColonNode<T>? = null
  open val placeholderRouterList: ArrayList<PlaceholderColonNode<T>>? = ArrayList(0)
  val subRouterMap = HashMap<String, ColonNode<T>>(0)

  open val singleRoute
    get() = "/$route"

  override fun forEach(action: (node: IColonNode<T>) -> Unit) {
    placeholderRouterList?.forEach(action)
    subRouterMap.forEach { (_, u) -> action(u) }
    wildSubRouter?.let(action)
  }

  open fun match(
    route: List<String>,
    startIndex: Int,
  ): Pair<Boolean, Int> = (route.size > startIndex && route[startIndex] == this.route) to 1

  fun addNode(route: List<String>, startIndex: Int, value: T? = null): Int {
    val r = route[startIndex]
    return when {
      r.isEmpty() -> return addNode(route, startIndex + 1)
      r == "*" -> {
        wildSubRouter = AnyColonNode(route, startIndex)
        1
      }
      r[0] == ':' -> {
        val node: PlaceholderColonNode<T> = PlaceholderColonNode(route, startIndex, value = value)
        // 必须保证 placeholderRouterList 存在，而且还不能有这个长度的节点
        if (synchronized(placeholderRouterList!!) {
            placeholderRouterList!!.binarySearch { it.size - node.size }
          } != null) {
          throw Exception()
        }
        synchronized(placeholderRouterList!!) {
          placeholderRouterList?.add(node)
          placeholderRouterList?.sortBy { it.size }
        }
        node.size
      }
      else -> synchronized(subRouterMap) {
        subRouterMap[r] = ColonNode(route, startIndex, value)
        1
      }
    }
  }

  operator fun get(route: List<String>, startIndex: Int = 0): Pair<ColonNode<T>?, Int> {
    val r = route[startIndex]
    if (r.isEmpty()) return this to 1

    val value = synchronized(subRouterMap) { subRouterMap[r] }
    if (value != null) return value to 1

    val matchLength = route.size - startIndex
    val exactRoute = placeholderRouterList?.let { list ->
      synchronized(list) { list.binarySearch { matchLength - it.size } }
    }
    if (exactRoute != null) return exactRoute to matchLength

    placeholderRouterList?.let { list ->
      synchronized(list) {
        list.forEach {
          val subRoute = it.getRoute(route, startIndex + it.size)
          if (subRoute != null) return subRoute to route.size - startIndex
        }
      }
    }

    return wildSubRouter to 1
  }

  fun getRoute(route: List<String>, startIndex: Int = 0): ColonNode<T>? {
    var index = startIndex
    var routeNode = this
    while (index < route.size) {
      val (node, size) = routeNode[route, index]
      routeNode = node ?: return null
      index += size
    }
    return routeNode
  }

  operator fun get(
    route: List<String>,
    startIndex: Int = 0,
    routeList: java.util.AbstractList<Pair<String, String>>,
  ): Pair<ColonNode<T>?, Int> {
    val r = route[startIndex]
    if (r.isEmpty()) {
      return this to 1
    }

    val value = synchronized(subRouterMap) { subRouterMap[r] }
    if (value != null) return value to 1

    val matchLength = route.size - startIndex
    val exactRoute = placeholderRouterList?.let { list ->
      synchronized(list) { list.binarySearch { matchLength - it.size } }
    }
    if (exactRoute != null) {
      exactRoute.routeList.forEachIndexed { index, s ->
        if (s.isNotEmpty() && s[0] == ':') routeList.add(s.substring(1) to route[index])
      }
      return exactRoute to matchLength
    }

    val list = ArrayList<Pair<String, String>>()
    placeholderRouterList?.let { routerList ->
      synchronized(routerList) {
        routerList.forEach {
          list.clear()
          val subRoute = it.getRoute(route, startIndex + it.size, list)
          if (subRoute != null) {
            subRoute.routeList.forEachIndexed { index, s ->
              if (s.isNotEmpty()) when {
                s == "*" -> for (i in index until route.size) {
                  routeList.add("*" to route[i])
                }
                s[0] == ':' -> routeList.add(s.substring(1) to route[index])
              }
            }
            var listIndex = 0
            var routeIndex = 0
            while (listIndex < list.size && routeIndex <= index) {
              val s = this.routeList[routeIndex++]
              if (s.isNotEmpty() && s[0] == ':') {
                routeList.add(s to list[listIndex++].second)
              }
            }
            return subRoute to route.size - startIndex
          }
        }
      }
    }

    for (i in startIndex until route.size)
      routeList.add("*" to route[i])
    return wildSubRouter to 1
  }

  operator fun get(
    route: List<String>,
    routeList: java.util.AbstractList<Pair<String, String>>,
  ) = getRoute(route, 0, routeList)

  fun getRoute(
    route: List<String>,
    startIndex: Int = 0,
    routeList: java.util.AbstractList<Pair<String, String>>,
  ): ColonNode<T>? {
    var index = startIndex
    var routeNode = this
    while (routeNode !is AnyColonNode && index < route.size) {
      val (node, size) = routeNode[route, index, routeList]
      routeNode = node ?: return null
      index += size
    }
    return routeNode
  }

  override fun toString(): String {
    val stringBuilder = StringBuilder("/")
    for (i in 0..index) {
      val s = routeList[i]
      if (s.isNotEmpty()) stringBuilder.append("$s/")
    }
    if (value != null) {
      stringBuilder.append("    $value")
    }
    return stringBuilder.toString()
  }
}