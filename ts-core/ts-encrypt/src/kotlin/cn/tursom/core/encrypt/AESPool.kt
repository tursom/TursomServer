package cn.tursom.core.encrypt

class AESPool(
  initSize: Int = 16
) : EncryptPool<AES>(initSize, { AES() })