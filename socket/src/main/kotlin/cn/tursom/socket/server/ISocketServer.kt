package cn.tursom.socket.server

import java.io.Closeable

/**
 * 套接字服务器的基本形式，提供运行、关闭的基本操作
 * 其应支持最基本的创建形式：
 * XXXServer(port) {
 *   // 业务逻辑
 * }
 */
interface ISocketServer : Runnable, Closeable {
  val port: Int
}