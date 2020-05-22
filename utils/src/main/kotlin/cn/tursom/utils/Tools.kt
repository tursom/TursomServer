package cn.tursom.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.javaMethod

@Suppress("unused", "SpellCheckingInspection")
val gson = GsonBuilder()
  .registerTypeAdapterFactory(GsonDataTypeAdaptor.FACTORY)
  .create()

@Suppress("unused", "SpellCheckingInspection")
val prettyGson = GsonBuilder()
  .registerTypeAdapterFactory(GsonDataTypeAdaptor.FACTORY)
  .setPrettyPrinting()
  .create()

fun Any.toJson(): String = gson.toJson(this)
fun Any.toPrettyJson(): String = prettyGson.toJson(this)
inline fun <reified T : Any> String.fromJson(): T = gson.fromJson(this, T::class.java)

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

suspend operator fun <T> Executor.invoke(action: () -> T): T {
  return suspendCoroutine { exec ->
    execute {
      try {
        exec.resume(action())
      } catch (e: Throwable) {
        exec.resumeWithException(e)
      }
    }
  }
}