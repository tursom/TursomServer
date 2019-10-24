package cn.tursom.utils

import com.google.gson.Gson
import kotlinx.coroutines.*

@Suppress("unused", "SpellCheckingInspection")
val gson = Gson()

inline fun <reified T : Any> Gson.fromJson(json: String) = this.fromJson(json, T::class.java)!!


suspend fun <T> io(block: suspend CoroutineScope.() -> T): T {
  return withContext(Dispatchers.IO, block)
}

fun background(block: suspend CoroutineScope.() -> Unit) {
  GlobalScope.launch(block = block)
}

suspend fun <T> ui(block: suspend CoroutineScope.() -> T): T {
  return withContext(Dispatchers.Main, block)
}