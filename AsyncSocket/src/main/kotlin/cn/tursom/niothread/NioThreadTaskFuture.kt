package cn.tursom.niothread

interface NioThreadTaskFuture<T> {
	fun get(): T
}