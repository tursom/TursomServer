package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper

class KtEnhanceQueryWrapper<T>(
  override var enhanceEntityClass: Class<T>,
) : QueryWrapper<T>(),
  EnhanceQuery<T, KtEnhanceQueryWrapper<T>>,
  KtEnhanceWrapper<T, QueryWrapper<T>, KtEnhanceQueryWrapper<T>> {
  init {
    this.entityClass = enhanceEntityClass
  }

  companion object {
    inline operator fun <reified T> invoke() = KtEnhanceQueryWrapper(T::class.java)
  }
}

