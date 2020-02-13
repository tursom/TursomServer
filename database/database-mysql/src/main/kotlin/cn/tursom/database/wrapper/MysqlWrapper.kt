package cn.tursom.database.wrapper

import java.util.function.BiPredicate
import java.util.function.Consumer
import kotlin.reflect.KProperty1

open class MysqlWrapper<T> : AbstractWrapper<T, MysqlWrapper<T>> {
  override fun <V> allEq(condition: Boolean, params: Map<KProperty1<T, *>, V>?, null2IsNull: Boolean): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun <V> allEq(condition: Boolean, filter: BiPredicate<KProperty1<T, *>, V>?, params: Map<KProperty1<T, *>, V>?, null2IsNull: Boolean): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun eq(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun ne(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun gt(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun ge(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun lt(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun le(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun between(condition: Boolean, column: KProperty1<T, *>, val1: Any?, val2: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun notBetween(condition: Boolean, column: KProperty1<T, *>, val1: Any?, val2: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun like(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun notLike(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun likeLeft(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun likeRight(condition: Boolean, column: KProperty1<T, *>, `val`: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun and(condition: Boolean, consumer: Consumer<MysqlWrapper<T>>?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun or(condition: Boolean, consumer: Consumer<MysqlWrapper<T>>?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun or(condition: Boolean): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun nested(condition: Boolean, consumer: Consumer<MysqlWrapper<T>>?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun apply(condition: Boolean, applySql: String?, vararg value: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun last(condition: Boolean, lastSql: String?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun comment(condition: Boolean, comment: String?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun exists(condition: Boolean, existsSql: String?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun notExists(condition: Boolean, notExistsSql: String?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun isNull(condition: Boolean, column: KProperty1<T, *>): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun isNotNull(condition: Boolean, column: KProperty1<T, *>): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun `in`(condition: Boolean, column: KProperty1<T, *>, coll: Collection<*>?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun notIn(condition: Boolean, column: KProperty1<T, *>, coll: Collection<*>?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun inSql(condition: Boolean, column: KProperty1<T, *>, inValue: String?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun notInSql(condition: Boolean, column: KProperty1<T, *>, inValue: String?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun groupBy(condition: Boolean, vararg columns: KProperty1<T, *>): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun orderBy(condition: Boolean, isAsc: Boolean, vararg columns: KProperty1<T, *>): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun having(condition: Boolean, sqlHaving: String?, vararg params: Any?): MysqlWrapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}