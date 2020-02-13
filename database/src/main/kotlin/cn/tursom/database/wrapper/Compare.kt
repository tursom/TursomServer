package cn.tursom.database.wrapper

import java.io.Serializable
import java.util.function.BiPredicate
import kotlin.reflect.KProperty1

interface Compare<Children, T> : Serializable {
  fun <V> allEq(params: Map<KProperty1<T,*>, V>?): Children {
    return this.allEq(params, true)
  }

  fun <V> allEq(params: Map<KProperty1<T,*>, V>?, null2IsNull: Boolean): Children {
    return this.allEq(true, params, null2IsNull)
  }

  fun <V> allEq(condition: Boolean, params: Map<KProperty1<T,*>, V>?, null2IsNull: Boolean): Children
  fun <V> allEq(filter: BiPredicate<KProperty1<T,*>, V>?, params: Map<KProperty1<T,*>, V>?): Children {
    return this.allEq(filter, params, true)
  }

  fun <V> allEq(filter: BiPredicate<KProperty1<T,*>, V>?, params: Map<KProperty1<T,*>, V>?, null2IsNull: Boolean): Children {
    return this.allEq(true, filter, params, null2IsNull)
  }

  fun <V> allEq(condition: Boolean, filter: BiPredicate<KProperty1<T,*>, V>?, params: Map<KProperty1<T,*>, V>?, null2IsNull: Boolean): Children
  fun eq(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.eq(true, column, `val`)
  }

  fun eq(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun ne(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.ne(true, column, `val`)
  }

  fun ne(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun gt(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.gt(true, column, `val`)
  }

  fun gt(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun ge(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.ge(true, column, `val`)
  }

  fun ge(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun lt(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.lt(true, column, `val`)
  }

  fun lt(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun le(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.le(true, column, `val`)
  }

  fun le(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun between(column: KProperty1<T,*>, val1: Any?, val2: Any?): Children {
    return this.between(true, column, val1, val2)
  }

  fun between(condition: Boolean, column: KProperty1<T,*>, val1: Any?, val2: Any?): Children
  fun notBetween(column: KProperty1<T,*>, val1: Any?, val2: Any?): Children {
    return this.notBetween(true, column, val1, val2)
  }

  fun notBetween(condition: Boolean, column: KProperty1<T,*>, val1: Any?, val2: Any?): Children
  fun like(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.like(true, column, `val`)
  }

  fun like(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun notLike(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.notLike(true, column, `val`)
  }

  fun notLike(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun likeLeft(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.likeLeft(true, column, `val`)
  }

  fun likeLeft(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
  fun likeRight(column: KProperty1<T,*>, `val`: Any?): Children {
    return this.likeRight(true, column, `val`)
  }

  fun likeRight(condition: Boolean, column: KProperty1<T,*>, `val`: Any?): Children
}