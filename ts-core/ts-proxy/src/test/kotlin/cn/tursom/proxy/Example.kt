package cn.tursom.proxy

import org.junit.Test
import org.objectweb.asm.ClassWriter
import java.io.File

class Example {
  open class TestClass protected constructor() {
    //@get:ForEachProxy
    open var a: Int = 0
  }

  class GetA(
    private val t: TestClass,
  ) : ProxyMethod {
    fun getA(): Int {
      Proxy.callSuper.set(true)
      return t.a + 1
    }
  }

  @Test
  fun getClass() {
    val writer = ClassWriter(0)
    val enhancer = Proxy.newEnhancer(TestClass::class.java)
    val clazz = enhancer.createClass()
    CglibUtil.setStaticCallbacks(clazz, arrayOf(ProxyInterceptor()))
    val instance = clazz.newInstance()
    enhancer.generateClass(writer)
    File("TestClass.class").writeBytes(writer.toByteArray())
  }

  @Test
  fun test() {
    //val enhancer = Enhancer()
    //enhancer.setSuperclass(TestClass::class.java)
    //enhancer.setCallback(ProxyDispatcher)
    //val t = enhancer.create() as TestClass
    //val enhancer = Enhancer()
    //enhancer.setSuperclass(TestClass::class.java)
    //enhancer.setCallback(ProxyRefDispatcher {
    //  println("on Dispatcher")
    //  ProxyInterceptor()
    //})
    //
    //val t = enhancer.create() as TestClass
    //val writer = ClassWriter(0)
    //enhancer.generateClass(writer)
    //File("TestClass_ProxyRefDispatcher.class").writeBytes(writer.toByteArray())
    val (t, container) = Proxy.get<TestClass>()
    //val t = Proxy.getCachedTarget(TestClass::class.java).newInstance()
    //val container = Proxy.injectCallback(t)
    //container as MutableProxyContainer
    container.addProxy(GetA(t))

    println(t.javaClass)
    repeat(10) {
      t.a = t.a
      println(t.a)
    }
  }

  @Test
  fun benchmark() {
    val (t, container) = Proxy.get<TestClass>()
    container.addProxy(GetA(t))

    println(t.javaClass)
    repeat(1000000000) {
      t.a = t.a
      //println(t.a)
    }
  }
}