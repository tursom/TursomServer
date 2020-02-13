package cn.tursom.microservices

import cn.tursom.socket.server.NioServer

class MicroserviceServer(val port: Int) {
  private val server = NioServer(port) {
    val content = ServiceContent()
  }
}

class ServiceContent {
  private val handler = ArrayList<Services<*, *>>()
}