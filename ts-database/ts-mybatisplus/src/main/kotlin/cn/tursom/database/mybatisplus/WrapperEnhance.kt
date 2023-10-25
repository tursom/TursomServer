/**
 * QueryWrapper kotlin 化改造
 * @author tursom
 */
@file:Suppress("unused")
@file:OptIn(UncheckedCast::class)

package cn.tursom.database.mybatisplus

import cn.tursom.core.util.UncheckedCast
import cn.tursom.core.util.allFieldsSequence
import cn.tursom.core.util.uncheckedCast
import cn.tursom.log.impl.Slf4jImpl
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
import java.sql.SQLException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

val logger = Slf4jImpl.getLogger("cn.tursom.database")

val selectMethod: Query<*, *, Any>.(Array<out Any>) -> Any = Query<*, *, Any>::select

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>, Q> Query<Children, T, Q>.select(
  columns: Collection<String>,
): Children = select(T::class.java) {
  it.column in columns
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>, Q> Query<Children, T, Q>.select(
  columns: Sequence<String>,
): Children = select(columns.toSet())

/**
 * QueryWrapper<T>().select(T::fieldName, value)
 */
inline fun <reified T, Children : Wrapper<T>> Query<Children, T, String>.select(
  vararg columns: KProperty1<T, *>,
): Children = select(columns = columns as Array<out KProperty<*>>)

inline fun <reified T, Children : Wrapper<T>> Query<Children, T, String>.select(
  vararg columns: KProperty<*>,
): Children = select(columns.mapNotNull {
  it.getFieldData()?.field?.name
}.toSet())

inline fun <reified T, Children : Wrapper<T>> Query<Children, T, String>.fullSelect(
  vararg columns: KProperty<*>,
): Children = selectMethod(uncheckedCast(), columns.mapNotNull {
  it.getFieldData()?.selectionName
}.toTypedArray()).uncheckedCast()

inline fun <reified T, Children : Wrapper<T>> Query<Children, T, String>.joinSelect(
  vararg columns: KProperty<*>,
): Children = fullSelect(columns = columns)

/**
 * QueryWrapper<T>().eq(T::fieldName, value)
 */
inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  column: KProperty1<T, *>,
  value: Any,
): Children = eq(column.getFieldData()!!.name, value)

fun <T, Children : Wrapper<T>> Compare<Children, String>.eq(
  column: KProperty<*>,
  value: Any,
): Children = eq(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  column: Pair<KProperty1<T, *>, Any?>,
): Children = eq(column.first.getFieldData()!!.name, column.second)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  vararg pair: Pair<KProperty1<T, *>, Any?>,
): Children = eq(pair.asSequence())

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.eq(
  pair: Collection<Pair<KProperty1<T, *>, Any>>,
): Children = eq(pair.asSequence())

fun <Children> Compare<Children, String>.eq(
  pair: Sequence<Pair<KProperty<*>, *>>,
): Children = allEq(
  pair.mapNotNull { (property, value) ->
    val fieldData = property.getFieldData() ?: return@mapNotNull null
    fieldData.name to value
  }.associate {
    it
  }
)

fun <Children> Compare<Children, String>.eq(vararg pair: Pair<KProperty<*>, *>): Children = eq(pair.asSequence())

inline fun <reified T : Any, Children : Wrapper<T>> Compare<Children, String>.eq(entity: T): Children {
  val eqs = LinkedList<Pair<KProperty1<T, *>, Any>>()
  entity::class.memberProperties.uncheckedCast<Collection<KProperty1<T, *>>>().forEach {
    it.isAccessible = true
    eqs.add(it to (it(entity) ?: return@forEach))
  }
  return eq(eqs)
}

