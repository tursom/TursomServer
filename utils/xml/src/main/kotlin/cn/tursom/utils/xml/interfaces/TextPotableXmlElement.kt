package cn.tursom.utils.xml.interfaces

interface TextPotableXmlElement : TextXmlElement, PotableXmlElement {
	override var text: String
}