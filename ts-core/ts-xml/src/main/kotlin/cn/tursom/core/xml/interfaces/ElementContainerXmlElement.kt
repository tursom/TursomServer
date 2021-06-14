package cn.tursom.core.xml.interfaces

interface ElementContainerXmlElement : XmlElement {
	val subElement: List<XmlElement>
}