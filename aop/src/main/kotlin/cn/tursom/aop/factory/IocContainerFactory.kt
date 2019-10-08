package cn.tursom.aop.factory

import cn.tursom.aop.advice.Advice
import cn.tursom.aop.aspect.DefaultAspect
import cn.tursom.aop.ioc.DefaultIocContainer
import cn.tursom.aop.ioc.IocContainer
import cn.tursom.aop.pointcut.Pointcut
import cn.tursom.aop.pointcut.RegexPointcut
import cn.tursom.utils.xml.Xml
import org.dom4j.Element
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
object IocContainerFactory {
	/**
	 * example:
	 * <IOC>
	 *   <bean class="cn.tursom.Example1" id="exampleBean1"/>
	 *   <bean class="cn.tursom.Example2" id="exampleBean2">
	 *     <property name="propertyField" value="test"/>
	 *     <ref name="refField" id="exampleBean1" />
	 *     <bean name="beanField" class="cn.tursom.BeanField" />
	 *   </bean>
	 *   <aspect>
	 *     <advice class="cn.tursom.AdviceExample" />
	 *     <advice class="cn.tursom.AdviceExample2" />
	 *     <RegexPointcut class="cn\.tursom\.aop\..*" method=".*" />
	 *     <pointcut pointcut="RegexPointcut" class="cn\.tursom\.aop\.ioc\..*" method=".*" />
	 *   </aspect>
	 * </IOC>
	 */
	fun loadFromXml(path: String): IocContainer = loadFromXml(path, Thread.currentThread().contextClassLoader)

	fun loadFromXml(path: String, classLoader: ClassLoader): IocContainer {
		val element: Element = Xml.saxReader.read(File(path)).rootElement
		val iocContainer = DefaultIocContainer()
		element.elements("bean").forEach {
			if (it !is Element) return@forEach
			val id = it.attribute("id").value
			val bean = getXmlBean(it, classLoader, iocContainer)
			iocContainer.addBean(id, bean)
		}
		element.elements("aspect").forEach {
			if (it !is Element) return@forEach
			val advices = getXmlAdvices(it, classLoader)
			val pointcuts = getXmlPointCuts(it)
			advices.forEach { advice ->
				pointcuts.forEach { pointcut ->
					iocContainer.addAspect(DefaultAspect(pointcut, advice))
				}
			}
		}
		return iocContainer
	}

	fun getXmlPointCuts(root: Element): List<Pointcut> {
		val pointcuts = ArrayList<Pointcut>()
		root.elements("RegexPointcut").forEach {
			if (it !is Element) return@forEach
			val clazz = it.attributeValue("class")
			val method = it.attributeValue("method")
			pointcuts.add(RegexPointcut(clazz, method))
		}
		root.elements().forEach {
			if (it !is Element) return@forEach
			val clazz = it.attributeValue("class")
			val method = it.attributeValue("method")
			pointcuts.add(RegexPointcut(clazz, method))
		}
		return pointcuts
	}

	fun getXmlAdvices(root: Element, classLoader: ClassLoader): List<Advice> {
		val advices = ArrayList<Advice>()
		root.elements("advice").forEach {
			if (it !is Element) return@forEach
			val clazzName = it.attributeValue("class")
			val clazz = classLoader.loadClass(clazzName)
			advices.add(clazz.newInstance() as Advice)
		}
		return advices
	}

	fun getXmlBean(root: Element, classLoader: ClassLoader, iocContainer: IocContainer): Any {
		val clazz = classLoader.loadClass(root.attribute("class").value)
		val bean = clazz.newInstance()
		root.elements()?.forEach subForEach@{ sub ->
			if (sub !is Element) return@subForEach
			when (sub.name) {
				"property" -> {
					val name = sub.attributeValue("name")
					val value = sub.attributeValue("value")
					val field = clazz.getDeclaredField(name)
					field.isAccessible = true
					field.set(bean, value)
				}
				"ref" -> {
					val name = sub.attributeValue("name")
					val refId = sub.attributeValue("id")
					val field = clazz.getDeclaredField(name)
					field.isAccessible = true
					field.set(bean, iocContainer[refId])
				}
				"bean" -> {
					val name = sub.attributeValue("name")
					val field = clazz.getDeclaredField(name)
					field.isAccessible = true
					field.set(bean, getXmlBean(sub, classLoader, iocContainer))
				}
			}
		}
		return bean
	}

	/**
	 * example:
	 * {
	 * 	"beans": [
	 * 		{
	 * 			"id": "exampleBean1",
	 * 			"class": "cn.tursom.Example1"
	 * 		},
	 * 		{
	 * 			"id": "exampleBean2",
	 * 			"class": "cn.tursom.Example2",
	 * 			"properties": [
	 * 				{
	 * 					"style": "property",
	 * 					"name": "propertyField",
	 * 					"value": "test"
	 * 				},
	 * 				{
	 * 					"style": "ref",
	 * 					"name": "refField",
	 * 					"id": "exampleBean1"
	 * 				},
	 * 				{
	 * 					"style": "bean",
	 * 					"id": "refFiled",
	 * 					"class": "cn.tursom.BeanField"
	 * 				}
	 * 			]
	 * 		}
	 * 	]
	 * }
	 */
	fun loadFromJson(json: String): IocContainer {
		TODO()
	}
}