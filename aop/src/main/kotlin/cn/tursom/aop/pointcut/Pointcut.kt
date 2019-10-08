package cn.tursom.aop.pointcut

import java.lang.reflect.Method

interface Pointcut {
	fun matchClass(clazz: Class<*>): Boolean
	fun matchMethod(method: Method): Boolean
}

