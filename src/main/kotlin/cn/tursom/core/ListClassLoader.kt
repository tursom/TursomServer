package cn.tursom.core

import java.net.URL
import java.net.URLClassLoader


@Suppress("unused")
class ListClassLoader(url: Array<out URL>, parent: ClassLoader? = null) : URLClassLoader(url, parent) {
  private val parentList = ArrayList<ClassLoader>()
  val parents: List<ClassLoader> get() = parentList

  fun addParent(parent: ClassLoader) = parentList.add(parent)
  fun removeParent(parent: ClassLoader) = parentList.remove(parent)

  override fun findClass(name: String?): Class<*> {
    try {
      return super.findClass(name)
    } catch (e: ClassNotFoundException) {
    }
    parentList.forEach {
      try {
        return it.loadClass(name)
      } catch (e: ClassNotFoundException) {
      }
    }
    throw ClassNotFoundException(name)
  }
}