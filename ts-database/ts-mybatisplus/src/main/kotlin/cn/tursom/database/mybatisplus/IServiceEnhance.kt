package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.service.IService


inline fun <reified T> IService<T>.update(
    wrapperBuilder: DdbesUpdateWrapper<T>.() -> Unit
): Boolean {
    val wrapper = DdbesUpdateWrapper<T>()
    wrapper.wrapperBuilder()
    return update(wrapper)
}

inline fun <reified T> IService<T>.update(
    queryBuilder: DdbesWrapperEnhance<T, UpdateWrapper<T>, DdbesUpdateWrapper<T>>.() -> Unit,
    updateBuilder: UpdateEnhance<T, DdbesUpdateWrapper<T>>.() -> Unit
): Boolean {
    val wrapper = DdbesUpdateWrapper<T>()
    wrapper.queryBuilder()
    wrapper.updateBuilder()
    return update(wrapper)
}

inline fun <reified T> IService<T>.remove(
    wrapperBuilder: DdbesQueryWrapper<T>.() -> Unit
): Boolean {
    val wrapper = DdbesQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return remove(wrapper)
}

inline fun <reified T> IService<T>.getOne(
    throwEx: Boolean = true,
    wrapperBuilder: DdbesQueryWrapper<T>.() -> Unit
): T {
    val wrapper = DdbesQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return getOne(wrapper, throwEx)
}

inline fun <reified T> IService<T>.count(
    wrapperBuilder: DdbesQueryWrapper<T>.() -> Unit
): Long {
    val wrapper = DdbesQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return count(wrapper)
}

inline fun <reified T> IService<T>.list(
    wrapperBuilder: DdbesQueryWrapper<T>.() -> Unit
): List<T> {
    val wrapper = DdbesQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return list(wrapper)
}

inline fun <reified T, E : IPage<T>> IService<T>.page(
    page: E,
    wrapperBuilder: DdbesQueryWrapper<T>.() -> Unit
): E {
    val wrapper = DdbesQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return page(page, wrapper)
}

inline fun <reified T, E : IPage<Map<String, Any>>> IService<T>.pageMaps(
    page: E,
    wrapperBuilder: DdbesQueryWrapper<T>.() -> Unit
): E {
    val wrapper = DdbesQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return pageMaps(page, wrapper)
}
