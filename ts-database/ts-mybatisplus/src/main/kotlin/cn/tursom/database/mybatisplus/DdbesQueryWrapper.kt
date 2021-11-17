package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper

class DdbesQueryWrapper<T>(
  override var enhanceEntityClass: Class<T>
) : QueryWrapper<T>(),
  QueryEnhance<T, DdbesQueryWrapper<T>>,
  DdbesWrapperEnhance<T, QueryWrapper<T>, DdbesQueryWrapper<T>> {
  init {
    this.entityClass = enhanceEntityClass
  }

  companion object {
    inline operator fun <reified T> invoke() = DdbesQueryWrapper(T::class.java)
  }
}

