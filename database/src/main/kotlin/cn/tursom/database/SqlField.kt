package cn.tursom.database

interface SqlField<T> {
	fun get(): T
	val sqlValue: String
}