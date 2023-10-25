package cn.tursom.core.util

fun <T, R> with(v: T, action: (T) -> R): () -> R = { action(v) }
fun <T1, T2, R> with(v1: T1, v2: T2, action: (T1, T2) -> R): () -> R = { action(v1, v2) }