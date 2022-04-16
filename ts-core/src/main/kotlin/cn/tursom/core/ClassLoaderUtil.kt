package cn.tursom.core

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

@Suppress("UNCHECKED_CAST")
val ClassLoader.classes: List<Class<*>>
  get() = ClassLoaderUtil.classes.get(this) as List<Class<*>>

fun ClassLoader.getClassByPackage(
  packageName: String,
  childPackage: Boolean = true,
): List<String> = ClassLoaderUtil.getClassByPackage(packageName, childPackage, this)

inline fun <reified T> T.getClassByPackage(
  packageName: String,
  childPackage: Boolean = true,
): List<String> = ClassLoaderUtil.getClassByPackage(packageName, childPackage, T::class.java.classLoader)

object ClassLoaderUtil {
  internal val classes = ClassLoader::class.java.getDeclaredField("classes").also { it.isAccessible = true }

  /**
   * 获取某包下所有类
   * @param packageName 包名
   * @param childPackage 是否遍历子包
   * @return 类的完整名称
   */
  fun getClassByPackage(
    packageName: String,
    childPackage: Boolean = true,
    loader: ClassLoader = Thread.currentThread().contextClassLoader,
  ): List<String> {
    val packagePath = packageName.replace(".", "/")
    val url: URL? = loader.getResource(packagePath)
    return if (url != null) {
      when (url.protocol) {
        "file" -> getClassNameByFile(url.path, packageName, childPackage)
        "jar" -> getClassNameByJar(url.path, childPackage)
        else -> listOf()
      }
    } else {
      getClassNameByJars((loader as URLClassLoader).urLs, packagePath, childPackage)
    }
  }

  /**
   * 从项目文件获取某包下所有类
   * @param filePath 文件路径
   * @param childPackage 是否遍历子包
   * @return 类的完整名称
   */
  private fun getClassNameByFile(filePath: String, basePackage: String, childPackage: Boolean): List<String> {
    val myClassName: MutableList<String> = ArrayList()
    val file = File(filePath)
    val childFiles: Array<File> = file.listFiles()!!
    for (childFile in childFiles) {
      if (childFile.isDirectory) {
        if (childPackage) {
          myClassName.addAll(
            getClassNameByFile(
              childFile.path,
              "$basePackage.${childFile.path.substringAfterLast(File.separator)}",
              childPackage
            )
          )
        }
      } else {
        val childFilePath: String = childFile.path
        if (childFilePath.endsWith(".class")) {
          myClassName.add("$basePackage.${childFilePath.substringAfterLast(File.separator).substringBeforeLast('.')}")
        }
      }
    }
    return myClassName
  }

  /**
   * 从jar获取某包下所有类
   * @param jarPath jar文件路径
   * @param childPackage 是否遍历子包
   * @return 类的完整名称
   */
  private fun getClassNameByJar(jarPath: String, childPackage: Boolean): List<String> {
    val myClassName: MutableList<String> = ArrayList()
    val jarInfo = jarPath.split("!").toTypedArray()
    val jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"))
    val packagePath = jarInfo[1].substring(1)
    try {
      val jarFile = JarFile(jarFilePath)
      val entries = jarFile.entries()
      while (entries.hasMoreElements()) {
        val jarEntry = entries.nextElement()
        var entryName = jarEntry.name
        if (entryName.endsWith(".class")) {
          if (childPackage) {
            if (entryName.startsWith(packagePath)) {
              entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."))
              myClassName.add(entryName)
            }
          } else {
            val index = entryName.lastIndexOf("/")
            var myPackagePath: String
            myPackagePath = if (index != -1) {
              entryName.substring(0, index)
            } else {
              entryName
            }
            if (myPackagePath == packagePath) {
              entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."))
              myClassName.add(entryName)
            }
          }
        }
      }
    } catch (e: Exception) {
    }
    return myClassName
  }

  /**
   * 从所有jar中搜索该包，并获取该包下所有类
   * @param urls URL集合
   * @param packagePath 包路径
   * @param childPackage 是否遍历子包
   * @return 类的完整名称
   */
  private fun getClassNameByJars(urls: Array<URL>, packagePath: String, childPackage: Boolean): List<String> {
    val myClassName = ArrayList<String>()
    for (i in urls.indices) {
      val url: URL = urls[i]
      val urlPath: String = url.path
      // 不必搜索classes文件夹
      if (urlPath.endsWith("classes/")) {
        continue
      }
      val jarPath = "$urlPath/$packagePath"
      myClassName.addAll(getClassNameByJar(jarPath, childPackage))
    }
    return myClassName
  }
}