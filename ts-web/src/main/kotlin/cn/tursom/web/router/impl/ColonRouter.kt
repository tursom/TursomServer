package cn.tursom.web.router.impl

import cn.tursom.core.binarySearch
import cn.tursom.web.router.Router
import cn.tursom.web.router.impl.colonnode.AnyColonNode
import cn.tursom.web.router.impl.colonnode.ColonNode
import cn.tursom.web.router.impl.colonnode.IColonNode
import cn.tursom.web.router.impl.colonnode.PlaceholderColonNode

/**
 * 支持以冒号“:”开头的匹配
 * 例：
 * 		/route/:aaa
 * 		/route/aaa/:bbb
 * 		/route/aaa/:bbb/ccc
 */
@Suppress("unused", "unused", "MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
class ColonRouter<T> : Router<T> {
  private val rootNode = ColonNode<T>(listOf(""), 0)
  val root: IColonNode<T> = rootNode

  override fun addSubRoute(route: String, value: T?, onDestroy: ((oldValue: T) -> Unit)?) {
    val routeList = route.split('?')[0].split('/').filter { it.isNotEmpty() }
    var routeNode = rootNode
    var r: String
    var index = 0
    while (index < routeList.size) {
      r = routeList[index]
      routeNode = when {
        r.isEmpty() -> routeNode

        r == "*" -> routeNode.wildSubRouter ?: run {
          val node = AnyColonNode<T>(routeList, index)
          routeNode.wildSubRouter = node
          index = routeList.size - 1
          node
        }

        r[0] == ':' -> run {
          val node = synchronized(routeNode.placeholderRouterList!!) {
            val matchLength = PlaceholderColonNode.matchLength(routeList, index)
            routeNode.placeholderRouterList!!.binarySearch { it.size - matchLength } ?: run {
              routeNode.addNode(routeList, index, null)
              routeNode.placeholderRouterList!!.binarySearch { it.size - matchLength }!!
            }
          }
          index += node.size - 1
          node
        }

        else -> synchronized(routeNode.subRouterMap) {
          routeNode.subRouterMap[r] ?: run {
            val node = ColonNode<T>(routeList, index)
            routeNode.subRouterMap[r] = node
            node
          }
        }
      }
      index++
    }
    val oldValue = routeNode.value
    if (oldValue != null) onDestroy?.invoke(oldValue)
    routeNode.value = value
    routeNode.routeList = routeList
    routeNode.index = index - 1
  }

  override fun delRoute(route: String) {
    this[route] = null
  }

  override operator fun get(route: String): Pair<T?, List<Pair<String, String>>> {
    val list = ArrayList<Pair<String, String>>()
    return rootNode[route.split('?')[0].split('/').filter { it.isNotEmpty() }, list]?.value to list
  }

  private fun toString(node: ColonNode<T>, stringBuilder: StringBuilder, indentation: String) {
    if (
      node.value == null &&
      node.subRouterMap.isEmpty() &&
      node.placeholderRouterList?.isEmpty() != false &&
      node.wildSubRouter == null
    ) {
      return
    }

    if (indentation.isNotEmpty()) {
      stringBuilder.append(indentation)
      stringBuilder.append("- ")
    }
    stringBuilder.append("${node.singleRoute}${if (node.value != null) "    ${node.value}" else ""}\n")

    if (node is AnyColonNode) return

    val subIndentation = if (indentation.isEmpty()) "|" else "$indentation  |"

    node.subRouterMap.forEach { (_, u) ->
      toString(u, stringBuilder, subIndentation)
    }
    node.placeholderRouterList?.forEach {
      toString(it, stringBuilder, subIndentation)
    }
    toString(node.wildSubRouter ?: return, stringBuilder, subIndentation)
    return
  }

  override fun toString(): String {
    val stringBuilder = StringBuilder()
    toString(rootNode, stringBuilder, "")
    return stringBuilder.toString()
  }
}

