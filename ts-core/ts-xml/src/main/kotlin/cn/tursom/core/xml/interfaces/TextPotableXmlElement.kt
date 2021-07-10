package cn.tursom.core.xml.interfaces

interface TextPotableXmlElement : TextXmlElement, PotableXmlElement {
  override var text: String
}