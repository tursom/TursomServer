/**
 * QueryWrapper kotlin 化改造
 * @author 王景阔
 */
@file:Suppress("unused")

package cn.tursom.database

import cn.tursom.core.uncheckedCast
import cn.tursom.database.annotations.Getter
import cn.tursom.log.impl.Slf4jImpl
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.ISqlSegment
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare
import com.baomidou.mybatisplus.core.conditions.interfaces.Func
import com.baomidou.mybatisplus.core.conditions.interfaces.Join
import com.baomidou.mybatisplus.core.conditions.query.Query
import com.baomidou.mybatisplus.core.conditions.update.Update
import com.baomidou.mybatisplus.core.toolkit.Constants
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

val logger = Slf4jImpl.getLogger("com.ddbes.pan.kit.jdbc")

val select: Query<*, *, Any>.(Array<out Any>) -> Any = Query<*, *, Any>::select

@Suppress("UNCHECKED_CAST")
fun <T, Children : Wrapper<T>, Q> Query<Children, T, Q>.select(
  columns: Array<out Q>,
): Children = (select as Query<Children, T, Q>.(Array<out Q>) -> Children)(columns)

/**
 * QueryWrapper<T>().select(T::fieldName, value)
 */
inline fun <reified T, Children : Wrapper<T>> Query<Children, T, String>.select(
  vararg columns: KProperty1<T, *>,
): Children = select(columns.tableField)

fun <T, Children : Wrapper<T>> Query<Children, T, String>.select(
  vararg columns: KProperty<*>,
): Children = fullSelect(*columns)

fun <T, Children : Wrapper<T>> Query<Children, T, String>.fullSelect(
  vararg columns: KProperty<*>,
): Children = select(columns.fullTableField)

/**
 * QueryWrapper<T>().eq(T::fieldName, value)
 */
inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  column: KProperty1<T, *>,
  value: Any,
): Children = eq(column.directTableField, value)

fun <T, Children : Wrapper<T>> Compare<Children, String>.eq(
  column: KProperty<*>,
  value: Any,
): Children = eq(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  column: Pair<KProperty1<T, *>, Any?>,
): Children = eq(column.first.directTableField, column.second)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  vararg pair: Pair<KProperty1<T, *>, Any?>,
): Children = allEq(pair.tableField)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  pair: Collection<Pair<KProperty1<T, *>, Any>>,
): Children = allEq(pair.tableField)

fun <Children> Compare<Children, String>.eq(vararg pair: Pair<KProperty<*>, *>): Children = allEq(pair.fullTableField)

inline fun <reified T : Any, Children : Wrapper<T>> Compare<Children, String>.eq(entity: T): Children {
  val eqs = LinkedList<Pair<KProperty1<T, *>, Any>>()
  entity::class.memberProperties.uncheckedCast<Collection<KProperty1<T, *>>>().forEach {
    it.isAccessible = true
    eqs.add(it to (it(entity) ?: return@forEach))
  }
  return eq(eqs)
}

/**
 * QueryWrapper<T>().allEq(mapOf(
 *     T::fieldName1 to value1,
 *     T::fieldName2 to value2,
 *     ...
 * ))
 */
inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.allEq(map: Map<out KProperty1<T, *>, *>): Children =
  allEq(map.tableField)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.allEq(vararg pair: Pair<KProperty1<T, *>, *>): Children =
  allEq(pair.tableField)

fun <Children> Compare<Children, String>.allEq(vararg pair: Pair<KProperty<*>, *>): Children =
  allEq(pair.fullTableField)

