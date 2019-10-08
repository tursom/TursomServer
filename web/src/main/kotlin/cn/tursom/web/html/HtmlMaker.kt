package cn.tursom.web.html

import cn.tursom.utils.xml.XmlDocument
import cn.tursom.utils.xml.interfaces.ElementContainerPotableXmlElement
import cn.tursom.utils.xml.interfaces.TextPotableXmlElement
import cn.tursom.utils.xml.interfaces.TextXmlElement
import cn.tursom.utils.xml.interfaces.XmlElement

//TODO
class HtmlMaker {
	val stringBuilder = StringBuilder()

	val head: ElementContainerPotableXmlElement = XmlDocument.ElementContainer("head")
	val body: ElementContainerPotableXmlElement = XmlDocument.ElementContainer("body")

	fun html5(): ElementContainerPotableXmlElement {
		stringBuilder.append("<!DOCTYPE html>")
		return XmlDocument.ElementContainer("body")
	}

	fun title(title: String): TextXmlElement = XmlDocument.text("title", title)

	operator fun invoke(
		advanceIndentation: String = "  ",
		indentation: String = "",
		action: HtmlMaker.() -> XmlElement
	): String {
		val element = this.action()
		return element.toString(stringBuilder, indentation, advanceIndentation)
	}

	companion object {
		fun tag(name: String? = null, action: TextPotableXmlElement.() -> Unit) = XmlDocument.tag(name, action)
		fun text(name: String? = null, action: TextPotableXmlElement.() -> String?) = XmlDocument.text(name, action)
		fun subElement(
			name: String? = null,
			action: ElementContainerPotableXmlElement.() -> Unit
		) = XmlDocument.subElement(name, action)

		val br: XmlElement = tag("br") { }
	}
}

fun main() {
	println(HtmlMaker().invoke {
		html5().apply {
			+head.apply {
				+title("hello")
			}
			+body.apply {
			}
		}
	})
}