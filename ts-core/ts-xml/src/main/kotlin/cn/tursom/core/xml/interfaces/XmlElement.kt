package cn.tursom.core.xml.interfaces

interface XmlElement {
  val name: String
  val attribute: Map<String, String>?

  fun toString(
    builder: StringBuilder,
    indentation: String,
    advanceIndentation: String
  ): String {
    builder.append(indentation)
    when (this) {
      is TextXmlElement -> {
        builder.append(
          if (text.isEmpty())
            "<${getElementHead()} />"
          else
            "<${getElementHead()}>${text}</${name}>"
        )
      }
      is ElementContainerXmlElement -> {
        if (subElement.isEmpty()) {
          builder.append("<${getElementHead()}></${getElementHead()}>")
        } else {
          builder.append("<${getElementHead()}>\n")
          subElement.forEach {
            it.toString(builder, indentation + advanceIndentation, advanceIndentation)
          }
          builder.append("$indentation</${name}>")
        }
      }
      else -> {
        builder.append("<${getElementHead()} />")
      }
    }
    builder.append('\n')
    return builder.toString()
  }

  private fun getElementHead(): String {
    val attribute = attribute ?: return name
    val sb = StringBuilder(name)
    attribute.forEach { (t, u) ->
      var value = u
      if (value.contains('&')) value = value.replace("&", "&amp;")
      if (value.contains('"')) value = value.replace("\"", "&quot;")
      sb.append(" $t=\"$value\"")
    }
    return sb.toString()
  }
}

