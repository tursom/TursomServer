package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.service.IService


inline fun <reified T> IService<T>.update(
    wrapperBuilder: KtEnhanceUpdateWrapper<T>.() -> Unit
): Boolean {
    val wrapper = KtEnhanceUpdateWrapper<T>()
    wrapper.wrapperBuilder()
    return update(wrapper)
}

inline fun <reified T> IService<T>.update(
    queryBuilder: KtEnhanceWrapper<T, UpdateWrapper<T>, KtEnhanceUpdateWrapper<T>>.() -> Unit,
    updateBuilder: EnhanceUpdate<T, KtEnhanceUpdateWrapper<T>>.() -> Unit
): Boolean {
    val wrapper = KtEnhanceUpdateWrapper<T>()
    wrapper.queryBuilder()
    wrapper.updateBuilder()
    return update(wrapper)
}

inline fun <reified T> IService<T>.remove(
    wrapperBuilder: KtEnhanceQueryWrapper<T>.() -> Unit
): Boolean {
    val wrapper = KtEnhanceQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return remove(wrapper)
}

inline fun <reified T> IService<T>.getOne(
    throwEx: Boolean = true,
    wrapperBuilder: KtEnhanceQueryWrapper<T>.() -> Unit
): T {
    val wrapper = KtEnhanceQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return getOne(wrapper, throwEx)
}

inline fun <reified T> IService<T>.count(
    wrapperBuilder: KtEnhanceQueryWrapper<T>.() -> Unit
): Long {
    val wrapper = KtEnhanceQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return count(wrapper)
}

inline fun <reified T> IService<T>.list(
    wrapperBuilder: KtEnhanceQueryWrapper<T>.() -> Unit
): List<T> {
    val wrapper = KtEnhanceQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return list(wrapper)
}

inline fun <reified T, E : IPage<T>> IService<T>.page(
    page: E,
    wrapperBuilder: KtEnhanceQueryWrapper<T>.() -> Unit
): E {
    val wrapper = KtEnhanceQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return page(page, wrapper)
}

inline fun <reified T, E : IPage<Map<String, Any>>> IService<T>.pageMaps(
    page: E,
    wrapperBuilder: KtEnhanceQueryWrapper<T>.() -> Unit
): E {
    val wrapper = KtEnhanceQueryWrapper<T>()
    wrapper.wrapperBuilder()
    return pageMaps(page, wrapper)
}
