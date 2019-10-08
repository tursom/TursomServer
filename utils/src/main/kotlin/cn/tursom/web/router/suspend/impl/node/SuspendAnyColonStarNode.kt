package cn.tursom.web.router.suspend.impl.node

import java.util.AbstractList

internal class SuspendAnyColonStarNode<T>(
	route: List<String>,
	index: Int,
	value: T? = null
) : SuspendColonStarNode<T>(route, index, value) {
	private val subNode = this to 1
	override fun match(route: List<String>, startIndex: Int) = true to 1
	override suspend fun get(route: List<String>, startIndex: Int, routeList: AbstractList<Pair<String, String>>) = subNode
}