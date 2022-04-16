package cn.tursom.core.encrypt

class RSAPool(
  initSize: Int = 16,
) : EncryptPool<RSA>(initSize, { RSA() })