package cn.tursom.web.utils

enum class CacheControl(val str: String) {
  Public("public"), Private("private"), NoStore("no-store"), NoCache("no-cache");

  override fun toString(): String = str
}