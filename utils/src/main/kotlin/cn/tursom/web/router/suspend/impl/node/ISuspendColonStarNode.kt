package cn.tursom.web.router.suspend.impl.node

interface ISuspendColonStarNode<T> {
	val value: T?
	val lastRoute: String
	val fullRoute: String
	val empty: Boolean
	
	suspend fun forEach(action: suspend (node: ISuspendColonStarNode<T>) -> Unit)
}