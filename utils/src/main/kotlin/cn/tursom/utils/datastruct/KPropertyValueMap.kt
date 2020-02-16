package cn.tursom.utils.datastruct

import cn.tursom.core.cast
import cn.tursom.core.datastruct.ReadWriteMap
import cn.tursom.core.datastruct.SoftArrayMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class KPropertyValueMap(val target: Any) : Map<String, Any?> {
    private val propertyMap = Companion[target]
    override val entries: Set<Map.Entry<String, Any?>> = KPropertyEntries(target, propertyMap)
    override val keys: Set<String> get() = propertyMap.keys
    override val size: Int get() = propertyMap.size
    override val values: Collection<Any?> = KPropertyValueCollection(target, propertyMap)
    override fun containsKey(key: String): Boolean = propertyMap.containsKey(key)
    override fun containsValue(value: Any?): Boolean = values.contains(value)
    override fun get(key: String): Any? = propertyMap[key]?.get(target)
    override fun isEmpty(): Boolean = propertyMap.isEmpty()

    companion object {
        private val propertiesMapCache = ReadWriteMap(HashMap<KClass<*>, SoftArrayMap<String, KProperty1<Any, *>>>())
        operator fun get(obj: Any) = get(obj::class)
        operator fun get(clazz: KClass<*>): SoftArrayMap<String, KProperty1<Any, *>> {
            var map = propertiesMapCache[clazz]
            if (map == null) {
                map = clazz.memberProperties.cast<Collection<KProperty1<Any, *>>>().let { properties ->
                    val valueMap = SoftArrayMap<String, KProperty1<Any, *>>(properties.size)
                    properties.forEach {
                        it.isAccessible = true
                        valueMap[it.name] = it
                    }
                    valueMap
                }
                propertiesMapCache[clazz] = map
            }
            return map
        }
    }
}

