package cn.tursom.web

interface HttpHandler<in T : HttpContent, in E : ExceptionContent> {
	fun handle(content: T)

	fun exception(e: E) {
		e.cause.printStackTrace()
	}

	operator fun invoke(content: T) {
		handle(content)
	}
}