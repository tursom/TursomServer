package cn.tursom.web.router.impl.colonnode

class AnyColonNode<T>(
	route: List<String>,
	index: Int,
	value: T? = null
) : ColonNode<T>(route, index, value) {
	override fun match(route: List<String>, startIndex: Int) = true to 1
}