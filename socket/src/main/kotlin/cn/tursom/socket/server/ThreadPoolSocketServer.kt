package cn.tursom.socket.server

import cn.tursom.core.getTAG
import cn.tursom.socket.BaseSocket
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * SocketServer线程池服务器
 * 每当有新连接接入时就会将handler:Runnable加入线程池的任务队列中运行
 * 通过重载handler:Runnable的getter处理业务逻辑
 * start()函数实现无限循环监听，同时自动处理异常
 * 最新接入的套接字出存在socket变量中
 * 通过调用close()或closeServer()关闭服务器，造成的异常会被自动处理
 *
 * 标准使用例：
 * object : ThreadPoolSocketServer(port) {
 *     override val handler: Runnable
 *         get() = object : ServerHandler(cn.tursom.socket) {
 *             override fun handle() {
 *                 ... // 业务逻辑代码
 *             }
 *         }
 * }
 *
 */
class ThreadPoolSocketServer
/**
 * 使用代码而不是配置文件的构造函数
 *
 * @param port 运行端口，必须指定
 * @param threads 线程池最大线程数
 * @param queueSize 线程池任务队列大小
 * @param keepAliveTime 线程最长存活时间
 * @param timeUnit timeout的单位，默认毫秒
 * @param handler 对套接字处理的业务逻辑
 */(
    override val port: Int,
    threads: Int = 1,
    queueSize: Int = 1,
    keepAliveTime: Long = 60_000L,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    override val handler: BaseSocket.() -> Unit
) : ISimpleSocketServer {

  constructor(
      port: Int,
      handler: BaseSocket.() -> Unit
  ) : this(port, 1, 1, 60_000L, TimeUnit.MILLISECONDS, handler)

  var socket = Socket()
  private val pool: ThreadPoolExecutor =
      ThreadPoolExecutor(threads, threads, keepAliveTime, timeUnit, LinkedBlockingQueue(queueSize))
  private var serverSocket: ServerSocket = ServerSocket(port)

  /**
   * 主要作用：
   * 循环接受连接请求
   * 讲接收的连接交给handler处理
   * 连接初期异常处理
   * 自动关闭套接字服务器与线程池
   */
  override fun run() {
    while (!serverSocket.isClosed) {
      try {
        socket = serverSocket.accept()
        println("$TAG: run(): get connect: $socket")
        pool.execute {
          socket.use {
            BaseSocket(it).handler()
          }
        }
      } catch (e: IOException) {
        if (pool.isShutdown || serverSocket.isClosed) {
          System.err.println("server closed")
          break
        }
        e.printStackTrace()
      } catch (e: SocketException) {
        e.printStackTrace()
        break
      } catch (e: RejectedExecutionException) {
        socket.getOutputStream()?.write(poolIsFull)
      } catch (e: Exception) {
        e.printStackTrace()
        break
      }
    }
    close()
    System.err.println("server closed")
  }

  /**
   * 关闭服务器套接字
   */
  private fun closeServer() {
    if (!serverSocket.isClosed) {
      serverSocket.close()
    }
  }

  /**
   * 关闭线程池
   */
  private fun shutdownPool() {
    if (!pool.isShutdown) {
      pool.shutdown()
    }
  }

  /**
   * 服务器是否已经关闭
   */
  @Suppress("unused")
  fun isClosed() = pool.isShutdown || serverSocket.isClosed

  /**
   * 关闭服务器
   */
  override fun close() {
    shutdownPool()
    closeServer()
  }

  companion object {
    val TAG = getTAG(this::class.java)
    /**
     * 线程池满时返回给客户端的信息
     */
    val poolIsFull = "server pool is full".toByteArray()
  }
}