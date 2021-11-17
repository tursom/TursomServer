package cn.tursom.database.mybatisplus

@MybatisPlusEnhanceDslMaker
interface EnhanceEntityClassEnhance<T> {
  val enhanceEntityClass: Class<T>
}