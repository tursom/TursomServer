package cn.tursom.aop.aspect

import cn.tursom.aop.advice.Advice
import cn.tursom.aop.pointcut.Pointcut

data class DefaultAspect(override val pointcut: Pointcut, override val advice: Advice) : Aspect