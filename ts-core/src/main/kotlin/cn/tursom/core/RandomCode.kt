package cn.tursom.core

import java.io.File
import java.util.*

class RandomCode {
  private val randomCode = "${randomInt(10000000, 99999999)}"

  override fun toString(): String {
    return randomCode
  }

  fun showCode(codeName: String = "passcode", filepath: String? = null) {
    println("$codeName: $randomCode")
    filepath ?: return
    val file = File(filepath)
    file.createNewFile()
    file.writeText("$codeName = $randomCode")
  }

  companion object {
    private fun randomInt(min: Int, max: Int) = Random().nextInt(max) % (max - min + 1) + min
  }
}