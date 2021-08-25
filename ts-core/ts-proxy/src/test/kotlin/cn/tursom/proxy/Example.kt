package cn.tursom.proxy

import cn.tursom.proxy.Example.GetA
import cn.tursom.proxy.annotation.ForEachProxy
import net.sf.cglib.proxy.Enhancer
import org.junit.Test

class Example {
  open class TestClass protected constructor() : ProxyContainer<ProxyMethod> {
    @get:ForEachProxy
    open val a: Int = 0
    override val proxy = ListProxy<ProxyMethod>()
  }

  fun interface GetA : ProxyMethod {
    fun getA(): Int
  }

  private val enhancer = Enhancer()

  init {
    enhancer.setSuperclass(TestClass::class.java)
    enhancer.setCallback(ProxyInterceptor())
  }

  @Test
  fun test() {
    val testClass = enhancer.create() as TestClass
    testClass.proxy.addProxy(GetA {
      println("on proxy method")
      0
    })

    println(testClass.javaClass)
    println(testClass.a)
  }
}