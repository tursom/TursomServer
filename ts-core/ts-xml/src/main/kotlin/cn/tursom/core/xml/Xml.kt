package cn.tursom.core.xml

import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader
import sun.misc.Unsafe
import java.io.File
import java.io.StringReader
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Xml {
  val saxReader = SAXReader()

  //利用Unsafe绕过构造函数获取变量
  private val unsafe by lazy {
    val field = Unsafe::class.java.getDeclaredField("theUnsafe")
    //允许通过反射设置属性的值
    field.isAccessible = true
    field.get(null) as Unsafe
  }

  private val parseSet = setOf(
    Byte::class.java,
    Short::class.java,
    Int::class.java,
    Long::class.java,
    Float::class.java,
    Double::class.java,
    Boolean::class.java,
    Char::class.java,
    String::class.java,

    java.lang.Byte::class.java,
    java.lang.Short::class.java,
    java.lang.Integer::class.java,
    java.lang.Long::class.java,
    java.lang.Float::class.java,
    java.lang.Double::class.java,
    java.lang.Boolean::class.java,
    Character::class.java,
    java.lang.String::class.java
  )

  private val Class<*>.defaultTarget
    get() = getAnnotation(DefaultTarget::class.java)?.target ?: ElementTarget.SubElement

  private val Class<*>.elementName: String
    get() = getAnnotation(ElementName::class.java)?.name ?: if (isArray) componentType.name else name

  private val Class<*>.textField
    get() = declaredFields.find {
      it.getAnnotation(Ignore::class.java) == null &&
        it.target ?: defaultTarget == ElementTarget.ElementText
    }

  private val Class<*>.attributeField
    get() = declaredFields.filter {
      it.getAnnotation(Ignore::class.java) == null &&
        it.target ?: defaultTarget == ElementTarget.Attribute
    }

  private val Class<*>.subElementField
    get() = declaredFields.filter {
      it.getAnnotation(Ignore::class.java) == null &&
        it.target ?: defaultTarget == ElementTarget.SubElement
    }

  private val Class<*>.isJustValue
    get() = parseSet.contains(this) || isEnum

  private val Field.elementName: String
    get() = getAnnotation(FieldName::class.java)?.name ?: name

  private val Field.target: ElementTarget?
    get() = when {
      getAnnotation(Attribute::class.java) != null -> ElementTarget.Attribute
      getAnnotation(ElementText::class.java) != null -> ElementTarget.ElementText
      getAnnotation(SubElement::class.java) != null -> ElementTarget.SubElement
      else -> null
    }

  private fun Class<*>.parse(value: String?, element: Element, fieldName: String? = null): Any? {
    return when {
      parseSet.contains(this) -> when (this) {
        Byte::class.java -> value?.toByteOrNull()
        Short::class.java -> value?.toShortOrNull()
        Int::class.java -> value?.toIntOrNull()
        Long::class.java -> value?.toLongOrNull()
        Float::class.java -> value?.toFloatOrNull()
        Double::class.java -> value?.toDoubleOrNull()
        Boolean::class.java -> value?.toBoolean()
        Char::class.java -> value?.toIntOrNull()?.toChar()
        String::class.java -> value

        java.lang.Byte::class.java -> value?.toByteOrNull()
        java.lang.Short::class.java -> value?.toShortOrNull()
        Integer::class.java -> value?.toIntOrNull()
        java.lang.Long::class.java -> value?.toLongOrNull()
        java.lang.Float::class.java -> value?.toFloatOrNull()
        java.lang.Double::class.java -> value?.toDoubleOrNull()
        java.lang.Boolean::class.java -> value?.toBoolean()
        java.lang.Character::class.java -> value?.toIntOrNull()?.toChar()
        java.lang.String::class.java -> value

        else -> null
      }
      isEnum -> {
        val valueOf = getDeclaredMethod("valueOf", String::class.java)
        try {
          valueOf(null, value ?: return null)
        } catch (e: Exception) {
          null
        }
      }
      else -> parse(this, if (fieldName != null) element.element(fieldName) ?: return null else element)
    }
  }

  private fun Field.parse(target: ElementTarget, element: Element, fieldName: String): Any? {
    return if (type.isArray) {
      val list = if (getAnnotation(Vararg::class.java) != null) {
        element.elements(fieldName)
      } else {
        element.element(fieldName).elements()
      }
      val array = Array.newInstance(type.componentType, list.size)
      list.forEachIndexed { index, any ->
        any as Element
        Array.set(array, index, type.componentType.parse(any.text ?: return null, any))
      }
      array
    } else {
      type.parse(getData(element, fieldName, target), element, fieldName)
    }
  }

  fun getData(element: Element, name: String, target: ElementTarget): String? = when (target) {
    ElementTarget.Attribute -> element.attribute(name)?.value
    ElementTarget.ElementText -> element.text
    ElementTarget.SubElement -> element.element(name)?.text
  }

  @Suppress("UNCHECKED_CAST")
  fun <T> parse(clazz: Class<T>, root: Element): T {
    val defaultTarget = clazz.defaultTarget
    val instance = unsafe.allocateInstance(clazz) as T

    clazz.declaredFields.forEach { field ->
      if (field.getAnnotation(Ignore::class.java) != null) return@forEach

      field.isAccessible = true

      val target = field.target ?: defaultTarget
      val fieldName = field.getAnnotation(FieldName::class.java)?.name ?: field.name

      val constructor = field.getAnnotation(Constructor::class.java)
      val value = if (constructor != null) {
        val advanceSetMethod = try {
          clazz.getDeclaredMethod(constructor.constructor, Element::class.java)
        } catch (e: NoSuchMethodException) {
          null
        }
        if (advanceSetMethod != null) {
          advanceSetMethod.isAccessible = true
          advanceSetMethod(instance, root)
        } else {
          val setMethod = clazz.getDeclaredMethod(constructor.constructor, String::class.java)
          setMethod.isAccessible = true
          setMethod(instance, getData(root, fieldName, target))
        }
      } else {
        field.parse(target, root, fieldName)
      } ?: return@forEach
      field.set(instance, value)
    }

    return instance
  }

  fun <T> parse(clazz: Class<T>, document: Document): T = parse(clazz, document.rootElement)
  fun <T> parse(clazz: Class<T>, document: String): T = parse(clazz, saxReader.read(StringReader(document)))
  fun <T> parse(clazz: Class<T>, url: URL): T = parse(clazz, saxReader.read(url))
  fun <T> parse(clazz: Class<T>, file: File): T = parse(clazz, saxReader.read(file))

  inline fun <reified T : Any> parse(root: Element): T = parse(T::class.java, root)
  inline fun <reified T : Any> parse(document: Document): T = parse(T::class.java, document.rootElement)
  inline fun <reified T : Any> parse(document: String): T = parse(T::class.java, saxReader.read(StringReader(document)))
  inline fun <reified T : Any> parse(url: URL): T = parse(T::class.java, saxReader.read(url))
  inline fun <reified T : Any> parse(file: File): T = parse(T::class.java, saxReader.read(file))

  fun Field.toXml(
    obj: Any,
    value: Any,
    elementName: String,
    builder: StringBuilder,
    indentation: String,
    advanceIndentation: String,
  ): Boolean {
    val clazz = obj.javaClass
    getAnnotation(ToXml::class.java)?.let { getter ->
      val method = try {
        clazz.getDeclaredMethod(
          getter.callback,
          type,
          String::class.java,
          String::class.java,
          String::class.java
        )
      } catch (e: NoSuchMethodException) {
        null
      }
      if (method != null) {
        method.isAccessible = true
        builder.append("\n")
        builder.append(indentation)
        builder.append(method(obj, value, elementName, indentation, advanceIndentation) ?: return false)
        return true
      }
      val method2 = try {
        clazz.getDeclaredMethod(
          getter.callback,
          type,
          String::class.java,
          StringBuilder::class.java,
          String::class.java,
          String::class.java
        )
      } catch (e: NoSuchMethodException) {
        null
      }
      if (method2 != null) {
        method2.isAccessible = true
        builder.append("\n")
        builder.append(indentation)
        method2(obj, value, elementName, builder, indentation, advanceIndentation)
        return true
      }
      val method3 = try {
        clazz.getDeclaredMethod(
          getter.callback,
          type,
          String::class.java
        )
      } catch (e: NoSuchMethodException) {
        null
      }
      if (method3 != null) {
        method3.isAccessible = true
        builder.append("\n")
        builder.append(indentation)
        builder.append(method3(obj, value, elementName) ?: return false)
      }
      val method4 = try {
        clazz.getDeclaredMethod(
          getter.callback,
          type,
          String::class.java,
          StringBuilder::class.java
        )
      } catch (e: NoSuchMethodException) {
        null
      }
      if (method4 != null) {
        method4.isAccessible = true
        builder.append("\n")
        builder.append(indentation)
        method4(obj, value, elementName, builder)
      }
      return true
    }
    return false
  }


  fun arrayXml(
    obj: kotlin.Array<*>,
    rootName: String = obj.javaClass.elementName,
    indentation: String = "    ",
    fieldName: String? = "i",
  ): String {
    val stringBuilder = StringBuilder()
    arrayXml(obj, rootName, stringBuilder, "", indentation, fieldName)
    return stringBuilder.toString()
  }

  fun arrayXmlOnce(
    obj: kotlin.Array<*>,
    value: Any,
    subElementName: String,
    builder: StringBuilder,
    subIndentation: String,
    advanceIndentation: String,
  ) {
    val type = value.javaClass

    if (type.getAnnotation(CompressionXml::class.java) != null) {
      builder.append("\n")
      builder.append(subIndentation)
      arrayXmlCom(obj, subElementName, builder)
      return
    }

    if ((value.javaClass.isJustValue))
      builder.append("\n$subIndentation<$subElementName>$value</$subElementName>")
    else {
      builder.append("\n")
      toXml(value, subElementName, builder, subIndentation, advanceIndentation)
    }
  }

  fun arrayXml(
    obj: kotlin.Array<*>,
    elementName: String,
    builder: StringBuilder,
    indentation: String,
    advanceIndentation: String,
    fieldName: String? = "i",
    multipleField: Boolean = false,
  ) {
    if (obj.isEmpty()) return
    val clazz = obj.javaClass
    if (clazz.isArray) {
      val subIndentation = "$advanceIndentation$indentation"
      if (multipleField) {
        if (builder.isNotEmpty()) builder.deleteCharAt(builder.length - 1)
        obj.forEach { value ->
          arrayXmlOnce(obj, value ?: return@forEach, elementName, builder, indentation, advanceIndentation)
        }
      } else {
        builder.append("$indentation<$elementName>")
        (0 until Array.getLength(obj)).forEach { i ->
          val value = Array.get(obj, i) ?: return@forEach
          val type = value.javaClass
          val subElementName = fieldName ?: type.elementName
          arrayXmlOnce(obj, value, subElementName, builder, subIndentation, advanceIndentation)
        }
        builder.append("\n$indentation</$elementName>")
      }
    }
  }


  fun iterableXml(
    obj: Iterable<*>,
    rootName: String,
    indentation: String,
    fieldName: String? = "i",
  ): String {
    val stringBuilder = StringBuilder()
    iterableXml(obj, rootName, stringBuilder, "", indentation, fieldName)
    return stringBuilder.toString()
  }

  fun iterableXml(
    obj: Iterable<*>,
    elementName: String,
    builder: StringBuilder,
    indentation: String,
    advanceIndentation: String,
    fieldName: String? = "i",
  ) {
    val subIndentation = "$advanceIndentation$indentation"
    builder.append("$indentation<$elementName>")
    obj.forEach {
      val type = (it ?: return@forEach).javaClass

      if (type.getAnnotation(CompressionXml::class.java) != null) {
        builder.append("\n")
        builder.append(subIndentation)
        iterableXmlCom(obj, elementName, builder)
        return@forEach
      }

      val subElementName = fieldName ?: type.elementName
      if (type.isJustValue)
        builder.append("\n$subIndentation<$subElementName>$it</$subElementName>")
      else {
        builder.append("\n")
        toXml(it, subElementName, builder, subIndentation, advanceIndentation)
      }
    }
    builder.append("\n$indentation</$elementName>")
  }

  fun mapXml(
    obj: Map<*, *>,
    rootName: String,
    indentation: String,
  ): String {
    val stringBuilder = StringBuilder()
    mapXml(obj, rootName, stringBuilder, "", indentation)
    return stringBuilder.toString()
  }

  fun mapXml(
    obj: Map<*, *>,
    elementName: String,
    builder: StringBuilder,
    indentation: String,
    advanceIndentation: String,
  ) {
    val subIndentation = "$advanceIndentation$indentation"
    builder.append("$indentation<$elementName>")
    obj.forEach { (k, v) ->
      val type = (v ?: return@forEach).javaClass

      if (type.getAnnotation(CompressionXml::class.java) != null) {
        builder.append("\n")
        builder.append(subIndentation)
        mapXmlCom(obj, elementName, builder)
        return@forEach
      }

      if (type.isJustValue)
        builder.append("\n$subIndentation<$k>$v</$k>")
      else {
        builder.append("\n")
        toXml(v, (k ?: return@forEach).toString(), builder, subIndentation, advanceIndentation)
      }
    }
    builder.append("\n$indentation</$elementName>")
  }

  fun normalXml(
    obj: Any,
    elementName: String,
    builder: StringBuilder,
    indentation: String,
    advanceIndentation: String,
  ) {
    val clazz = obj.javaClass
    val textField = clazz.textField
    val attributeField = clazz.attributeField
    val subElement = clazz.subElementField
    val subIndentation = "$advanceIndentation$indentation"

    builder.append("$indentation<$elementName")
    attributeField.forEach {
      it.isAccessible = true
      val value = it.get(obj) ?: return@forEach
      builder.append(" ${it.elementName}=\"$value\"")
    }

    when {
      textField != null && subElement.isEmpty() -> {
        builder.append(if (attributeField.isEmpty()) "/>" else "\n$indentation/>")
        return
      }
      else -> builder.append(">")
    }

    subElement.forEach {
      it.isAccessible = true
      val value = it.get(obj) ?: return@forEach
      val eleName = it.elementName

      if (it.toXml(obj, value, eleName, builder, subIndentation, advanceIndentation))
        return@forEach

      if (it.getAnnotation(CompressionXml::class.java) != null) {
        builder.append("\n")
        builder.append(subIndentation)
        toXmlCom(value, eleName, builder, it)
        return@forEach
      }

      if (it.type.isJustValue) {
        builder.append("\n$subIndentation<$eleName>$value</$eleName>")
        return@forEach
      }

      builder.append("\n")
      toXml(value, eleName, builder, subIndentation, advanceIndentation, it)
    }

    if (textField != null) run {
      textField.isAccessible = true
      val value = textField.get(obj) ?: return@run
      builder.append(value)
      builder.append("</$elementName>")
    } else
      builder.append("\n$indentation</$elementName>")
  }

  fun toXml(
    obj: Any,
    elementName: String,
    builder: StringBuilder,
    indentation: String,
    advanceIndentation: String,
    field: Field? = null,
  ) {
    try {
      obj as Pair<*, *>
      builder.append("$indentation<${obj.first}>${obj.second}</${obj.first}>")
      return
    } catch (e: ClassCastException) {
    }

    val clazz = obj.javaClass

    if (clazz.getAnnotation(CompressionXml::class.java) != null) {
      toXmlCom(obj, elementName, builder, field)
      return
    }

    if (clazz.isArray) {
      arrayXml(
        obj as kotlin.Array<*>,
        elementName,
        builder,
        indentation,
        advanceIndentation,
        "a",
        field?.getAnnotation(Vararg::class.java) != null
      )
      return
    }

    try {
      mapXml(obj as Map<*, *>, elementName, builder, indentation, advanceIndentation)
      return
    } catch (e: ClassCastException) {
    }

    try {
      iterableXml(obj as Iterable<*>, elementName, builder, indentation, advanceIndentation)
      return
    } catch (e: ClassCastException) {
    }

    normalXml(obj, elementName, builder, indentation, advanceIndentation)
  }

  fun toXml(obj: Any, rootName: String = obj.javaClass.elementName, indentation: String = "    "): String {
    val stringBuilder = StringBuilder()
    toXml(obj, rootName, stringBuilder, "", indentation)
    return stringBuilder.toString()
  }

  fun arrayXmlComOnce(value: Any, fieldName: String?, builder: StringBuilder) {
    val type = value.javaClass
    val subElementName = fieldName ?: type.elementName
    if (type.isJustValue)
      builder.append("<$subElementName>$value</$subElementName>")
    else {
      toXmlCom(value, subElementName, builder)
    }
  }

  fun arrayXmlCom(
    obj: kotlin.Array<*>,
    elementName: String,
    builder: StringBuilder,
    fieldName: String? = "i",
    multi: Boolean = false,
  ) {
    val clazz = obj.javaClass
    if (clazz.isArray) {
      if (multi) {
        obj.forEach {
          arrayXmlComOnce(it ?: return@forEach, elementName, builder)
        }
      } else {
        builder.append("<$elementName>")

        obj.forEach {
          arrayXmlComOnce(it ?: return@forEach, fieldName, builder)
        }
        builder.append("</$elementName>")
      }
      return
    }
  }

  fun iterableXmlCom(obj: Iterable<*>, elementName: String, builder: StringBuilder, fieldName: String? = "i") {
    builder.append("<$elementName>\n")
    obj.forEach {
      val type = (it ?: return@forEach).javaClass
      val subElementName = fieldName ?: type.elementName
      if (type.isJustValue)
        builder.append("<$subElementName>$it</$subElementName>")
      else {
        toXmlCom(it, subElementName, builder)
      }
    }
    builder.append("</$elementName>")
  }

  fun mapXmlCom(obj: Map<*, *>, elementName: String, builder: StringBuilder) {
    builder.append("<$elementName>")
    obj.forEach { (k, v) ->
      val type = (v ?: return@forEach).javaClass
      if (type.isJustValue)
        builder.append("<$k>$v</$k>")
      else {
        toXmlCom(v, (k ?: return@forEach).toString(), builder)
      }
    }
    builder.append("</$elementName>")
  }

  fun normalXmlCom(obj: Any, elementName: String, builder: StringBuilder) {
    try {
      obj as Pair<*, *>
      builder.append("<${obj.first}>${obj.second}</${obj.first}>")
      return
    } catch (e: ClassCastException) {
    }

    val clazz = obj.javaClass
    val textField = clazz.textField
    val attributeField = clazz.attributeField
    val subElement = clazz.subElementField

    builder.append("<$elementName")
    attributeField.forEach {
      it.isAccessible = true
      builder.append(" ${it.elementName}=\"${it.get(obj)}\"")
    }

    when {
      textField != null && subElement.isEmpty() -> {
        builder.append(" />")
        return
      }
      else -> builder.append(">")
    }


    if (textField != null) {
      textField.isAccessible = true
      val value = textField.get(obj)
      if (value != null) {
        builder.append(value)
      }
    }

    subElement.forEach {
      it.isAccessible = true
      val value = it.get(obj) ?: return@forEach
      val eleName = it.elementName

      it.getAnnotation(ToXml::class.java)?.let { getter ->
        val method3 = try {
          clazz.getDeclaredMethod(
            getter.callback,
            it.type,
            String::class.java
          )
        } catch (e: NoSuchMethodException) {
          null
        }
        if (method3 != null) {
          method3.isAccessible = true
          builder.append(method3(obj, value, eleName) ?: return@let)
          return@forEach
        }

        val method = try {
          clazz.getDeclaredMethod(
            getter.callback,
            it.type,
            String::class.java,
            StringBuilder::class.java
          )
        } catch (e: NoSuchMethodException) {
          return@let
        }
        method.isAccessible = true
        method(obj, value, eleName, builder)
        return@forEach
      }

      if (it.type.isJustValue) {
        builder.append("<$eleName>$value</$eleName>")
        return@forEach
      }

      toXmlCom(value, eleName, builder, it)
    }

    builder.append("</$elementName>")
  }

  fun toXmlCom(obj: Any, elementName: String, builder: StringBuilder, field: Field? = null) {
    val clazz = obj.javaClass
    if (clazz.isArray) {
      arrayXmlCom(
        obj as kotlin.Array<*>,
        elementName, builder,
        "a",
        field?.getAnnotation(Vararg::class.java) != null
      )
      return
    }

    try {
      mapXmlCom(obj as Map<*, *>, elementName, builder)
      return
    } catch (e: ClassCastException) {
    }

    try {
      iterableXmlCom(obj as Iterable<*>, elementName, builder)
      return
    } catch (e: ClassCastException) {
    }

    normalXmlCom(obj, elementName, builder)
  }

  fun toXmlCom(obj: Any, rootName: String = obj.javaClass.elementName): String {
    val stringBuilder = StringBuilder()
    toXmlCom(obj, rootName, stringBuilder)
    return stringBuilder.toString()
  }
}