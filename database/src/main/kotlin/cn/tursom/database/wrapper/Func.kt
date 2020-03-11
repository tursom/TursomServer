package cn.tursom.database.wrapper

import cn.tursom.database.SqlUtils
import java.io.Serializable
import kotlin.reflect.KProperty1

interface Func<T, Children> : Serializable {
  /**
   * 字段 IS NULL
   * <p>例: isNull("name")</p>
   *
   * @this 字段
   * @return children
   */
  fun KProperty1<T, *>.isNull(): Children = SqlUtils { tableField }.isNull()
  fun String.isNull(): Children

  /**
   * 字段 IS NOT NULL
   * <p>例: isNotNull("name")</p>
   *
   * @this 字段
   * @return children
   */
  fun KProperty1<T, *>.isNotNull(): Children = SqlUtils { tableField }.isNotNull()
  fun String.isNotNull(): Children

  /**
   * 字段 IN (value.get(0), value.get(1), ...)
   * <p>例: in("id", Arrays.asList(1, 2, 3, 4, 5))</p>
   *
   * <li> 如果集合为 empty 则不会进行 sql 拼接 </li>
   *
   * @this 字段
   * @param coll      数据集合
   * @return children
   */
  infix fun <R> KProperty1<T, R>.`in`(coll: Collection<R>): Children = SqlUtils { tableField }.`in`(coll as Collection<Any?>)
  fun String.`in`(coll: Collection<Any?>): Children

  /**
   * 字段 IN (v0, v1, ...)
   * <p>例: in("id", 1, 2, 3, 4, 5)</p>
   *
   * <li> 如果动态数组为 empty 则不会进行 sql 拼接 </li>
   *
   * @this 字段
   * @param values    数据数组
   * @return children
   */
  fun <R> KProperty1<T, R>.`in`(vararg values: R): Children = SqlUtils { tableField }.`in`(values.asList())
  fun String.`in`(vararg values: Any?): Children = `in`(values.asList())


  /**
   * 字段 NOT IN (value.get(0), value.get(1), ...)
   * <p>例: notIn("id", Arrays.asList(1, 2, 3, 4, 5))</p>
   *
   * @this 字段
   * @param coll      数据集合
   * @return children
   */
  infix fun <R> KProperty1<T, R>.notIn(coll: Collection<R>): Children = SqlUtils { tableField }.notIn(coll)
  fun String.notIn(coll: Collection<Any?>): Children

  /**
   * 字段 NOT IN (v0, v1, ...)
   * <p>例: notIn("id", 1, 2, 3, 4, 5)</p>
   *
   * @this 字段
   * @param values    数据数组
   * @return children
   */
  fun <R> KProperty1<T, R>.notIn(vararg values: R): Children = notIn(values.asList())
  fun String.notIn(vararg value: Any?): Children = notIn(value.asList())

  /**
   * 字段 IN ( sql语句 )
   * <p>!! sql 注入方式的 in 方法 !!</p>
   * <p>例1: inSql("id", "1, 2, 3, 4, 5, 6")</p>
   * <p>例2: inSql("id", "select id from table where id &lt; 3")</p>
   *
   * @this 字段
   * @param inValue   sql语句
   * @return children
   */
  infix fun KProperty1<T, *>.inSql(inValue: String): Children = SqlUtils { tableField }.inSql(inValue)
  fun String.inSql(inValue: String): Children

  /**
   * 字段 NOT IN ( sql语句 )
   * <p>!! sql 注入方式的 not in 方法 !!</p>
   * <p>例1: notInSql("id", "1, 2, 3, 4, 5, 6")</p>
   * <p>例2: notInSql("id", "select id from table where id &lt; 3")</p>
   *
   * @this 字段
   * @param inValue   sql语句 ---&gt; 1,2,3,4,5,6 或者 select id from table where id &lt; 3
   * @return children
   */
  infix fun KProperty1<T, *>.notInSql(inValue: String): Children = SqlUtils { tableField }.notInSql(inValue)
  fun String.notInSql(inValue: String): Children

  val groupBy: String?

  /**
   * 分组：GROUP BY 字段, ...
   * <p>例: groupBy("id", "name")</p>
   *
   * @param columns   字段数组
   * @return children
   */
  fun groupBy(columns: Iterable<KProperty1<T, *>>): Children = groupBy(SqlUtils { columns.map { it.tableField } })
  fun groupBy(vararg columns: KProperty1<T, *>): Children = groupBy(columns.asList())
  fun groupBy(columns: Collection<String>): Children
  fun groupBy(vararg columns: String): Children = groupBy(columns.asList())

  val orderBy: String?

  /**
   * 排序：ORDER BY 字段, ... ASC
   * <p>例: orderByAsc("id", "name")</p>
   *
   * @param columns   字段数组
   * @return children
   */
  fun orderByAsc(columns: Iterable<KProperty1<T, *>>): Children = orderByAsc(SqlUtils { columns.map { it.tableField } })
  fun orderByAsc(vararg columns: KProperty1<T, *>): Children = orderByAsc(columns.asList())
  fun orderByAsc(columns: Collection<String>): Children
  fun orderByAsc(vararg columns: String): Children = orderByAsc(columns.asList())

  /**
   * 排序：ORDER BY 字段, ... DESC
   * <p>例: orderByDesc("id", "name")</p>
   *
   * @param columns   字段数组
   * @return children
   */
  fun orderByDesc(columns: Iterable<KProperty1<T, *>>): Children = orderByDesc(SqlUtils { columns.map { it.tableField } })
  fun orderByDesc(vararg columns: KProperty1<T, *>): Children = orderByDesc(columns.asList())
  fun orderByDesc(columns: Collection<String>): Children
  fun orderByDesc(vararg columns: String): Children = orderByDesc(columns.asList())

  /**
   * 排序：ORDER BY 字段, ...
   * <p>例: orderBy("id", "name")</p>
   *
   * @param isAsc     是否是 ASC 排序
   * @param columns   字段数组
   * @return children
   */
  fun orderBy(isAsc: Boolean, columns: Iterable<KProperty1<T, *>>): Children = orderBy(isAsc, SqlUtils { columns.map { it.tableField } })
  fun orderBy(isAsc: Boolean, vararg columns: KProperty1<T, *>): Children = orderBy(isAsc, columns.asList())
  fun orderBy(isAsc: Boolean, columns: Collection<String>): Children
  fun orderBy(isAsc: Boolean, vararg columns: String): Children = orderBy(isAsc, columns.asList())

  val having: String?
  val havingParams: Collection<Any?>

  /**
   * HAVING ( sql语句 )
   * <p>例1: having("sum(age) &gt; 10")</p>
   * <p>例2: having("sum(age) &gt; ?", 10)</p>
   *
   * @param sqlHaving sql 语句
   * @param params    参数数组
   * @return children
   */
  fun having(sqlHaving: String, vararg params: Any?): Children


}