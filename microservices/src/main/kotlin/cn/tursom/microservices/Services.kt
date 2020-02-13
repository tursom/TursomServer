package cn.tursom.microservices

interface Services<T, R> {
  suspend fun handle(msg: T): R
}