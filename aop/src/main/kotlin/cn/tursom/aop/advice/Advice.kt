package cn.tursom.aop.advice

interface Advice {
	operator fun invoke(content: AdviceContent): Any?
}

