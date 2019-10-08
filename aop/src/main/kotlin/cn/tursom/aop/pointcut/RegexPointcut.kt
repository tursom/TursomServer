package cn.tursom.aop.pointcut

import java.lang.reflect.Method

data class RegexPointcut(
	private val classRegex: Regex,
	private val methodRegex: Regex
) : Pointcut {
	constructor(classRegex: String, methodRegex: String) : this(classRegex.toRegex(), methodRegex.toRegex())

	override fun matchClass(clazz: Class<*>) = classRegex.matches(clazz.name)
	override fun matchMethod(method: Method) = methodRegex.matches(method.name)
}