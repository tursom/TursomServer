package cn.tursom.socket.niothread

import java.nio.channels.SelectionKey

data class SelectionContext(val key: SelectionKey, val nioThread: INioThread)