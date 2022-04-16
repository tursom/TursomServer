package cn.tursom.core.reflect

import cn.tursom.core.UncheckedCast
import cn.tursom.core.cast
import cn.tursom.core.forAllMethods
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.Continuation
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

@Suppress("unused", "MemberVisibilityCanBePrivate")
object MethodInspector {
  private val logger = LoggerFactory.getLogger(MethodInspector::class.java)

  fun <R> forEachMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType: Array<Class<*>>,
    self: Any? = null,
    handler: (Method, (Array<Any?>) -> R) -> Unit,
  ) {
    clazz.forAllMethods { method ->
      val parameterTypes = method.parameterTypes
      val methodReturnType = if (
        method.returnType == Void.TYPE ||
        method.returnType == Void::class.java
      ) Unit::class.java else method.returnType

      val argumentTypeIterator = argumentType.iterator()
      if (parameterTypes.size == argumentType.size &&
        parameterTypes.all {
          argumentTypeIterator.next().isAssignableFrom(it)
        } &&
        returnType.isAssignableFrom(methodReturnType)
      ) {
        method.isAccessible = true
        @OptIn(UncheckedCast::class)
        handler(method) { method.invoke(self, *it).cast() }
      }
    }
  }

  fun <R> forEachSuspendMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType: Array<Class<*>>,
    self: Any? = null,
    handler: (Method, suspend (Array<Any?>) -> R) -> Unit,
  ) {
    clazz.forAllMethods { method ->
      val parameterTypes = method.parameterTypes
      val kotlinFunction = method.kotlinFunction ?: return@forAllMethods
      var methodReturnType = kotlinFunction.returnType.javaType
      while (methodReturnType is ParameterizedType) {
        methodReturnType = methodReturnType.rawType
      }

      if (methodReturnType !is Class<*>) {
        logger.warn("method return type {}({}) is not Class", methodReturnType, methodReturnType.javaClass)
        return@forAllMethods
      }

      val argumentTypeIterator = argumentType.iterator()
      if (parameterTypes.size - 1 == argumentType.size &&
        parameterTypes.dropLast(1).all {
          argumentTypeIterator.next().isAssignableFrom(it)
        } &&
        Continuation::class.java.isAssignableFrom(parameterTypes.last()) &&
        returnType.isAssignableFrom(methodReturnType)
      ) {
        method.isAccessible = true
        @OptIn(UncheckedCast::class)
        handler(method, { it: Array<Any?>, cont: Continuation<*> -> method.invoke(self, *it, cont) }.cast())
      }
    }
  }

  fun forEachMethod(clazz: Class<*>, self: Any? = null, handler: (Method, () -> Unit) -> Unit) {
    forEachMethod(clazz, Unit::class.java, self, handler)
  }

  fun <R> forEachMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    self: Any? = null,
    handler: (Method, () -> R) -> Unit,
  ) {
    clazz.javaClass.forAllMethods { method ->
      val parameterTypes = method.parameterTypes
      val methodReturnType = if (
        method.returnType == Void.TYPE ||
        method.returnType == Void::class.java
      ) Unit::class.java else method.returnType
      if (parameterTypes.isEmpty() && returnType.isAssignableFrom(methodReturnType)) {
        method.isAccessible = true
        @OptIn(UncheckedCast::class)
        handler(method) { method.invoke(self).cast() }
      }
    }
  }

  fun <T, R> forEachMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType: Class<T>,
    self: Any? = null,
    handler: (Method, (T) -> R) -> Unit,
  ) {
    forEachMethod(clazz, returnType, arrayOf(argumentType), self) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) {
        m(arrayOf(it))
      }
    }
  }

  fun <T1, T2, R> forEachMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    self: Any? = null,
    handler: (Method, (T1, T2) -> R) -> Unit,
  ) {
    forEachMethod(
      clazz,
      returnType,
      arrayOf(argumentType1, argumentType2),
      self
    ) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) { t1, t2 ->
        m(arrayOf(t1, t2))
      }
    }
  }

  fun <T1, T2, T3, R> forEachMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    self: Any? = null,
    handler: (Method, (T1, T2, T3) -> R) -> Unit,
  ) {
    forEachMethod(
      clazz,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3),
      self
    ) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3 ->
        m(arrayOf(t1, t2, t3))
      }
    }
  }

  fun <T1, T2, T3, T4, R> forEachMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    argumentType4: Class<T4>,
    self: Any? = null,
    handler: (Method, (T1, T2, T3, T4) -> R) -> Unit,
  ) {
    forEachMethod(
      clazz,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3, argumentType4),
      self
    ) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3, t4 ->
        m(arrayOf(t1, t2, t3, t4))
      }
    }
  }

  fun forEachSuspendMethod(clazz: Class<*>, self: Any? = null, handler: (Method, suspend () -> Unit) -> Unit) {
    forEachSuspendMethod(clazz, Unit::class.java, self, handler)
  }

  fun <R> forEachSuspendMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    self: Any? = null,
    handler: (Method, suspend () -> R) -> Unit,
  ) {
    forEachSuspendMethod(clazz, returnType, arrayOf(), self) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) {
        m(arrayOf())
      }
    }
  }

  fun <T, R> forEachSuspendMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType: Class<T>,
    self: Any? = null,
    handler: (Method, suspend (T) -> R) -> Unit,
  ) {
    forEachSuspendMethod(
      clazz,
      returnType,
      arrayOf(argumentType),
      self
    ) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) {
        m(arrayOf(it))
      }
    }
  }

  fun <T1, T2, R> forEachSuspendMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    self: Any? = null,
    handler: (Method, suspend (T1, T2) -> R) -> Unit,
  ) {
    forEachSuspendMethod(
      clazz,
      returnType,
      arrayOf(argumentType1, argumentType2),
      self
    ) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) { t1, t2 ->
        m(arrayOf(t1, t2))
      }
    }
  }

  fun <T1, T2, T3, R> forEachSuspendMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    self: Any? = null,
    handler: (Method, suspend (T1, T2, T3) -> R) -> Unit,
  ) {
    forEachSuspendMethod(
      clazz,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3),
      self
    ) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3 ->
        m(arrayOf(t1, t2, t3))
      }
    }
  }

  fun <T1, T2, T3, T4, R> forEachSuspendMethod(
    clazz: Class<*>,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    argumentType4: Class<T4>,
    self: Any? = null,
    handler: (Method, suspend (T1, T2, T3, T4) -> R) -> Unit,
  ) {
    forEachSuspendMethod(
      clazz,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3, argumentType4),
      self
    ) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3, t4 ->
        m(arrayOf(t1, t2, t3, t4))
      }
    }
  }


  fun <R> forEachMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType: Array<Class<*>>,
    handler: (Method, (Array<Any?>) -> R) -> Unit,
  ) {
    forEachMethod(self.javaClass, returnType, argumentType, self, handler)
  }

  fun <R> forEachSuspendMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType: Array<Class<*>>,
    handler: (Method, suspend (Array<Any?>) -> R) -> Unit,
  ) {
    forEachSuspendMethod(self.javaClass, returnType, argumentType, self, handler)
  }

  fun forEachMethod(self: Any, handler: (Method, () -> Unit) -> Unit) {
    forEachMethod(self, Unit::class.java, handler)
  }

  fun <R> forEachMethod(self: Any, returnType: Class<out R>, handler: (Method, () -> R) -> Unit) {
    forEachMethod(self.javaClass, returnType, self, handler)
  }

  fun <T, R> forEachMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType: Class<T>,
    handler: (Method, (T) -> R) -> Unit,
  ) {
    forEachMethod(self, returnType, arrayOf(argumentType)) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) {
        m(arrayOf(it))
      }
    }
  }

  fun <T1, T2, R> forEachMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    handler: (Method, (T1, T2) -> R) -> Unit,
  ) {
    forEachMethod(
      self,
      returnType,
      arrayOf(argumentType1, argumentType2)
    ) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) { t1, t2 ->
        m(arrayOf(t1, t2))
      }
    }
  }

  fun <T1, T2, T3, R> forEachMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    handler: (Method, (T1, T2, T3) -> R) -> Unit,
  ) {
    forEachMethod(
      self,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3)
    ) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3 ->
        m(arrayOf(t1, t2, t3))
      }
    }
  }

  fun <T1, T2, T3, T4, R> forEachMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    argumentType4: Class<T4>,
    handler: (Method, (T1, T2, T3, T4) -> R) -> Unit,
  ) {
    forEachMethod(
      self,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3, argumentType4)
    ) { method: Method, m: (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3, t4 ->
        m(arrayOf(t1, t2, t3, t4))
      }
    }
  }

  fun forEachSuspendMethod(self: Any, handler: (Method, suspend () -> Unit) -> Unit) {
    forEachSuspendMethod(self, Unit::class.java, handler)
  }

  fun <R> forEachSuspendMethod(self: Any, returnType: Class<out R>, handler: (Method, suspend () -> R) -> Unit) {
    forEachSuspendMethod(self, returnType, arrayOf()) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) {
        m(arrayOf())
      }
    }
  }

  fun <T, R> forEachSuspendMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType: Class<T>,
    handler: (Method, suspend (T) -> R) -> Unit,
  ) {
    forEachSuspendMethod(self, returnType, arrayOf(argumentType)) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) {
        m(arrayOf(it))
      }
    }
  }

  fun <T1, T2, R> forEachSuspendMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    handler: (Method, suspend (T1, T2) -> R) -> Unit,
  ) {
    forEachSuspendMethod(
      self,
      returnType,
      arrayOf(argumentType1, argumentType2)
    ) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) { t1, t2 ->
        m(arrayOf(t1, t2))
      }
    }
  }

  fun <T1, T2, T3, R> forEachSuspendMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    handler: (Method, suspend (T1, T2, T3) -> R) -> Unit,
  ) {
    forEachSuspendMethod(
      self,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3)
    ) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3 ->
        m(arrayOf(t1, t2, t3))
      }
    }
  }

  fun <T1, T2, T3, T4, R> forEachSuspendMethod(
    self: Any,
    returnType: Class<out R>,
    argumentType1: Class<T1>,
    argumentType2: Class<T2>,
    argumentType3: Class<T3>,
    argumentType4: Class<T4>,
    handler: (Method, suspend (T1, T2, T3, T4) -> R) -> Unit,
  ) {
    forEachSuspendMethod(
      self,
      returnType,
      arrayOf(argumentType1, argumentType2, argumentType3, argumentType4)
    ) { method: Method, m: suspend (Array<Any?>) -> R ->
      handler(method) { t1, t2, t3, t4 ->
        m(arrayOf(t1, t2, t3, t4))
      }
    }
  }
}