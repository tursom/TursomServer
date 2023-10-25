package cn.tursom.proxy

import cn.tursom.core.util.allFieldsSequence
import cn.tursom.core.util.static
import cn.tursom.proxy.function.ProxyMethod
import com.esotericsoftware.reflectasm.MethodAccess
import net.sf.cglib.proxy.InvocationHandler
import net.sf.cglib.proxy.MethodProxy
import org.junit.Test
import org.objectweb.asm.ClassWriter
import java.io.File

class Example {
  companion object {
    var bytes: ByteArray? = null
  }

  open class TestClass protected constructor() {
    open var a: Int? = 0
  }

  class GetA(
    t: TestClass,
  ) : ProxyMethod {
    val t: TestClass = Proxy.getSuperCaller(t)

    val a get() = (t.a ?: 0) + 1
  }

  @Test
  fun getClass() {
    val access = MethodAccess.get(TestClass::class.java)
    println(access)

    val writer = ClassWriter(0)
    val enhancer = Proxy.newEnhancer(TestClass::class.java)

    enhancer.setCallbackType(InvocationHandler::class.java)
    enhancer.setCallbackFilter { 0 }

    val clazz = enhancer.createClass()
    //CglibUtil.setStaticCallbacks(clazz, arrayOf(ProxyInterceptor()))
    //val instance = clazz.newInstance()
    enhancer.generateClass(writer)
    File("TestClass_InvocationHandler.class").writeBytes(writer.toByteArray())
    clazz.allFieldsSequence.forEach {
      if (!it.static) return@forEach

      it.isAccessible = true
      println("${it.name} = ${it[null]}")

      if (it.type == MethodProxy::class.java) {
        val methodProxy = it[null] as MethodProxy
        println(methodProxy.signature)
        println(methodProxy.superName)
        println(methodProxy.superIndex)
      }
    }
  }

  @Test
  fun getMethodAccessClass() {
    val (t, container) = Proxy.get<TestClass>()

    MethodAccess.get(t.javaClass)!!
    //File("TestClass_MethodAccess.class").writeBytes(bytes!!)
  }

  @Test
  fun test() {
    val (t, container) = Proxy.get<TestClass>()
    val getA = GetA(t)
    println(getA.t == t)

    container.addProxy(getA)

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
    for (l in 0..10_000_000_000L) {
      t.a = t.a
    }
  }

  interface IntContainer {
    var i: Int?
  }

  class IntContainerImpl(override var i: Int?) : IntContainer

  class IntProxy(
    private val c: IntContainer,
  ) : IntContainer {
    override var i: Int?
      get() = (c.i ?: 0) + 1
      set(value) {
        c.i = value
      }
  }

  private val a: IntContainerImpl = IntContainerImpl(0)

  @Test
  fun benchmarkOrigin() {
    val p: IntContainer = IntProxy(a)

    for (l in 0..10000000000L) {
      p.i = p.i
    }
  }
}