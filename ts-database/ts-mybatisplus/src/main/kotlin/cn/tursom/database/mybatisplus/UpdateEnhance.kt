package cn.tursom.database.mybatisplus

import cn.tursom.core.allFieldsSequence
import cn.tursom.core.uncheckedCast
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.update.Update
import java.sql.SQLException
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

@MybatisPlusEnhanceDslMaker
interface UpdateEnhance<T, out Children : Wrapper<T>> {
  val update: Update<out Children, String> get() = uncheckedCast()

  infix fun KProperty1<T, *>.set(
    value: Any?,
  ): Children {
    if (getFieldData()?.exist == false) {
      logger.warn(
        "cannot get field data for {}, javaField: {}, kPropertyFieldDataMap: {}",
        this, javaField, kPropertyFieldDataMap[this]
      )
      throw SQLException("using non exist field $name")
    }
    return update.set(getFieldData()!!.name, value).uncheckedCast()
  }

  fun set(vararg values: Pair<KProperty1<T, *>, Any?>): Children {
    values.forEach { (column, value) ->
      column set value
    }
    return uncheckedCast()
  }

  fun set(value: T?): Children {
    value ?: return uncheckedCast()
    value.javaClass.allFieldsSequence.filterNotExists.forEach { field ->
      field.isAccessible = true
      update.set(field.getFieldData().name, field.get(value))
    }
    return uncheckedCast()
  }
}