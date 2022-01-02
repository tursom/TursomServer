package cn.tursom.database.mybatisplus

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare
import com.baomidou.mybatisplus.core.conditions.interfaces.Func
import com.baomidou.mybatisplus.core.conditions.interfaces.Join
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested

@MybatisPlusEnhanceDslMaker
interface KtEnhanceWrapper<T, W : AbstractWrapper<T, String, out W>, EnhanceWrapper : Wrapper<T>> :
  EnhanceCompare<T, W, EnhanceWrapper>,
  JoinEnhance<EnhanceWrapper>,
  FuncEnhance<T, EnhanceWrapper>,
  Compare<W, String>,
  Nested<W, W>,
  Join<W>,
  Func<W, String>