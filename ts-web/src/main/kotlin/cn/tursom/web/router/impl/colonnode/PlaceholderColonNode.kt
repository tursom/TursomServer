package cn.tursom.web.router.impl.colonnode

class PlaceholderColonNode<T>(
  route: List<String>,
  private val startIndex: Int = 0,
  endIndex: Int = startIndex + route.matchLength(startIndex),
  value: T? = null,
) : ColonNode<T>(route, endIndex - 1, value) {
  override val placeholderRouterList: ArrayList<PlaceholderColonNode<T>>?
    get() = null

  val size: Int = route.matchLength(startIndex, endIndex)

  override val singleRoute: String
    get() {
      val sb = StringBuilder()
      for (i in startIndex..index) {
        sb.append("/")
        sb.append(routeList[i])
      }
      return sb.toString()
    }

  override fun match(
    route: List<String>,
    startIndex: Int,
  ): Pair<Boolean, Int> =
    (size == route.matchLength(startIndex)) to size

  companion object {
    @JvmStatic
    private fun List<String>.matchLength(startIndex: Int, endIndex: Int = size): Int {
      var length = 0
      for (i in startIndex until endIndex) {
        if (this[i].isEmpty()) continue
        else if (this[i][0] != ':') return length
        else length++
      }
      return length
    }

    @JvmStatic
    fun matchLength(route: List<String>, startIndex: Int): Int {
      var length = 0
      for (i in startIndex until route.size) {
        if (route[i].isEmpty()) continue
        else if (route[i][0] != ':') return length
        else length++
      }
      return length
    }
  }
}