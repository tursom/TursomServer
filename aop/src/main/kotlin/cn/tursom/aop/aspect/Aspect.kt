package cn.tursom.aop.aspect

import cn.tursom.aop.advice.Advice
import cn.tursom.aop.pointcut.Pointcut

interface Aspect {
	val pointcut: Pointcut
	val advice: Advice
}

