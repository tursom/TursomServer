package cn.tursom.database.wrapper

interface IUpdateWrapper<T> : Wrapper<T> {
  val sqlSet: Pair<String, Collection<Any?>>
}