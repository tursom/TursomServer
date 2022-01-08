package cn.tursom.reflect

import java.lang.reflect.Method

class NoargMethodFilter<R>(
    val prevFilter: ReturnTypeMethodFilter<R>,
) : MethodFilter {
    override fun filterMethod(clazz: Class<*>): Sequence<Method> {
        return prevFilter.filterMethod(clazz).filter { method ->
            method.parameterCount == 0
        }
    }

    fun <T> filter(clazz: Class<T>): Sequence<T.() -> R> {
        return filterMethod(clazz).map {
            {
                it.invoke(this) as R
            }
        }
    }

    fun filter(obj: Any): Sequence<() -> R> {
        return filterMethod(obj.javaClass).map {
            {
                it.invoke(obj) as R
            }
        }
    }
}

fun <R> ReturnTypeMethodFilter<R>.noarg() = NoargMethodFilter(this)
