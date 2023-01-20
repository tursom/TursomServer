package cn.tursom.proxy.interceptor

import net.sf.cglib.proxy.MethodInterceptor

interface CachedMethodInterceptor : MethodInterceptor {
  fun clearCache()
}