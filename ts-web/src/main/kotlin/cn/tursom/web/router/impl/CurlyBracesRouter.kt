package cn.tursom.web.router.impl

import cn.tursom.core.datastruct.StringRadixTree
import cn.tursom.web.router.Router

/**
 * 匹配类似
 * /java/:mod
 * /java/:mod/:id
 * 不支持在参数后加静态路径
 */
class CurlyBracesRouter<T> : Router<T> {
  private val router = StringRadixTree<Pair<T, List<String>>>()

  override fun addSubRoute(route: String, value: T?, onDestroy: ((oldValue: T) -> Unit)?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun delRoute(route: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun get(route: String): Pair<T?, List<Pair<String, String>>> {
    val t = router.listGet(route)
    if (t.isEmpty()) {
      return null to listOf()
    }
    val pair = t.last()
    if (pair.first == null || pair.second != route.length) {
      return null to listOf()
    }

    val list = ArrayList<Pair<String, String>>()
    pair.first!!.second.forEach {
    }

    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

@Suppress("MemberVisibilityCanBePrivate")
class RouteContext(
  val nodeList: List<String>,
  var location: Int = 0,
) {
  constructor(route: String) : this(route.substringBefore('?').split('/').filter { it.isNotEmpty() })

  val remain get() = nodeList.size - location
  val peek get() = nodeList[location]
  val copy get() = RouteContext(nodeList, location)

  fun increase(size: Int) {
    location += size
  }

  fun reset() {
    location = 0
  }
}

@Suppress("MemberVisibilityCanBePrivate")
class NodeContext(val nodeList: List<String>, val location: Int = 0) {
  val remain get() = nodeList.size - location
  val peek get() = nodeList[location]
  val route get() = RouteContext(nodeList, location)
}

interface ICurlyBracesNode {
  val deep: Int

  /**
   * 用来对路径进行匹配与获得参数
   */
  fun matchesAndParse(context: RouteContext, paramList: MutableList<Pair<String, String>>): Boolean

  fun matchesAndParse(context: RouteContext): List<Pair<String, String>>? {
    val list = ArrayList<Pair<String, String>>()
    return if (matchesAndParse(context, list as MutableList<Pair<String, String>>)) {
      list
    } else {
      null
    }
  }
}

class CurlyBracesNode(private val nodeContext: NodeContext) : ICurlyBracesNode {
  override val deep: Int get() = 1
  override fun matchesAndParse(context: RouteContext, paramList: MutableList<Pair<String, String>>): Boolean {
    return if (nodeContext.peek == context.peek) {
      context.increase(1)
      true
    } else {
      false
    }
  }
}

class PlaceholderCurlyBracesNode