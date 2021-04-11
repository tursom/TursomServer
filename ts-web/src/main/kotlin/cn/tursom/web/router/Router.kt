package cn.tursom.web.router

interface Router<T> {
	fun addSubRoute(route: String, value: T?, onDestroy: ((oldValue: T) -> Unit)? = null)
	fun delRoute(route: String)

	operator fun set(
		route: String,
		onDestroy: ((oldValue: T) -> Unit)? = null,
		value: T?
	) = addSubRoute(route, value, onDestroy)

	operator fun get(route: String): Pair<T?, List<Pair<String, String>>>
}