package cn.tursom.core.curry

fun <T1, T2, R> ((T1, T2) -> R).currying() = { a1: T1 -> { a2: T2 -> this(a1, a2) } }
fun <T1, T2, T3, R> ((T1, T2, T3) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> this(a1, a2, a3) } } }
fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> this(a1, a2, a3, a4) } } } }
fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> this(a1, a2, a3, a4, a5) } } } } }
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> this(a1, a2, a3, a4, a5, a6) } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, R> ((T1, T2, T3, T4, T5, T6, T7) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> this(a1, a2, a3, a4, a5, a6, a7) } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> ((T1, T2, T3, T4, T5, T6, T7, T8) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> this(a1, a2, a3, a4, a5, a6, a7, a8) } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9) } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11) } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12) } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) } } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> { a14: T14 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) } } } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> { a14: T14 -> { a15: T15 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) } } } } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> { a14: T14 -> { a15: T15 -> { a16: T16 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) } } } } } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> { a14: T14 -> { a15: T15 -> { a16: T16 -> { a17: T17 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) } } } } } } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> { a14: T14 -> { a15: T15 -> { a16: T16 -> { a17: T17 -> { a18: T18 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) } } } } } } } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> { a14: T14 -> { a15: T15 -> { a16: T16 -> { a17: T17 -> { a18: T18 -> { a19: T19 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) } } } } } } } } } } } } } } } } } } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R).currying() = { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> { a6: T6 -> { a7: T7 -> { a8: T8 -> { a9: T9 -> { a10: T10 -> { a11: T11 -> { a12: T12 -> { a13: T13 -> { a14: T14 -> { a15: T15 -> { a16: T16 -> { a17: T17 -> { a18: T18 -> { a19: T19 -> { a20: T20 -> this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) } } } } } } } } } } } } } } } } } } } }

@JvmName("curryingVararg")
inline fun <reified T1, T2, R> ((Array<out T1>, T2) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, R> ((Array<out T1>, T2, T3) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, R> ((Array<out T1>, T2, T3, T4) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, R> ((Array<out T1>, T2, T3, T4, T5) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, R> ((Array<out T1>, T2, T3, T4, T5, T6) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>().currying(args = args)

@JvmName("curryingVararg")
inline fun <reified T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> ((Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R).currying(vararg args: T1) =
    currying<Array<out T1>, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>().currying(args = args)
