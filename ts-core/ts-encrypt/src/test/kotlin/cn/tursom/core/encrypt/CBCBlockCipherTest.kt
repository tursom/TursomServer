package cn.tursom.core.encrypt

import org.junit.Test

class CBCBlockCipherTest {
  private val encrypt = ECC()

  @Test
  fun test() {
    //println(Security.getProviders().map { provider ->
    //  provider.services.map { service ->
    //    service.algorithm
    //  }
    //}.toPrettyJson())
    val bytes = encrypt.sign("test".repeat(1000).toByteArray())
    assert(encrypt.public.verify("test".repeat(1000).toByteArray(), bytes))
  }
}
