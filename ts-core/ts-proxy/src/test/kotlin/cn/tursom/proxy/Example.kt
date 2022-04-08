package cn.tursom.proxy

import org.junit.Test

class Example {
  open class TestClass protected constructor() {
    //@get:ForEachProxy
    open var a: Int = 0
  }

  class GetA(
    private val t: TestClass,
  ) : ProxyMethod {
    fun getA(): Int {
      ProxyInterceptor.callSuper.set(true)
      return t.a + 1
    }
  }


  @Test
  fun test() {
    val (t, container) = Proxy.get<TestClass>()
    container.addProxy(GetA(t))

    println(t.javaClass)
    repeat(1000000000) {
      t.a = t.a
      //println(t.a)
    }
  }
}