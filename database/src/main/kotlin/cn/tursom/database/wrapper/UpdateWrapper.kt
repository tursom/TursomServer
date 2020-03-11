package cn.tursom.database.wrapper

import java.util.*

class UpdateWrapper<T> : AbstractWrapper<T, UpdateWrapper<T>>(), IUpdateWrapper<T>, Update<T, UpdateWrapper<T>> {
  private val setBuilder = StringBuilder()
  private val params = LinkedList<Any?>()

  override val sqlSet: Pair<String, Collection<Any?>>
    get() = setBuilder.toString() to params

  override fun String.set(value: T): UpdateWrapper<T> {
    if (setBuilder.isNotEmpty()) setBuilder.append(",")
    setBuilder.append("$this=?")
    params.add(value)
    return this@UpdateWrapper
  }

  override fun setSql(sql: String, values: Collection<Any?>): UpdateWrapper<T> {
    setBuilder.append(sql)
    params.addAll(values)
    return this
  }
}

