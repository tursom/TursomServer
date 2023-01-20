package cn.tursom.core.delegation.observer

import cn.tursom.core.delegation.expirable
import cn.tursom.core.delegation.filter
import cn.tursom.core.delegation.notNull
import java.lang.Thread.sleep

class A {
  @OptIn(Listenable::class)
  var field by listenable(0)
    .filter { _, new ->
      new >= 0
    }
    .expirable(100)
    .notNull {
      throw IllegalAccessError()
    }

  @OptIn(Listenable::class)
  var canServer by listenable(true)

  @OptIn(Listenable::class)
  var count: Int by listenable(0)
}

fun f(obj: A) {
  obj::field.listen { old: Int?, new: Int? ->
    println("old: $old, new: $new")
  }

  obj::canServer.listen { old, new ->
    println(new)
  }

  obj::count.listen { old, new ->

  }
}

fun main() {
  val a = A()
  f(a)

  a.field = 1
  println(a.field)

  sleep(1000)
  println(a.field)
}
