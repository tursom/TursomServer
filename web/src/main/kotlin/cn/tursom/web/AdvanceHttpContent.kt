package cn.tursom.web

interface AdvanceHttpContent : HttpContent {
    fun addParam(key: String, value: String)
}