package cn.tursom.reflect.asm

import com.esotericsoftware.reflectasm.FieldAccess
import java.lang.reflect.Field

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ReflectAsmField(
  val fieldAccess: FieldAccess,
  val index: Int,
) {
  val name: String get() = fieldAccess.fieldNames[index]
  val type: Class<*> get() = fieldAccess.fieldTypes[index]
  val field: Field get() = fieldAccess.fields[index]
}

