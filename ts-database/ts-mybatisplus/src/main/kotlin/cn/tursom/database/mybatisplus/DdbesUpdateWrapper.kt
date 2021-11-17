package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper

class DdbesUpdateWrapper<T>(
  override val enhanceEntityClass: Class<T>,
) : UpdateWrapper<T>(),
  UpdateEnhance<T, DdbesUpdateWrapper<T>>,
  DdbesWrapperEnhance<T, UpdateWrapper<T>, DdbesUpdateWrapper<T>> {
  companion object {
    inline operator fun <reified T> invoke() = DdbesUpdateWrapper(T::class.java)
  }
}