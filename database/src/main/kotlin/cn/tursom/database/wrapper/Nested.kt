package cn.tursom.database.wrapper

import java.io.Serializable
import java.util.function.Consumer

interface Nested<Param, Children> : Serializable {
  fun and(consumer: Consumer<Param>?): Children {
    return this.and(true, consumer)
  }

  fun and(condition: Boolean, consumer: Consumer<Param>?): Children
  fun or(consumer: Consumer<Param>?): Children {
    return this.or(true, consumer)
  }

  fun or(condition: Boolean, consumer: Consumer<Param>?): Children
  fun nested(consumer: Consumer<Param>?): Children {
    return this.nested(true, consumer)
  }

  fun nested(condition: Boolean, consumer: Consumer<Param>?): Children
}