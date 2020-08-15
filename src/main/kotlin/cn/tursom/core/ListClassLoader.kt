package cn.tursom.core

@Suppress("unused")
class ListClassLoader(
  //url: Array<out URL>,
  parent: ClassLoader = getSystemClassLoader()
) : ClassLoader(parent),
  MutableList<ClassLoader> by ArrayList() {

  val parents: List<ClassLoader> get() = this

  fun addParent(parent: ClassLoader) = add(parent)
  fun removeParent(parent: ClassLoader) = remove(parent)

  override fun findClass(name: String?): Class<*> {
    try {
      return super.findClass(name)
    } catch (e: ClassNotFoundException) {
    }
    forEach {
      try {
        return it.loadClass(name)
      } catch (e: ClassNotFoundException) {
      }
    }
    throw ClassNotFoundException(name)
  }
}