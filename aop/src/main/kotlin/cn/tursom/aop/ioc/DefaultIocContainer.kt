package cn.tursom.aop.ioc

import cn.tursom.aop.aspect.Aspect
import cn.tursom.aop.ProxyHandler
import java.lang.reflect.Proxy

class DefaultIocContainer : IocContainer {
	private val benDefinitionMap = HashMap<String, Class<*>>()
	private val beanMap = HashMap<String, Any>()
	private val aspectSet: java.util.AbstractSet<Aspect> = HashSet<Aspect>()

	override val aspects: Set<Aspect> get() = aspectSet

	override fun addBeanDefinition(beanName: String, clazz: Class<*>) {
		benDefinitionMap[beanName] = clazz
	}

	override fun addBean(beanName: String, bean: Any) {
		beanMap[beanName] = proxyEnhance(bean)
	}

	override fun getBean(beanName: String): Any {
		return beanMap[beanName] ?: run {
			val bean = createInstance(beanName)
			val proxyBean = proxyEnhance(bean)
			beanMap[beanName] = proxyBean
			proxyBean
		}
	}

	private fun proxyEnhance(bean: Any): Any {
		val clazz = getTopBean(bean).javaClass
		var enhanceBean = bean
		aspectSet.forEach {
			if (it.pointcut.matchClass(clazz)) {
				enhanceBean = ProxyHandler.proxyEnhance(enhanceBean, it)
			}
		}
		return enhanceBean
	}

	private fun createInstance(beanName: String): Any {
		return (benDefinitionMap[beanName] ?: throw NullPointerException()).newInstance()
	}

	override fun addAspect(aspect: Aspect) {
		if (aspectSet.contains(aspect)) return

		aspectSet.add(aspect)
		beanMap.forEach { (t, u) ->
			val bean = getTopBean(u)
			if (aspect.pointcut.matchClass(bean.javaClass)) {
				beanMap[t] = ProxyHandler.proxyEnhance(u, aspect)
			}
		}
	}


	private fun getTopBean(bean: Any): Any {
		return if (Proxy.isProxyClass(bean.javaClass)) {
			val handler = Proxy.getInvocationHandler(bean)
			if (handler is ProxyHandler) handler.getTopBean()
			else bean
		} else {
			bean
		}
	}
}

