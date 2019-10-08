package cn.tursom.utils.datastruct.async

import cn.tursom.utils.asynclock.AsyncReadFirstRWLock
import cn.tursom.utils.asynclock.AsyncWriteFirstRWLock
import cn.tursom.utils.datastruct.async.collections.AsyncMapSet
import cn.tursom.utils.datastruct.async.collections.AsyncRWLockAbstractMap
import cn.tursom.core.datastruct.async.interfaces.AsyncPotableMap


val <K : Comparable<K>> AsyncPotableMap<K, Unit>.keySet
	get() = AsyncMapSet(this)

@Suppress("FunctionName")
fun <K, V> ReadWriteLockHashMap() = AsyncRWLockAbstractMap<K, V>(AsyncWriteFirstRWLock())

@Suppress("FunctionName")
fun <K, V> WriteLockHashMap() =
	AsyncRWLockAbstractMap<K, V>(AsyncReadFirstRWLock())