package cn.tursom.database

interface SqlTemplate {
  fun <T : Any> getHelper(clazz: Class<T>): SqlHelper<T>
}