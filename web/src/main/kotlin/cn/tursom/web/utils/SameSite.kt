package cn.tursom.web.utils

enum class SameSite(val str: String) {
	Strict("Strict"), Lax("Lax");

	override fun toString() = str
}