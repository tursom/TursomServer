package cn.tursom.core

import sun.misc.Unsafe

object Unsafe {
    val unsafe: Unsafe

    init {
        val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
        theUnsafe.isAccessible = true
        unsafe = theUnsafe.get(null) as Unsafe
    }
}