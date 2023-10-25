package cn.tursom.core.util

import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.jvm.internal.PropertyReference
import kotlin.reflect.KProperty

object ReflectUtil {
  val receiverField: Field by lazy {
    kotlin.jvm.internal.CallableReference::class.java.getDeclaredField("receiver").apply { isAccessible = true }
  }
  val ownerField: Field by lazy {
    kotlin.jvm.internal.CallableReference::class.java.getDeclaredField("owner").apply { isAccessible = true }
  }
}

inline fun Class<*>.forAllFields(action: (Field) -> Unit) {
  allFieldsSequence.forEach(action)
}

val Class<*>.allFields: List<Field>
  get() {
    val fieldList = ArrayList<Field>()
    forAllFields(fieldList::add)
    return fieldList
  }

val Class<*>.allFieldsSequence: Sequence<Field>
  get() = sequence {
    var clazz = this@allFieldsSequence
    while (clazz != Any::class.java) {
      clazz.declaredFields.forEach { field ->
        yield(field)
      }
      clazz = clazz.superclass
    }
  }

fun Class<*>.getFieldForAll(name: String): Field? {
  forAllFields {
    if (it.name == name) return it
  }
  return null
}

inline fun Class<*>.forAllMethods(action: (Method) -> Unit) {
  allMethodsSequence.forEach(action)
}

fun Class<*>.getMethodForAll(name: String, vararg parameterTypes: Class<*>?): Method? {
  forAllMethods {
    if (it.name == name && parameterTypes.contentEquals(it.parameterTypes)) return it
  }
  return null
}

val Class<*>.allMethods: List<Method>
  get() {
    val fieldList = ArrayList<Method>()
    forAllMethods(fieldList::add)
    return fieldList
  }

val Class<*>.allMethodsSequence: Sequence<Method>
  get() = sequence {
    var clazz = this@allMethodsSequence
    while (clazz != Any::class.java) {
      clazz.declaredMethods.forEach {
        yield(it)
      }
      clazz = clazz.superclass
    }
    clazz.declaredMethods.forEach {
      yield(it)
    }
  }

/**
 * 获取一个 KProperty<*> 对应的对象
 */
val KProperty<*>.receiver: Any?
  get() = if (this is PropertyReference) {
    boundReceiver
  } else try {
    ReflectUtil.receiverField.get(this)
  } catch (e: Exception) {
    null
  } ?: javaClass.getFieldForAll("receiver")?.let {
    it.isAccessible = true
    it.get(this)
  }

val KProperty<*>.owner: Class<*>?
  get() = try {
    ReflectUtil.ownerField.get(this)?.uncheckedCast<Class<*>>()
  } catch (e: Exception) {
    null
  } ?: javaClass.getFieldForAll("owner")?.let {
    it.isAccessible = true
    it.get(this)?.castOrNull()
  }
