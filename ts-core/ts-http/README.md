# ts-http 模块
## 简介
ts-http 模块负责提供同步的 http 请求接口。目前我们仅对 OkHttp 进行了少量封装，以提高易用性。

封装的功能包括：
1. DSL 支持。DSL 形式的请求构建结构更加清晰，易于维护。
2. json 解析。这部分需要引入 GSON 依赖。
3. 其他接口封装。如 Call.str()、Call.bytes() 等。

## 功能描述
### DSL 支持
ts-http 模块提供轻量级的 DSL 封装。提供的方法有：
- ```Call.Factory.newCall(builder: Request.Builder.() -> Unit): Call``` 用于构建新请求
- ```WebSocket.Factory.newWebSocket(listener: WebSocketListener, builder: Request.Builder.() -> Unit): WebSocket``` 用于构建 WebSocket 请求
- ```<B : Request.Builder> B.url(url: String? = null, builder: HttpUrl.Builder.() -> Unit): B``` 用于构建请求 url
- ```form(builder: FormBody.Builder.() -> Unit): FormBody``` 用于构建 form 请求体
- ```FormBody.Builder.build(builder: FormBody.Builder.() -> Unit): FormBody``` 同上

### json 解析
ts-http 模块使用 GSON 提供 json 解析功能，考虑到有些项目可能不希望引入 GSON 依赖，因此没有自动引入 GSON。用户如果希望使用此功能，需要手动引入 GSON 依赖。由于 ts-http 只使用了 GSON 最基础的功能，因此对 GSON 的版本没有过高要求，只要有```T fromJson(String json, Class<T> classOfT)```，```T fromJson(String json, Type typeOfT)```和```com.google.gson.reflect.TypeToken<T>```即可。

ts-http 为 Call 和 ResponseBody 分别添加了```inline fun <reified T : Any> json(): T```和```inline fun <reified T : Any> jsonTyped(): T```这两个方法，前者直接使用```T::class.java```获取 Class 对象，后者则使用 TypeToken 获取 Type 对象，用户可以根据需求使用不同的方法。

### 其他接口封装
ts-http 还为 GET 和 POST 单独封装了额外的接口以便于使用。
