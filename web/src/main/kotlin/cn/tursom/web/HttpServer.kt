package cn.tursom.web

import java.io.Closeable

interface HttpServer : Runnable, Closeable {
    val port: Int
}