package cn.tursom.web

interface MutableHttpContent : HttpContent {
    fun addParam(key: String, value: String)
}