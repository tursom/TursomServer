package cn.tursom.database

import cn.tursom.core.clone.InstantAllocator
import cn.tursom.core.getClassByPackage
import cn.tursom.core.uncheckedCast
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf

/**
 * 注册类型适配器的工厂
 */
object TypeAdapterFactory {
  private val adapterMap = ConcurrentSkipListMap<Int, MutableSet<TypeAdapter<*>>>()

  init {
    scanPackage(TypeAdapterFactory::class.java.`package`.name + ".typeadapter")
  }

  private fun getAdapterQueue(level: Int): MutableCollection<TypeAdapter<*>> {
    var set = adapterMap[-level]
    if (set == null) {
      synchronized(this) {
        adapterMap[-level] = CopyOnWriteArraySet()
        set = adapterMap[-level]!!
      }
    }
    return set!!
  }

  /**
   * 扫描包并注册适配器
   */
  fun scanPackage(
    pkg: String,
    classLoader: ClassLoader = this.javaClass.classLoader,
  ) {
    classLoader.getClassByPackage(pkg).forEach {
      try {
        val clazz = Class.forName(it).kotlin
        if (clazz.isSubclassOf(TypeAdapter::class)) {
          val adapter: TypeAdapter<*> = InstantAllocator[clazz].uncheckedCast()
          registerAdapter(adapter)
        }
      } catch (e: Throwable) {
      }
    }
  }

  /**
   * 注册适配器实例
   */
  fun registerAdapter(adapter: TypeAdapter<*>) {
    getAdapterQueue(adapter.level).add(adapter)
  }

  fun register(
    table: BaseTable<*>,
    field: KProperty1<*, *>,
  ): Column<*>? {
    adapterMap.forEach { (_, queue) ->
      queue.forEach {
        val column = it.register(table.uncheckedCast(), field.uncheckedCast())
        if (column != null) {
          return column
        }
      }
    }
    return null
  }

  override fun toString() = adapterMap.toString()
}