@JvmName("eqMapEntry")
fun <Children> Compare<Children, String>.eq(
  pair: Sequence<Map.Entry<KProperty<*>, *>>,
): Children = allEq(
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
inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.allEq(map: Map<out KProperty1<T, *>, *>): Children =
  eq(map.asSequence())

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.allEq(vararg pair: Pair<KProperty1<T, *>, *>): Children =
  eq(pair.asSequence())

fun <Children> Compare<Children, String>.allEq(vararg pair: Pair<KProperty<*>, *>): Children =
  eq(pair.asSequence())

fun <Children> Compare<Children, String>.allFullEq(vararg pair: Pair<KProperty<*>, *>): Children =
  eq(pair.asSequence())

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.ne(
  column: KProperty1<T, *>,
  value: Any?,
): Children = ne(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.gt(
  column: KProperty1<T, *>,
  value: Any?,
): Children = gt(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.ge(
  column: KProperty1<T, *>,
  value: Any?,
): Children = ge(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.lt(
  column: KProperty1<T, *>,
  value: Any?,
): Children = lt(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.le(
  column: KProperty1<T, *>,
  value: Any?,
): Children = le(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.between(
  column: KProperty1<T, *>,
  val1: Any?,
  val2: Any?,
): Children = between(column.getFieldData()!!.name, val1, val2)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.notBetween(
  column: KProperty1<T, *>,
  val1: Any?,
  val2: Any?,
): Children = notBetween(column.getFieldData()!!.name, val1, val2)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.like(
  column: KProperty1<T, *>,
  value: Any?,
): Children = like(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.notLike(
  column: KProperty1<T, *>,
  value: Any?,
): Children = notLike(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.likeLeft(
  column: KProperty1<T, *>,
  value: Any?,
): Children = likeLeft(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Compare<Children, String>.likeRight(
  column: KProperty1<T, *>,
  value: Any?,
): Children = likeRight(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.having(
  column: KProperty1<T, *>,
  vararg value: Any?,
): Children = having(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.isNull(column: KProperty1<T, *>): Children =
  isNull(column.getFieldData()!!.name)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.isNotNull(column: KProperty1<T, *>): Children =
  isNotNull(column.getFieldData()!!.name)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.`in`(
  column: KProperty1<T, *>,
  value: Any?,
): Children = `in`(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.`in`(
  column: KProperty1<T, *>,
  value: Collection<Any?>,
): Children = `in`(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.`in`(
  column: KProperty1<T, *>,
  vararg value: Any,
): Children = `in`(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.notIn(
  column: KProperty1<T, *>,
  value: Collection<Any?>,
): Children = notIn(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.notIn(
  column: KProperty1<T, *>,
  vararg value: Any,
): Children = notIn(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.inSql(
  column: KProperty1<T, *>,
  value: String?,
): Children = inSql(column.getFieldData()!!.name, value)

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.notInSql(
  column: KProperty1<T, *>,
  value: String?,
): Children = notInSql(column.getFieldData()!!.name, value)


inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.groupBy(column: KProperty1<T, *>): Children =
  groupBy(column.getFieldData()!!.name)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.groupBy(vararg columns: KProperty1<T, *>): Children =
  groupBy(columns.map { column -> column.getFieldData()!!.name }) as Children

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByAsc(column: KProperty1<T, *>): Children =
  orderByAsc(column.getFieldData()!!.name)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByAsc(vararg columns: KProperty1<T, *>): Children =
  orderByAsc(columns.map { column -> column.getFieldData()!!.name }) as Children

inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByDesc(column: KProperty1<T, *>): Children =
  orderByDesc(column.getFieldData()!!.name)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, Children : Wrapper<T>> Func<Children, String>.orderByDesc(vararg columns: KProperty1<T, *>): Children =
  orderByDesc(columns.map { column -> column.getFieldData()!!.name }) as Children


/**
 * QueryWrapper<T>()
 *     .xx()
 *     .xxx()
 *     ...
 *     .limit1()
 */
fun <Children> Join<Children>.limit1(): Children = last("LIMIT 1")

fun <Children> Join<Children>.limit(count: Int): Children = last("LIMIT $count")
fun <Children> Join<Children>.limit(start: Int, count: Int): Children = last("LIMIT $start, $count")

fun <T, Children : Wrapper<T>> Update<Children, String>.set(
  column: KProperty1<T, *>,
  value: Any?,
): Children {
  if (column.getFieldData()?.exist == false) {
    logger.warn(
      "cannot get field data for {}, javaField: {}, kPropertyFieldDataMap: {}",
      column, column.javaField, kPropertyFieldDataMap[column]
    )
    throw SQLException("using non exist field ${column.name}")
  }
  return set(column.getFieldData()!!.name, value)
}

fun <T, Children : Wrapper<T>> Update<Children, String>.set(vararg values: Pair<KProperty1<T, *>, Any?>): Children {
  var children: Children? = null
  values.forEach { (column, value) ->
    set(column, value).let {
      if (children == null) children = it
    }
  }
  return children ?: uncheckedCast()
}

fun <Children> Update<Children, String>.set(value: Any?): Children {
  value ?: return uncheckedCast()
  value.javaClass.allFieldsSequence.filterNotExists.forEach { field ->
    field.isAccessible = true
    set(field.getFieldData().name, field.get(value))
  }
  return uncheckedCast()
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

  fun <T, W : AbstractWrapper<T, String, out W>> regex(wrapper: W, column: String, value: Any): W {
    wrapper.expression.add(ISqlSegment { columnToString(column) }, Regexp, ISqlSegment {
      val genParamName =
        Constants.WRAPPER_PARAM + (paramNameSeqField.get(wrapper) as AtomicInteger).incrementAndGet()
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
): W = WrapperEnhance.regex(this, column.getFieldData()!!.name, value)

inline fun <reified T, W : AbstractWrapper<T, String, W>> W.regex(
  column: KProperty1<T, *>, regex: Regex,
): W = WrapperEnhance.regex(this, column.getFieldData()!!.name, regex.toString())
