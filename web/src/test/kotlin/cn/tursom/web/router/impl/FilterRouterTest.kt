package cn.tursom.web.router.impl

fun main() {
  val router = FilterRouter<String>('(' to ']')
  router.addSubRoute("/123/(aa]/123", "1")
  router.addSubRoute("/123/(bb]//////////////////////(cc]/123", "2")
  println(router["/123/aaa/123"])
  println(router["/123/bbb/123/123"])
  println(router["/123/bbb/ccc/123"])
  println(router["/123/bbb/ccc//////123////"])
}