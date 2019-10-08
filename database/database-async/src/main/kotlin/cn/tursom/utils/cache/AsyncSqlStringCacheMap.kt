package cn.tursom.utils.cache

import cn.tursom.database.async.AsyncSqlAdapter
import cn.tursom.database.async.AsyncSqlHelper
import cn.tursom.database.clauses.clause
import cn.tursom.utils.background
import cn.tursom.utils.cache.interfaces.AsyncPotableCacheMap
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")
class AsyncSqlStringCacheMap(
	val db: AsyncSqlHelper,
	val timeout: Long,
	val table: String,
	val updateDelay: Long = 60 * 1000,
	val prevCacheMap: AsyncPotableCacheMap<String, String> = DefaultAsyncPotableCacheMap(timeout),
	val logger: Logger? = null
) : AsyncPotableCacheMap<String, String> by prevCacheMap {

	override suspend fun get(key: String): String? {
		return prevCacheMap.get(key) ?: try {
			val storage = db.select(AsyncSqlAdapter(StorageData::class.java), where = clause { !StorageData::key equal !key }, maxCount = 1, table = table)
			if (storage.isNotEmpty() && storage[0].cacheTime + timeout > System.currentTimeMillis()) {
				val value = storage[0].value
				set(key, value)
				value
			} else null
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}

	override suspend fun get(key: String, constructor: suspend () -> String): String {
		val memCache = get(key)
		return if (memCache != null) memCache
		else {
			val newValue = constructor()
			this.set(key, newValue)
			newValue
		}
	}

	override suspend fun set(key: String, value: String): String? {
		prevCacheMap.set(key, value)
		background { updateStorage(key, value) }
		return value
	}

	suspend fun updateStorage(key: String, value: String) {
		try {
			val updated = db.replace(table, StorageData(key, value))
			logger?.log(Level.INFO, "AsyncSqlStringCacheMap update $updated coloums: $key")
		} catch (e: Exception) {
			System.err.println("AsyncSqlStringCacheMap cause an Exception on update cn.tusom.database")
			e.printStackTrace()
		}
	}
}

