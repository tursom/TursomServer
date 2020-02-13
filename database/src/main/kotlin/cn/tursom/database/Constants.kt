package cn.tursom.database

import cn.tursom.database.StringPool.DOT

/**
 * mybatis_plus 自用常量集中管理
 *
 * @author miemie
 * @since 2018-07-22
 */
@Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")
object Constants {
  /**
   * project name
   */
  const val MYBATIS_PLUS = "mybatis-plus"
  /**
   * MD5
   */
  const val MD5 = "MD5"
  /**
   * 实体类
   */
  const val ENTITY = "et"
  /**
   * 实体类 带后缀 ==> .
   */
  const val ENTITY_DOT = ENTITY + DOT
  /**
   * wrapper 类
   */
  const val WRAPPER = "ew"
  /**
   * wrapper 类 带后缀 ==> .
   */
  const val WRAPPER_DOT = WRAPPER + DOT
  /**
   * wrapper 类的属性 entity
   */
  const val WRAPPER_ENTITY = WRAPPER_DOT + "entity"
  /**
   * wrapper 类的属性 sqlSegment
   */
  const val WRAPPER_SQLSEGMENT = WRAPPER_DOT + "sqlSegment"
  /**
   * wrapper 类的属性 emptyOfNormal
   */
  const val WRAPPER_EMPTYOFNORMAL = WRAPPER_DOT + "emptyOfNormal"
  /**
   * wrapper 类的属性 nonEmptyOfNormal
   */
  const val WRAPPER_NONEMPTYOFNORMAL = WRAPPER_DOT + "nonEmptyOfNormal"
  /**
   * wrapper 类的属性 nonEmptyOfEntity
   */
  const val WRAPPER_NONEMPTYOFENTITY = WRAPPER_DOT + "nonEmptyOfEntity"
  /**
   * wrapper 类的属性 emptyOfWhere
   */
  const val WRAPPER_EMPTYOFWHERE = WRAPPER_DOT + "emptyOfWhere"
  /**
   * wrapper 类的判断属性 nonEmptyOfWhere
   */
  const val WRAPPER_NONEMPTYOFWHERE = WRAPPER_DOT + "nonEmptyOfWhere"
  /**
   * wrapper 类的属性 entity 带后缀 ==> .
   */
  const val WRAPPER_ENTITY_DOT = WRAPPER_DOT + "entity" + DOT
  /**
   * UpdateWrapper 类的属性 sqlSet
   */
  const val U_WRAPPER_SQL_SET = WRAPPER_DOT + "sqlSet"
  /**
   * QueryWrapper 类的属性 sqlSelect
   */
  const val Q_WRAPPER_SQL_SELECT = WRAPPER_DOT + "sqlSelect"
  /**
   * wrapper 类的属性 sqlComment
   */
  const val Q_WRAPPER_SQL_COMMENT = WRAPPER_DOT + "sqlComment"
  /**
   * columnMap
   */
  const val COLUMN_MAP = "cm"
  /**
   * columnMap.isEmpty
   */
  const val COLUMN_MAP_IS_EMPTY = COLUMN_MAP + DOT.toString() + "isEmpty"
  /**
   * collection
   */
  const val COLLECTION = "coll"
  /**
   * where
   */
  const val WHERE = "WHERE"
  /**
   * 乐观锁字段
   */
  const val MP_OPTLOCK_VERSION_ORIGINAL = "MP_OPTLOCK_VERSION_ORIGINAL"
  const val MP_OPTLOCK_VERSION_COLUMN = "MP_OPTLOCK_VERSION_COLUMN"
  const val MP_OPTLOCK_ET_ORIGINAL = "MP_OPTLOCK_ET_ORIGINAL"
  const val WRAPPER_PARAM = "MPGENVAL"
  const val WRAPPER_PARAM_FORMAT = "#{%s.paramNameValuePairs.%s}"
}