fun <Children> Compare<Children, String>.allFullEq(vararg pair: Pair<KProperty<*>, *>): Children =
  allEq(pair.fullTableField)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.ne(
  column: KProperty1<T, *>,
  value: Any?,
): Children = ne(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.gt(
  column: KProperty1<T, *>,
  value: Any?,
): Children = gt(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.ge(
  column: KProperty1<T, *>,
  value: Any?,
): Children = ge(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.lt(
  column: KProperty1<T, *>,
  value: Any?,
): Children = lt(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.le(
  column: KProperty1<T, *>,
  value: Any?,
): Children = le(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.between(
  column: KProperty1<T, *>,
  val1: Any?,
  val2: Any?,
): Children = between(column.directTableField, val1, val2)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.notBetween(
  column: KProperty1<T, *>,
  val1: Any?,
  val2: Any?,
): Children = notBetween(column.directTableField, val1, val2)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.like(
  column: KProperty1<T, *>,
  value: Any?,
): Children = like(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.notLike(
  column: KProperty1<T, *>,
  value: Any?,
): Children = notLike(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.likeLeft(
  column: KProperty1<T, *>,
  value: Any?,
): Children = likeLeft(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.likeRight(
  column: KProperty1<T, *>,
  value: Any?,
): Children = likeRight(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.having(
  column: KProperty1<T, *>,
  vararg value: Any?,
): Children = having(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.isNull(column: KProperty1<T, *>): Children =
  isNull(column.directTableField)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.isNotNull(column: KProperty1<T, *>): Children =
  isNotNull(column.directTableField)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.`in`(
  column: KProperty1<T, *>,
  value: Any?,
): Children = `in`(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.`in`(
  column: KProperty1<T, *>,
  value: Collection<Any?>,
): Children = `in`(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.`in`(
  column: KProperty1<T, *>,
  vararg value: Any,
): Children = `in`(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.notIn(
  column: KProperty1<T, *>,
  value: Collection<Any?>,
): Children = notIn(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.notIn(
  column: KProperty1<T, *>,
  vararg value: Any,
): Children = notIn(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.inSql(
  column: KProperty1<T, *>,
  value: String?,
): Children = inSql(column.directTableField, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.notInSql(
  column: KProperty1<T, *>,
  value: String?,
): Children = notInSql(column.directTableField, value)


// val groupBy: Func<*, String>.(Array<out String>) -> Any = Func<*, String>::groupBy
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.groupBy(column: KProperty1<T, *>): Children =
  groupBy(column.directTableField)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.groupBy(vararg column: KProperty1<T, *>): Children =
  groupBy(column.directTableField.asList()) as Children

// val orderByAsc: Func<*, String>.(Array<out String>) -> Any = Func<*, String>::orderByAsc
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByAsc(column: KProperty1<T, *>): Children =
  orderByAsc(column.directTableField)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByAsc(vararg column: KProperty1<T, *>): Children =
  orderByAsc(column.directTableField.asList()) as Children

// val orderByDesc: Func<*, String>.(Array<out String>) -> Any = Func<*, String>::orderByDesc
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByDesc(column: KProperty1<T, *>): Children =
  orderByDesc(column.directTableField)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByDesc(vararg column: KProperty1<T, *>): Children =
  orderByDesc(column.directTableField.asList()) as Children


/**
 * QueryWrapper<T>()
 *     .xx()
 *     .xxx()
 *     ...
 *     .limit1()
 */
fun <Children> Join<Children>.limit1(): Children = last("LIMIT 1")

inline fun <reified T, Children : Wrapper<T>> Update<Children, String>.set(
  column: KProperty1<T, *>,
  value: Any?,
): Children {
  if (column.javaField == null || Modifier.isTransient(column.javaField?.modifiers ?: Modifier.TRANSIENT) ||
    column.javaField?.getAnnotation(TableField::class.java)?.exist == false
  ) {
    return uncheckedCast()
  }
  val getter = column.findAnnotation<Getter>()
  val v = if (getter == null || !(value != null && getter.getterType.isInstance(value))) {
    value
  } else {
    val getterMethod = column.javaGetter!!.declaringClass.getDeclaredMethod(getter.getter, getter.getterType.java)
    getterMethod.isAccessible = true
    getterMethod.invoke(null, value)
  }
  return set(column.directTableField, v)
}

inline fun <reified T, Children : Wrapper<T>> Update<Children, String>.set(vararg values: Pair<KProperty1<T, *>, Any?>): Children {
  var children: Children? = null
  values.forEach { (column, value) ->
    set(column, value).let {
      if (children == null) children = it
    }
  }
  return children ?: uncheckedCast()
}

inline fun <reified T : Any, Children : Wrapper<T>> Update<Children, String>.set(value: T): Children {
  var children: Children? = null
  value::class.memberProperties
    .uncheckedCast<Collection<KProperty1<T, *>>>()
    .filter {
      it.javaField != null &&
        it.findAnnotation() ?: it.javaField?.getAnnotation(Transient::class.java) == null &&
        !Modifier.isTransient(it.javaField?.modifiers ?: Modifier.TRANSIENT) &&
        it.javaField?.getAnnotation(TableField::class.java)?.exist != false
    }
    .forEach { property ->
      property.isAccessible = true
      set(property, property.get(value) ?: return@forEach).let {
        if (children == null) children = it
      }
    }
  return children ?: uncheckedCast()
}

object Regexp : ISqlSegment {
  override fun getSqlSegment(): String = "REGEXP"
}

object WrapperEnhance : AbstractWrapper<Any, String, WrapperEnhance>() {
  override fun instance(): WrapperEnhance = this
  private val paramNameSeqField: Field =
    AbstractWrapper::class.java.getDeclaredField("paramNameSeq").apply { isAccessible = true }

  init {
    initNeed()
  }

  fun <T, W : AbstractWrapper<T, String, W>> regex(wrapper: W, column: String, value: Any): W {
    wrapper.expression.add(ISqlSegment { columnToString(column) }, Regexp, ISqlSegment {
      val genParamName = Constants.WRAPPER_PARAM + (paramNameSeqField.get(wrapper) as AtomicInteger).incrementAndGet()
      wrapper.paramNameValuePairs[genParamName] = value
      "#{${Constants.WRAPPER}${Constants.WRAPPER_PARAM_MIDDLE}$genParamName}"
    })
    return wrapper
  }
}

fun <T, W : AbstractWrapper<T, String, W>> W.regex(column: String, value: Any): W =
  WrapperEnhance.regex(this, column, value)

inline fun <reified T, W : AbstractWrapper<T, String, W>> W.regex(
  column: KProperty1<T, *>, value: Any,
): W = WrapperEnhance.regex(this, column.directTableField, value)

inline fun <reified T, W : AbstractWrapper<T, String, W>> W.regex(
  column: KProperty1<T, *>, regex: Regex,
): W = WrapperEnhance.regex(this, column.directTableField, regex.toString())
