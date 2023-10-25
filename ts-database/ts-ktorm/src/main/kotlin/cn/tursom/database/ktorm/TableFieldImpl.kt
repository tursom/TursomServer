package cn.tursom.database.ktorm

import cn.tursom.core.util.uncheckedCast
import cn.tursom.database.ktorm.annotations.KtormTableField
import cn.tursom.log.impl.Slf4jImpl
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmName

/**
 * 自动注入表单名称映射
 * @author tursom
 * 实现使用 类名[属性对象] 的方式来获取数据库属性名
 * 同时为 KProperty<*>.tableField 实现缓存加速
 */
open class TableFieldImpl<T>(clazz: KClass<*>? = null) : TableField<T> {
  companion object : Slf4jImpl()

  final override var tableName: String

  @Transient
  final override val fieldMap = HashMap<KProperty1<out T, *>, String>()
  final override val simpFieldMap = HashMap<KProperty1<out T, *>, String>()
  final override val fullFieldMap = HashMap<KProperty1<out T, *>, String>()

  @Transient
  final override val allField: Array<out String>
  final override val fullNameField: Array<out String>
  final override val properties: Array<KProperty1<T, *>>

  init {
    if (clazz == null && this.javaClass == TableFieldImpl::class.java) {
      throw NotImplementedError("需提供解析类")
    }
    @Suppress("UNCHECKED_CAST")
    (clazz ?: this.javaClass.kotlin.run {
      if (isCompanion) {
        java.classLoader.loadClass(jvmName.dropLast(10)).kotlin
      } else {
        this
      }
    }).also {
      tableName = it.tableName
    }.memberProperties
      .uncheckedCast<Collection<KProperty1<out T, *>>>()
      .filter {
        it.javaField != null &&
          (it.findAnnotation() ?: it.javaField?.getAnnotation(Transient::class.java)) == null &&
          !Modifier.isTransient(it.javaField?.modifiers ?: Modifier.TRANSIENT) &&
          it.javaField?.getAnnotation(KtormTableField::class.java)?.exist != false
      }
      .forEach {
        val simpTableField = it.simpTableField
        trace("mapping {}::{}", tableName, simpTableField)
        simpFieldMap[it] = simpTableField
        fieldMap[it] = it.selectionTableField
        fullFieldMap[it] = "$tableName.${it.name.sqlName}"
      }
    properties = fieldMap.keys.toTypedArray().uncheckedCast()
    allField = fieldMap.values.toTypedArray()
    fullNameField = allField.map { "$tableName.$it" }.toTypedArray()
  }
}
