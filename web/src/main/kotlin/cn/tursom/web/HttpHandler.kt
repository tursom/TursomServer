package cn.tursom.web

interface HttpHandler<in T : HttpContent, in E : ExceptionContent> {
  fun handle(content: T)

  fun exceptionCause(e: E) {
    e.cause.printStackTrace()
    e.responseCode = 500
    e.finish()
  }

  operator fun invoke(content: T) {
    handle(content)
  }
}