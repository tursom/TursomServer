package cn.tursom.database

import cn.tursom.core.cast
import cn.tursom.core.getClassByPackage
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

object TypeAdapterFactory {
  private val adapterMap = ConcurrentSkipListMap<Int, ConcurrentLinkedQueue<TypeAdapter<*>>>()
  private val adapterQueue = ConcurrentLinkedQueue<TypeAdapter<*>>()

  init {
    getClassByPackage("cn.tursom.database.typeadapter").forEach {
      try {
        val clazz = Class.forName(it).kotlin
        if (clazz.isSubclassOf(TypeAdapter::class)) {
          val adapter: TypeAdapter<*> = clazz.objectInstance?.cast() ?: run {
            clazz.createInstance()
          }.cast()
          registerAdapter(adapter)
        }
      } catch (e: Error) {
      }
    }
  }

  private fun getAdapterQueue(level: Int): ConcurrentLinkedQueue<TypeAdapter<*>> {
    var queue = adapterMap[-level]
    if (queue == null) {
      synchronized(this) {
        adapterMap[-level] = ConcurrentLinkedQueue()
        queue = adapterMap[-level]!!
      }
    }
    return queue!!
  }

  fun registerAdapter(adapter: TypeAdapter<*>) {
    getAdapterQueue(adapter.level).add(adapter)
  }


}