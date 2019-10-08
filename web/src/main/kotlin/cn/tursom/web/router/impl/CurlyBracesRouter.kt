package cn.tursom.web.router.impl

import cn.tursom.web.router.IRouter

/**
 * 匹配类似
 * /java/{mod}
 * /java/{mod}/{id}
 * /java/a{mod}/id
 * /java/a{mod}_i/id
 */
class CurlyBracesRouter<T> : IRouter<T> {
	override fun addSubRoute(route: String, value: T?, onDestroy: ((oldValue: T) -> Unit)?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun delRoute(route: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun get(route: String): Pair<T?, List<Pair<String, String>>> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}

@Suppress("MemberVisibilityCanBePrivate")
class RouteContext(
	val nodeList: List<String>,
	var location: Int = 0
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
		return if (matchesAndParse(context, list)) {
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