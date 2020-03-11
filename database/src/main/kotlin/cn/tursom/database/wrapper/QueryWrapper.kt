package cn.tursom.database.wrapper

class QueryWrapper<T> : AbstractWrapper<T, QueryWrapper<T>>(), Query<T, QueryWrapper<T>> {
  private val selectBuilder = StringBuilder()
  override val sqlSelect: String get() = selectBuilder.dropLast(1).toString()

  override fun select(columns: Collection<String>): QueryWrapper<T> {
    columns.forEach {
      selectBuilder.append(it)
      selectBuilder.append(',')
    }
    return this
  }
}