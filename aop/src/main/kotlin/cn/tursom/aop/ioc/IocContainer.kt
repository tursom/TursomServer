package cn.tursom.aop.ioc

import cn.tursom.aop.aspect.Aspect

interface IocContainer {
	val aspects: Set<Aspect>

	fun addAspect(aspect: Aspect)
	fun addBeanDefinition(beanName: String, clazz: Class<*>)
	fun addBean(beanName: String, bean: Any)
	operator fun set(beanName: String, bean: Any) = addBean(beanName, bean)
	fun getBean(beanName: String): Any
	@Suppress("UNCHECKED_CAST")
	operator fun <T> get(beanName: String): T = getBean(beanName) as T
}

