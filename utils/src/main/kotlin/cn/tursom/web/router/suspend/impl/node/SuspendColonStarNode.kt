package cn.tursom.web.router.suspend.impl.node

import cn.tursom.utils.asynclock.AsyncReadFirstRWLock
import cn.tursom.core.binarySearch

@Suppress("MemberVisibilityCanBePrivate")
internal open class SuspendColonStarNode<T>(
	var routeList: List<String>,
	var index: Int,
	override var value: T? = null
) : ISuspendColonStarNode<T> {
	val route: String = routeList[index]
	var wildSubRouter: SuspendAnyColonStarNode<T>? = null

	private val placeholderRouterListLock = AsyncReadFirstRWLock()
	protected open val placeholderRouterList: ArrayList<SuspendPlaceholderColonStarNode<T>>? = ArrayList(0)

	private val subRouterMapLock = AsyncReadFirstRWLock()
	val subRouterMap = HashMap<String, cn.tursom.web.router.suspend.impl.node.SuspendColonStarNode<T>>(0)

	override val lastRoute
		get() = "/$route"

	override val fullRoute: String by lazy {
		val stringBuilder = StringBuilder("")
		for (i in 0..index) {
			val s = routeList[i]
			if (s.isNotEmpty()) stringBuilder.append("/$s")
		}
		stringBuilder.toString()
	}

	val placeholderRouterListEmpty
		get() = placeholderRouterList?.isEmpty() ?: true

	override val empty: Boolean
		get() = value == null &&
			subRouterMap.isEmpty() &&
			placeholderRouterListEmpty &&
			wildSubRouter == null

	override suspend fun forEach(action: suspend (node: ISuspendColonStarNode<T>) -> Unit) {
		placeholderRouterListLock.doRead {
			placeholderRouterList?.forEach { action(it) }
		}
		subRouterMapLock.doRead {
			subRouterMap.forEach { (_, u) -> action(u) }
		}
		wildSubRouter?.let { action(it) }
	}

	suspend fun forEachPlaceholderRouter(block: suspend (SuspendPlaceholderColonStarNode<T>) -> Unit) {
		placeholderRouterListLock.doRead { placeholderRouterList?.forEach { block(it) } }
	}

	suspend fun getPlaceholderRouter(length: Int): SuspendPlaceholderColonStarNode<T>? {
		return placeholderRouterListLock.doRead { placeholderRouterList!!.binarySearch { it.size - length } }
	}

	open fun match(
		route: List<String>,
		startIndex: Int
	): Pair<Boolean, Int> = (route.size > startIndex && route[startIndex] == this.route) to 1

	suspend fun addNode(route: List<String>, startIndex: Int, value: T? = null): Int {
		val r = route[startIndex]
		return when {
			r.isEmpty() -> return addNode(route, startIndex + 1)
			r == "*" -> {
				wildSubRouter = SuspendAnyColonStarNode(route, startIndex, null)
				1
			}
			r[0] == ':' -> {
				val node: SuspendPlaceholderColonStarNode<T> = SuspendPlaceholderColonStarNode(
					route,
					startIndex,
					value = value
				)
				// 必须保证 placeholderRouterList 存在，而且还不能有这个长度的节点
				if (placeholderRouterListLock.doRead { placeholderRouterList!!.binarySearch { it.size - node.size } } != null) {
					throw Exception()
				}
				placeholderRouterListLock.doWrite {
					placeholderRouterList?.add(node)
					placeholderRouterList?.sortBy { it.size }
				}
				node.size
			}
			else -> {
				subRouterMap[r] = SuspendColonStarNode(route, startIndex, value)
				1
			}
		}
	}

	operator fun get(route: List<String>, startIndex: Int = 0): Pair<cn.tursom.web.router.suspend.impl.node.SuspendColonStarNode<T>?, Int> {
		val r = route[startIndex]
		if (r.isEmpty()) return this to 1

		val value = subRouterMap[r]
		if (value != null) return value to 1

		val matchLength = route.size - startIndex
		val exactRoute = placeholderRouterList?.let { list ->
			list.binarySearch { matchLength - it.size }
		}
		if (exactRoute != null) return exactRoute to matchLength

		placeholderRouterList?.let { list ->
			list.forEach {
				val subRoute = it.getRoute(route, startIndex + it.size)
				if (subRoute != null) return subRoute to route.size - startIndex
			}
		}

		return wildSubRouter to 1
	}

	fun getRoute(route: List<String>, startIndex: Int = 0): cn.tursom.web.router.suspend.impl.node.SuspendColonStarNode<T>? {
		var index = startIndex
		var routeNode = this
		while (index < route.size) {
			val (node, size) = routeNode[route, index]
			routeNode = node ?: return null
			index += size
		}
		return routeNode
	}

	open suspend fun get(
		route: List<String>,
		startIndex: Int = 0,
		routeList: java.util.AbstractList<Pair<String, String>>
	): Pair<cn.tursom.web.router.suspend.impl.node.SuspendColonStarNode<T>?, Int> {
		val r = route[startIndex]
		if (r.isEmpty()) {
			return this to 1
		}

		val value = subRouterMapLock.doRead { subRouterMap[r] }
		if (value != null) return value to 1

		val matchLength = route.size - startIndex
		val exactRoute = placeholderRouterListLock.doRead {
			placeholderRouterList?.binarySearch { matchLength - it.size }
		}
		if (exactRoute != null) {
			exactRoute.routeList.forEachIndexed { index, s ->
				if (s.isNotEmpty() && s[0] == ':') routeList.add(s.substring(1) to route[index])
			}
			return exactRoute to matchLength
		}

		if (placeholderRouterList != null) {
			val list = ArrayList<Pair<String, String>>()
			val detected = placeholderRouterListLock.doRead {
				placeholderRouterList?.let { routerList ->
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
							return@doRead subRoute to route.size - startIndex
						}
					}
				}
				null
			}
			if (detected != null) return detected
		}

		for (i in startIndex until route.size)
			routeList.add("*" to route[i])
		return wildSubRouter to 1
	}

	suspend fun get(
		route: List<String>,
		routeList: java.util.AbstractList<Pair<String, String>>
	) = getRoute(route, 0, routeList)

	suspend fun getRoute(
		route: List<String>,
		startIndex: Int = 0,
		routeList: java.util.AbstractList<Pair<String, String>>
	): cn.tursom.web.router.suspend.impl.node.SuspendColonStarNode<T>? {
		var index = startIndex
		var routeNode = this
		while (index < route.size) {
			val (node, size) = routeNode.get(route, index, routeList)
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