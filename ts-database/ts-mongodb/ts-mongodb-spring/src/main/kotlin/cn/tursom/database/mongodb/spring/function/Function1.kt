package cn.tursom.database.mongodb.spring.function

fun interface Function1<R, T> : SerializedFunction {
  operator fun T.invoke(): R
}