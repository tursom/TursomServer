package cn.tursom.socket.niothread

interface NioThreadFuture<T> {
	fun get(): T
}