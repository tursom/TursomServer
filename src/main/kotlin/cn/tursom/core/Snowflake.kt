package cn.tursom.core

import java.util.concurrent.atomic.AtomicLong

/**
 * 被改造过的雪花ID
 * 时间戳仅在初始化时使用, 与序列号接壤, 这样做可以避免某一时间内大量请求导致的ID爆炸
 * 当前方案在ID溢出时, 溢出的数据会让时间戳 +1.
 * 这样做, 只要节点不重启或者在重启前平均QPS没有超标, 重启后分配的ID仍能唯一
 *
 * 当前被调试为平均每毫秒可以相应 128 个消息
 * 如果平均每个人 10 秒发一条消息, 1 秒 128 条消息大约要 1280 人, 1 毫秒 128 条消息就大约需要 128W 用户了
 * 单点无法应付如此巨量的并发, ID生成器保证性能过剩
 *
 * 当前最多支持 8192 个节点同时上线, 未来如果节点数超过了 8192 个, 也可以以ID生成的最晚时间为代价提升节点最高数量
 */
class Snowflake(
    @Suppress("MemberVisibilityCanBePrivate") val nodeId: Int
) {
    constructor(workerId: String) : this(parseId(workerId))

    @Suppress("MemberVisibilityCanBePrivate")
    val timestamp = System.currentTimeMillis().and(0x00_00_07_ff__ff_ff_ff_ff).shl(7)
    private val seed = AtomicLong((nodeId and 0x1fff).toLong().shl(50) or timestamp)
    val id: Long get() = seed.incrementAndGet()

    override fun toString() = "Snowflake(workerId=${nodeId
    }, timestamp=0x${timestamp.toByteArray().toHexString()
    }, seed=0x${seed.get().toByteArray().toHexString()})"

    companion object {
        fun parseId(workerId: String): Int {
            var id = 0
            var step = 1
            for (index in workerId.length - 1 downTo 0) {
                if (Character.isDigit(workerId[index])) {
                    id += (workerId[index] - '0') * step
                    step *= 10
                } else {
                    break
                }
            }
            if (step != 1) {
                return id
            } else {
                throw NumberFormatException(workerId)
            }
        }
    }
}
