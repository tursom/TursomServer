package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.Wrapper

inline fun <C : EnhanceCompare<T, W, Children>, T, W : AbstractWrapper<T, String, W>, Children : Wrapper<T>> C.compare(
  compare: EnhanceCompare<T, W, Children>.() -> Unit,
): C {
  compare()
  return this
}

inline fun <C : FuncEnhance<T, Children>, T, Children : Wrapper<T>> C.func(
  func: FuncEnhance<T, Children>.() -> Unit,
): C {
  func()
  return this
}

inline fun <C : JoinEnhance<Children>, Children> C.join(
  join: JoinEnhance<Children>.() -> Unit,
): C {
  join()
  return this
}

inline fun <C : EnhanceQuery<T, Children>, T, Children : Wrapper<T>> C.query(
  query: EnhanceQuery<T, Children>.() -> Unit,
): C {
  query()
  return this
}

inline fun <C : EnhanceUpdate<T, Children>, T, Children : Wrapper<T>> C.update(
  update: EnhanceUpdate<T, Children>.() -> Unit,
): C {
  update()
  return this
}

inline fun <
  C : KtEnhanceWrapper<T, W, EnhanceWrapper>,
  T, W : AbstractWrapper<T, String, out W>, EnhanceWrapper : Wrapper<T>,
  >
  C.query(
  query: KtEnhanceWrapper<T, W, EnhanceWrapper>.() -> Unit,
): C {
  query()
  return this
}
