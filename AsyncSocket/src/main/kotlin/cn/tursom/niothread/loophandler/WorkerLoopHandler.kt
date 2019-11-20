package cn.tursom.niothread.loophandler

import cn.tursom.socket.NioProtocol
import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey

class WorkerLoopHandler(private val protocol: NioProtocol) {
  fun handle(nioThread: NioThread, key: SelectionKey) {
    try {
      when {
        key.isReadable -> {
          protocol.handleRead(key, nioThread)
        }
        key.isWritable -> {
          protocol.handleWrite(key, nioThread)
        }
      }
    } catch (e: Throwable) {
      try {
        protocol.exceptionCause(key, nioThread, e)
      } catch (e1: Throwable) {
        e.printStackTrace()
        e1.printStackTrace()
        key.cancel()
        key.channel().close()
      }
    }
  }
}