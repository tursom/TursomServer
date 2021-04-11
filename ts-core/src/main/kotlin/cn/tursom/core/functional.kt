package cn.tursom.core

inline fun <T, R> with(v: T, crossinline action: (T) -> R): () -> R = { action(v) }
inline fun <T1, T2, R> with(v1: T1, v2: T2, crossinline action: (T1, T2) -> R): () -> R = { action(v1, v2) }