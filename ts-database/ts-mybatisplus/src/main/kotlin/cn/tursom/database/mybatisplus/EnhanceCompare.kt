package cn.tursom.database.mybatisplus

import cn.tursom.core.uncheckedCast
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Suppress("unused")
@MybatisPlusEnhanceDslMaker
interface EnhanceCompare<T, out W : AbstractWrapper<T, String, out W>, Children : Wrapper<T>> :
  EnhanceEntityClassEnhance<T>,
  RegexAbstractWrapperEnhance<T, W, Children> {
  val compare: Compare<Children, String> get() = uncheckedCast()

  /**
   * QueryWrapper<T>().eq(T::fieldName, value)
   */
  infix fun KProperty1<T, *>.eq(
    value: Any,
  ): Children = compare.eq(getFieldData()!!.name, value)

  infix fun KProperty<*>.eq(
    value: Any,
  ): Children = compare.eq(getFieldData()!!.name, value)

  fun eq(
    column: Pair<KProperty1<T, *>, Any?>,
  ): Children = compare.eq(column.first.getFieldData()!!.name, column.second)

  fun eq(
    vararg pair: Pair<KProperty1<T, *>, Any?>,
  ): Children = compare.eq(pair.asSequence())

  fun eq(
    pair: Collection<Pair<KProperty1<T, *>, Any>>,
  ): Children = compare.eq(pair.asSequence())

  fun eq(
    pair: Sequence<Pair<KProperty<*>, *>>,
  ): Children = compare.allEq(
    pair.mapNotNull { (property, value) ->
      val fieldData = property.getFieldData() ?: return@mapNotNull null
      fieldData.name to value
    }.associate {
      it
    }
  )

  fun <Children> Compare<Children, String>.eq(vararg pair: Pair<KProperty<*>, *>): Children = eq(pair.asSequence())

  fun eq(entity: T): Children {
    val eqs = LinkedList<Pair<KProperty1<T, *>, Any>>()
    entity!!.javaClass.kotlin.memberProperties.uncheckedCast<Collection<KProperty1<T, *>>>().forEach {
      it.isAccessible = true
      eqs.add(it to (it(entity) ?: return@forEach))
    }
    return eq(eqs)
  }

  fun eqMapEntry(
    pair: Sequence<Map.Entry<KProperty<*>, *>>,
  ): Children = compare.allEq(
    pair.mapNotNull { (property, value) ->
      val fieldData = property.getFieldData() ?: return@mapNotNull null
      fieldData.name to value
    }.associate {
      it
    }
  )

  /**
   * QueryWrapper<T>().allEq(mapOf(
   *     T::fieldName1 to value1,
   *     T::fieldName2 to value2,
   *     ...
   * ))
   */
  fun allEq(map: Map<out KProperty1<T, *>, *>): Children = compare.eq(map.asSequence())

  fun allEq(vararg pair: Pair<KProperty<*>, *>): Children = compare.eq(pair.asSequence())

  fun allFullEq(vararg pair: Pair<KProperty<*>, *>): Children = compare.eq(pair.asSequence())

  infix fun KProperty1<T, *>.ne(
    value: Any?,
  ): Children = compare.ne(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.gt(
    value: Any?,
  ): Children = compare.gt(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.ge(
    value: Any?,
  ): Children = compare.ge(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.lt(
    value: Any?,
  ): Children = compare.lt(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.le(
    value: Any?,
  ): Children = compare.le(getFieldData()!!.name, value)

  fun KProperty1<T, *>.between(
    val1: Any?,
    val2: Any?,
  ): Children = compare.between(getFieldData()!!.name, val1, val2)

  fun KProperty1<T, *>.notBetween(
    val1: Any?,
    val2: Any?,
  ): Children = compare.notBetween(getFieldData()!!.name, val1, val2)

  infix fun KProperty1<T, *>.like(
    value: Any?,
  ): Children = compare.like(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.notLike(
    value: Any?,
  ): Children = compare.notLike(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.likeLeft(
    value: Any?,
  ): Children = compare.likeLeft(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.likeRight(
    value: Any?,
  ): Children = compare.likeRight(getFieldData()!!.name, value)
}