package cn.tursom.core.xml

import cn.tursom.core.xml.interfaces.*

object XmlDocument {
  fun tag(name: String? = null, action: (TextPotableXmlElement.() -> Unit)? = null): TextXmlElement {
    val textXml = TextXml()
    if (name != null) textXml.name = name
    if (action != null) {
      textXml.action()
    }
    return textXml
  }

  fun text(name: String? = null, action: (TextPotableXmlElement.() -> String?)? = null): TextXmlElement {
    val textXml = TextXml()
    if (name != null) textXml.name = name
    val text = action?.let { textXml.it() }
    if (text != null) textXml.text = text
    return textXml
  }

  fun text(name: String? = null, text: String): TextXmlElement {
    val textXml = TextXml()
    if (name != null) textXml.name = name
    textXml.text = text
    return textXml
  }

  fun subElement(
    name: String? = null,
    action: (ElementContainerPotableXmlElement.() -> Unit)? = null,
  ): ElementContainerXmlElement {
    val container = ElementContainer()
    if (name != null) container.name = name
    if (action != null) {
      container.action()
    }
    return container
  }

  operator fun invoke(
    advanceIndentation: String = "  ",
    indentation: String = "",
    action: XmlDocument.() -> XmlElement,
  ): String {
    return this.action().toString(StringBuilder(), indentation, advanceIndentation)
  }

  class TextXml(override var name: String = "Text") : TextPotableXmlElement {
    override var text: String = ""

    override val attribute: HashMap<String, String> = HashMap()

    override fun setAttribute(key: String, value: String) {
      attribute[key] = value
    }
  }

  class ElementContainer(override var name: String = "ElementContainer") : ElementContainerPotableXmlElement {
    override val attribute: HashMap<String, String> = HashMap()
    override val subElement: ArrayList<XmlElement> = ArrayList()
    override val size: Int get() = subElement.size

    override fun addSubElement(element: XmlElement): ElementContainerPotableXmlElement {
      subElement.add(element)
      return this
    }

    override fun setAttribute(key: String, value: String) {
      attribute[key] = value
    }

    override fun removeElement(index: Int): ElementContainerPotableXmlElement {
      subElement.removeAt(index)
      return this
    }
  }

}