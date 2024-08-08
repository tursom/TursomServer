package cn.tursom.web.router.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SimpleRouterTest {
  @Test
  fun testAddSubRoute() {
    // 创建 SimpleRouter 实例
    val router = SimpleRouter<String>()

    // 添加路由
    router.addSubRoute("/test", "testValue", null)

    // 获取路由
    val result = router.get("/test")

    // 验证结果
    assertEquals("testValue", result.first)
  }

  @Test
  fun testDelRoute() {
    // 创建 SimpleRouter 实例
    val router = SimpleRouter<String>()

    // 添加路由
    router.addSubRoute("/test", "testValue", null)

    // 删除路由
    router.delRoute("/test")

    // 获取路由
    val result = router.get("/test")

    // 验证结果
    assertNull(result.first)
  }
}
