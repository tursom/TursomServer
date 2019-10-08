package cn.tursom.utils.xml.interfaces

interface ElementContainerXmlElement : XmlElement {
	val subElement: List<XmlElement>
}