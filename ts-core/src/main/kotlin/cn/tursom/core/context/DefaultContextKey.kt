package cn.tursom.core.context

class DefaultContextKey<T>(envId: Int, id: Int, val provider: () -> T) : ContextKey<T>(envId, id)
