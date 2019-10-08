package cn.tursom.utils.cache

import cn.tursom.database.annotation.NotNull
import cn.tursom.database.annotation.PrimaryKey


data class StorageData(
    @PrimaryKey
    @NotNull
    val key: String,
    @NotNull
    val value: String,
    val cacheTime: Long = System.currentTimeMillis()
)