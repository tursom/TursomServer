package cn.tursom.core.xml.interfaces

interface ElementContainerPotableXmlElement : ElementContainerXmlElement, PotableXmlElement {
	val size: Int

	fun addSubElement(element: XmlElement): ElementContainerPotableXmlElement
	operator fun plus(element: XmlElement): ElementContainerPotableXmlElement = addSubElement(element)
	operator fun XmlElement.unaryPlus() = addSubElement(this)

	fun removeElement(index: Int): ElementContainerPotableXmlElement
	operator fun minus(index: Int) = removeElement(index)
}