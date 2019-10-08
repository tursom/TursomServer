package cn.tursom.web.router.suspend

interface ISuspendRouter<T> {
	suspend fun setSubRoute(route: String, value: T?, onDestroy: ((oldValue: T) -> Unit)? = null)
	suspend fun delRoute(route: String, onDestroy: ((oldValue: T) -> Unit)? = null)

	suspend fun set(
		route: String,
		onDestroy: ((oldValue: T) -> Unit)? = null,
		value: T?
	) = setSubRoute(route, value, onDestroy)

	suspend fun get(route: String): Pair<T?, List<Pair<String, String>>>
}