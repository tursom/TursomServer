package cn.tursom.reflect.asm

import cn.tursom.reflect.canCast
import cn.tursom.reflect.match
import com.esotericsoftware.reflectasm.FieldAccess
import com.esotericsoftware.reflectasm.MethodAccess
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
object ReflectAsmUtils {
  private val fieldAccessMap = ConcurrentHashMap<Class<*>, List<FieldAccess>>()
  private val methodAccessMap = ConcurrentHashMap<Class<*>, List<MethodAccess>>()

  val Class<*>.fieldAccessList: List<FieldAccess>
    get() = fieldAccessMap[this] ?: run {
      var analyzeFieldAccessClass = this
      val fieldAccessList = ArrayList<FieldAccess>()
      while (analyzeFieldAccessClass != Any::class.java) {
        try {
          fieldAccessList.add(FieldAccess.get(analyzeFieldAccessClass)!!)
          analyzeFieldAccessClass = analyzeFieldAccessClass.superclass
        } catch (_: Exception) {
        }
      }
      fieldAccessMap[this] = fieldAccessList
      fieldAccessList
    }

  fun getField(clazz: Class<*>, fieldName: String, type: Class<*> = Any::class.java): Pair<FieldAccess, Int>? {
    clazz.fieldAccessList.forEach { fieldAccess ->
      try {
        val index = fieldAccess.getIndex(fieldName)
        if (fieldAccess.fieldTypes[index] canCast type) {
          return fieldAccess to index
        }
      } catch (_: Exception) {
      }
    }
    return null
  }

  inline fun <reified T, reified R> getField(fieldName: String): Pair<FieldAccess, Int>? =
    getField(T::class.java, fieldName, R::class.java)

  fun getReflectAsmField(clazz: Class<*>, fieldName: String, type: Class<*> = Any::class.java): ReflectAsmField? {
    val (fieldAccess, index) = getField(clazz, fieldName, type) ?: return null
    return ReflectAsmField(fieldAccess, index)
  }

  inline fun <reified T, reified R> getReflectAsmField(fieldName: String): ReflectAsmField? =
    getReflectAsmField(T::class.java, fieldName, R::class.java)

  val Class<*>.methodAccessList: List<MethodAccess>
    get() = methodAccessMap[this] ?: run {
      var analyzeFieldAccessClass = this
      val fieldAccessList = ArrayList<MethodAccess>()
      while (analyzeFieldAccessClass != Any::class.java) {
        try {
          fieldAccessList.add(MethodAccess.get(analyzeFieldAccessClass)!!)
          analyzeFieldAccessClass = analyzeFieldAccessClass.superclass
        } catch (_: Exception) {
        }
      }
      fieldAccessList.add(MethodAccess.get(analyzeFieldAccessClass)!!)
      methodAccessMap[this] = fieldAccessList
      fieldAccessList
    }

  fun getMethod(
    clazz: Class<*>,
    methodName: String,
    vararg paramTypes: Class<*>,
    returnType: Class<*> = Any::class.java,
  ): Pair<MethodAccess, Int>? {
    clazz.methodAccessList.forEach { methodAccess ->
      repeat(methodAccess.methodNames.size) { i ->
        if (methodAccess.methodNames[i] == methodName && methodAccess.parameterTypes[i] match paramTypes &&
          methodAccess.returnTypes[i] canCast returnType
        ) {
          return methodAccess to i
        }
      }
    }
    return null
  }

  fun getMethodSequence(
    clazz: Class<*>,
    methodName: String,
    vararg paramTypes: Class<*>,
    returnType: Class<*> = Any::class.java,
  ) = sequence {
    clazz.methodAccessList.forEach { methodAccess ->
      repeat(methodAccess.methodNames.size) { i ->
        if (methodAccess.methodNames[i] == methodName && methodAccess.parameterTypes[i] match paramTypes &&
          methodAccess.returnTypes[i] canCast returnType
        ) {
          yield(methodAccess to i)
        }
      }
    }
  }

  fun getMethodByRegex(
    clazz: Class<*>,
    methodName: Regex,
    vararg paramTypes: Class<*>,
    returnType: Class<*> = Any::class.java,
  ): Pair<MethodAccess, Int>? {
    clazz.methodAccessList.forEach { methodAccess ->
      repeat(methodAccess.methodNames.size) { i ->
        if (methodName.matches(methodAccess.methodNames[i]) && methodAccess.parameterTypes[i] match paramTypes &&
          methodAccess.returnTypes[i] canCast returnType
        ) {
          return methodAccess to i
        }
      }
    }
    return null
  }

  fun getMethod(method: Method): Pair<MethodAccess, Int>? = getMethod(
    method.declaringClass,
    method.name,
    paramTypes = method.parameterTypes,
    returnType = method.returnType,
  )

