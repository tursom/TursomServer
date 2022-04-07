package cn.tursom.proxy

import cn.tursom.proxy.Example.GetA
import cn.tursom.proxy.annotation.ForEachProxy
import org.junit.Test

class Example {
  open class TestClass protected constructor() {
    @get:ForEachProxy
    open val a: Int = 0
  }

  fun interface GetA : ProxyMethod {
    fun getA(): Int
  }


  @Test
  fun test() {
    repeat(3) {
      val (t, container) = Proxy.get<TestClass>()
      container.addProxy(GetA {
        println("on proxy method")
        0
      })

      println(t.javaClass)
      println(t.a)
    }
  }
}