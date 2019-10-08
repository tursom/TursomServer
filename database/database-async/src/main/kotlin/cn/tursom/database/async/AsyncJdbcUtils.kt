package cn.tursom.database.async

import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import org.springframework.beans.factory.annotation.Value
import java.sql.Connection
import javax.annotation.PostConstruct
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AsyncJdbcUtils {
	private val regexp = object : org.sqlite.Function() {
		override fun xFunc() {
			val regex = Regex(value_text(0) ?: "")
			val value = value_text(1) ?: ""
			result(if (regex.containsMatchIn(value)) 1 else 0)
		}
	}

	@Value("\${jdbc.providerClass}")
	var providerClass: String? = null
	@Value("\${jdbc.rowStreamFetchSize}")
	var rowStreamFetchSize: String? = null

	@Value("\${jdbc.url}")
	var url: String = ""
	@Value("\${jdbc.driver}")
	var driver: String = ""
	@Value("\${jdbc.driver}")
	var user: String? = null
	@Value("\${jdbc.driver}")
	var password: String? = null

	@Value("\${jdbc.maxPoolSize}")
	var maxPoolSize: Int? = null
	@Value("\${jdbc.initialPoolSize}")
	var initialPoolSize: Int? = null
	@Value("\${jdbc.minPoolSize}")
	var minPoolSize: Int? = null
	/**
	 * 预处理SQL语句最小缓存数，默认 0
	 */
	@Value("\${jdbc.maxStatements}")
	var maxStatements: Int? = null
	/**
	 * 每个数据库连接的最大预处理SQL缓存数，默认0
	 */
	@Value("\${jdbc.maxStatementsPerConnection}")
	var maxStatementsPerConnection: Int? = null
	/**
	 * 空闲连接保留时间，默认600000 (10分钟)
	 */
	@Value("\${jdbc.maxIdleTime}")
	var maxIdleTime: Int = 600000

	/**
	 * 是否自动提交，对 Hikari 有效
	 */
	@Value("\${jdbc.autoCommit}")
	var autoCommit: Boolean = false

	/**
	 * Hikari 连接最大时间
	 */
	@Value("\${jdbc.connectionTimeout}")
	var connectionTimeout: Int = 30000

	@Value("\${jdbc.poolName}")
	var poolName: String? = null

	private var client: JDBCClient? = null

	private var injectSqliteRegex: Boolean = false

	@PostConstruct
	fun initClient() {
		client?.close()
		val config = JsonObject()
		injectSqliteRegex = try {
			url.split(':')[1] == "sqlite"
		} catch (e: Exception) {
			false
		}
		providerClass?.let { config.put("provider_class", it) }
		rowStreamFetchSize?.let { config.put("row_stream_fetch_size", it) }

		config.put("url", url)
		config.put("jdbcUrl", url)
		config.put("driver_class", driver)
		config.put("driverClassName", driver)
		user?.let { config.put("user", it) }
		password?.let { config.put("password", it) }

		maxPoolSize?.let { config.put("max_pool_size", it) }
		maxPoolSize?.let { config.put("maximumPoolSize", it) }
		initialPoolSize?.let { config.put("initial_pool_size", it) }
		minPoolSize?.let { config.put("min_pool_size", it) }
		maxStatements?.let { config.put("max_statements", it) }
		maxStatementsPerConnection?.let { config.put("max_statements_per_connection", it) }
		config.put("max_idle_time", maxIdleTime)
		config.put("idleTimeout", maxIdleTime)
		config.put("autoCommit", autoCommit)
		config.put("connectionTimeout", connectionTimeout)
		poolName?.let { config.put("poolName", it) }

		client = JDBCClient.createShared(vertx, config, url)
	}

	suspend fun getConnection(): SQLConnection {
		return suspendCoroutine { cont ->
			client?.getConnection {
				if (it.failed()) {
					cont.resumeWithException(it.cause())
				} else {
					if (injectSqliteRegex) {
						val conn = it.result()
						// 我们要利用反射强行把我们需要的方法注入进去
						val connField = conn.javaClass.getDeclaredField("conn")
						connField.isAccessible = true
						val fieldConn = connField.get(conn)
						val innerField = fieldConn.javaClass.getDeclaredField("inner")
						innerField.isAccessible = true
						org.sqlite.Function.create(innerField.get(fieldConn) as Connection, "REGEXP", regexp)
					}
					cont.resume(it.result())
				}
			} ?: cont.resumeWithException(NullPointerException())
		}
	}
}