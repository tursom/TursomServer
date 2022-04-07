package cn.tursom.reflect

import cn.tursom.core.allMethodsSequence
import java.lang.reflect.Method

class ReturnTypeMethodFilter<R>(
  val returnType: Class<R>,
) : MethodFilter {
  override fun filterMethod(clazz: Class<*>): Sequence<Method> {
    return clazz.allMethodsSequence.filter { method ->
      val methodReturnType = if (
        method.returnType == Void.TYPE ||
        method.returnType == Void::class.java
      ) Unit::class.java else method.returnType
      returnType.isAssignableFrom(methodReturnType)
    }
  }

  companion object
}

inline fun <reified R> MethodFilter.Companion.returnType() = ReturnTypeMethodFilter(R::class.java)
