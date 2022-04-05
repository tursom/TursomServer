package cn.tursom.core.context

data class ContextKey<T>(
  val envId: Int,
  val id: Int,
) {
  override fun hashCode(): Int {
    return id
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ContextKey<*>) return false

    if (envId != other.envId) return false
    if (id != other.id) return false

    return true
  }
}
