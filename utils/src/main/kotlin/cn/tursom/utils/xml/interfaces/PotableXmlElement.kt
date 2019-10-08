package cn.tursom.utils.xml.interfaces

interface PotableXmlElement : XmlElement {
	override var name: String
	fun setAttribute(key: String, value: String)
	operator fun set(key: String, value: String) = setAttribute(key, value)
	operator fun String.rangeTo(value: String) = setAttribute(this, value)
}