package cn.tursom.database

import cn.tursom.core.cast
import cn.tursom.core.getClassByPackage
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

object TypeAdapterFactory {
  private val adapterMap = ConcurrentSkipListMap<Int, ConcurrentLinkedQueue<TypeAdapter<*>>>()

  init {
    scanPackage("cn.tursom.database.typeadapter")
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

  fun scanPackage(
    pkg: String,
    classLoader: ClassLoader = this.javaClass.classLoader
  ) {
    classLoader.getClassByPackage(pkg).forEach {
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

  fun registerAdapter(adapter: TypeAdapter<*>) {
    getAdapterQueue(adapter.level).add(adapter)
  }

  fun register(
    table: BaseTable<*>,
    field: KProperty1<*, *>
  ): Column<*>? {
    adapterMap.forEach { (_, queue) ->
      queue.forEach {
        val column = it.register(table.cast(), field.cast())
        if (column != null) {
          return column
        }
      }
    }
    return null
  }

  override fun toString() = adapterMap.toString()
}

fun main() {
  println(TypeAdapterFactory)
}