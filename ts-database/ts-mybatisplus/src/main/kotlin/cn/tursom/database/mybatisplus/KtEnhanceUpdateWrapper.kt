package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper

class KtEnhanceUpdateWrapper<T>(
  override val enhanceEntityClass: Class<T>,
) : UpdateWrapper<T>(),
  EnhanceUpdate<T, KtEnhanceUpdateWrapper<T>>,
  KtEnhanceWrapper<T, UpdateWrapper<T>, KtEnhanceUpdateWrapper<T>> {
  companion object {
    inline operator fun <reified T> invoke() = KtEnhanceUpdateWrapper(T::class.java)
  }
}