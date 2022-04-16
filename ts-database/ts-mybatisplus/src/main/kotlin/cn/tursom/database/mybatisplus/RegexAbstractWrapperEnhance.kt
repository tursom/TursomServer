package cn.tursom.database.mybatisplus

import cn.tursom.core.uncheckedCast
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import kotlin.reflect.KProperty1

@MybatisPlusEnhanceDslMaker
interface RegexAbstractWrapperEnhance<T, out W : AbstractWrapper<T, String, out W>, Children> {
  val wrapper: W get() = uncheckedCast()

  infix fun String.regex(
    value: Any,
  ): Children = WrapperEnhance.regex(wrapper, this, value).uncheckedCast()

  infix fun KProperty1<T, *>.regex(
    value: Any,
  ): Children = WrapperEnhance.regex(wrapper, getFieldData()!!.name, value).uncheckedCast()

  infix fun KProperty1<T, *>.regex(
    regex: Regex,
  ): Children = WrapperEnhance.regex(wrapper, getFieldData()!!.name, regex.toString()).uncheckedCast()
}