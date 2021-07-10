package cn.tursom.web.router.impl.colonnode

interface IColonNode<T> {
  val value: T?

  fun forEach(action: (node: IColonNode<T>) -> Unit)
}