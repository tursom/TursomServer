package cn.tursom.database.mybatisplus

import cn.tursom.core.util.uncheckedCast
import com.baomidou.mybatisplus.core.conditions.interfaces.Join

@MybatisPlusEnhanceDslMaker
interface JoinEnhance<out Children> {
  val join: Join<out Children> get() = uncheckedCast()

  /**
   * QueryWrapper<T>()
   *     .xx()
   *     .xxx()
   *     ...
   *     .limit1()
   */
  fun limit1(): Children = join.last("LIMIT 1").uncheckedCast()

  fun limit(count: Int): Children = join.last("LIMIT $count").uncheckedCast()
  fun limit(start: Int, count: Int): Children = join.last("LIMIT $start, $count").uncheckedCast()
}