  inline fun <reified T, reified R> getMethod0(
    methodName: String,
    type: Class<T> = T::class.java,
    returnType: Class<R> = R::class.java,
  ): (T.() -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, returnType = returnType)
      ?: return null
    return {
      methodAccess.invoke(this, index) as R
    }
  }

  inline fun <reified T, reified A1, reified R> getMethod1(
    methodName: String,
    type: Class<T> = T::class.java,
    ta1: Class<A1> = A1::class.java,
    returnType: Class<R> = R::class.java,
  ): (T.(A1) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, returnType = returnType)
      ?: return null
    return { a1 ->
      methodAccess.invoke(this, index, a1) as R
    }
  }

  inline fun <reified T, reified A1, reified A2, reified R> getMethod2(
    methodName: String,
    type: Class<T> = T::class.java,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    returnType: Class<R> = R::class.java,
  ): (T.(A1, A2) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, returnType = returnType)
      ?: return null
    return { a1, a2 ->
      methodAccess.invoke(this, index, a1, a2) as R
    }
  }

  inline fun <reified T, reified A1, reified A2, reified A3, reified R> getMethod3(
    methodName: String,
    type: Class<T> = T::class.java,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    returnType: Class<R> = R::class.java,
  ): (T.(A1, A2, A3) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, returnType = returnType)
      ?: return null
    return { a1, a2, a3 ->
      methodAccess.invoke(this, index, a1, a2, a3) as R
    }
  }

  inline fun <reified T, reified A1, reified A2, reified A3, reified A4, reified R>
    getMethod4(
    methodName: String,
    type: Class<T> = T::class.java,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    ta4: Class<A4> = A4::class.java,
    returnType: Class<R> = R::class.java,
  ): (T.(A1, A2, A3, A4) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, ta4, returnType = returnType)
      ?: return null
    return { a1, a2, a3, a4 ->
      methodAccess.invoke(this, index, a1, a2, a3, a4) as R
    }
  }

  inline fun <reified T, reified A1, reified A2, reified A3, reified A4, reified A5, reified R>
    getMethod5(
    methodName: String,
    type: Class<T> = T::class.java,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    ta4: Class<A4> = A4::class.java,
    ta5: Class<A5> = A5::class.java,
    returnType: Class<R> = R::class.java,
  ): (T.(A1, A2, A3, A4, A5) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, ta4, ta5, returnType = returnType)
      ?: return null
    return { a1, a2, a3, a4, a5 ->
      methodAccess.invoke(this, index, a1, a2, a3, a4, a5) as R
    }
  }

  inline fun <reified T, reified A1, reified A2, reified A3, reified A4, reified A5, reified A6, reified R>
    getMethod6(
    methodName: String,
    type: Class<T> = T::class.java,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    ta4: Class<A4> = A4::class.java,
    ta5: Class<A5> = A5::class.java,
    ta6: Class<A6> = A6::class.java,
    returnType: Class<R> = R::class.java,
  ): (T.(A1, A2, A3, A4, A5, A6) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, ta4, ta5, ta6, returnType = returnType)
      ?: return null
    return { a1, a2, a3, a4, a5, a6 ->
      methodAccess.invoke(this, index, a1, a2, a3, a4, a5, a6) as R
    }
  }

  inline fun <reified R> getStaticMethod0(
    type: Class<*>,
    methodName: String,
    returnType: Class<R> = R::class.java,
  ): (() -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, returnType = returnType)
      ?: return null
    return {
      methodAccess.invoke(null, index) as R
    }
  }

  inline fun <reified A1, reified R> getStaticMethod1(
    type: Class<*>,
    methodName: String,
    ta1: Class<A1> = A1::class.java,
    returnType: Class<R> = R::class.java,
  ): ((A1) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, returnType = returnType)
      ?: return null
    return { a1 ->
      methodAccess.invoke(null, index, a1) as R
    }
  }

  inline fun <reified A1, reified A2, reified R> getStaticMethod2(
    type: Class<*>,
    methodName: String,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    returnType: Class<R> = R::class.java,
  ): ((A1, A2) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, returnType = returnType)
      ?: return null
    return { a1, a2 ->
      methodAccess.invoke(null, index, a1, a2) as R
    }
  }

  inline fun <reified A1, reified A2, reified A3, reified R> getStaticMethod3(
    type: Class<*>,
    methodName: String,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    returnType: Class<R> = R::class.java,
  ): ((A1, A2, A3) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, returnType = returnType)
      ?: return null
    return { a1, a2, a3 ->
      methodAccess.invoke(null, index, a1, a2, a3) as R
    }
  }

  inline fun <reified A1, reified A2, reified A3, reified A4, reified R> getStaticMethod4(
    type: Class<*>,
    methodName: String,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    ta4: Class<A4> = A4::class.java,
    returnType: Class<R> = R::class.java,
  ): ((A1, A2, A3, A4) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, ta4, returnType = returnType)
      ?: return null
    return { a1, a2, a3, a4 ->
      methodAccess.invoke(null, index, a1, a2, a3, a4) as R
    }
  }

  inline fun <reified A1, reified A2, reified A3, reified A4, reified A5, reified R> getStaticMethod5(
    type: Class<*>,
    methodName: String,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    ta4: Class<A4> = A4::class.java,
    ta5: Class<A5> = A5::class.java,
    returnType: Class<R> = R::class.java,
  ): ((A1, A2, A3, A4, A5) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, ta4, ta5, returnType = returnType)
      ?: return null
    return { a1, a2, a3, a4, a5 ->
      methodAccess.invoke(null, index, a1, a2, a3, a4, a5) as R
    }
  }

  inline fun <reified A1, reified A2, reified A3, reified A4, reified A5, reified A6, reified R> getStaticMethod6(
    type: Class<*>,
    methodName: String,
    ta1: Class<A1> = A1::class.java,
    ta2: Class<A2> = A2::class.java,
    ta3: Class<A3> = A3::class.java,
    ta4: Class<A4> = A4::class.java,
    ta5: Class<A5> = A5::class.java,
    ta6: Class<A6> = A6::class.java,
    returnType: Class<R> = R::class.java,
  ): ((A1, A2, A3, A4, A5, A6) -> R)? {
    val (methodAccess, index) = getMethod(type, methodName, ta1, ta2, ta3, ta4, ta5, ta6, returnType = returnType)
      ?: return null
    return { a1, a2, a3, a4, a5, a6 ->
      methodAccess.invoke(null, index, a1, a2, a3, a4, a5, a6) as R
    }
  }
}
