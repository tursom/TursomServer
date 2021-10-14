package cn.tursom.core.curry

fun <T1, R> curry(action: (a1: T1) -> R) = Curry1(action)

fun <T1, T2, R> curry(action: (a1: T1, a2: T2) -> R) = Curry2 { a1: T1, a2: T2 ->
    action(a1, a2)
}

fun <T1, T2, T3, R> curry(action: (a1: T1, a2: T2, a3: T3) -> R) = Curry3 { a1: T1, a2: T2, a3: T3 ->
    action(a1, a2, a3)
}

fun <T1, T2, T3, T4, R> curry(action: (T1, T2, T3, T4) -> R) = Curry4 { a1: T1, a2: T2, a3: T3, a4: T4 ->
    action(a1, a2, a3, a4)
}

fun <T1, T2, T3, T4, T5, R> curry(action: (T1, T2, T3, T4, T5) -> R) =
    Curry5 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5 ->
        action(a1, a2, a3, a4, a5)
    }

fun <T1, T2, T3, T4, T5, T6, R> curry(action: (T1, T2, T3, T4, T5, T6) -> R) =
    Curry6 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6 ->
        action(a1, a2, a3, a4, a5, a6)
    }

fun <T1, T2, T3, T4, T5, T6, T7, R> curry(action: (T1, T2, T3, T4, T5, T6, T7) -> R) =
    Curry7 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7 ->
        action(a1, a2, a3, a4, a5, a6, a7)
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> curry(action: (T1, T2, T3, T4, T5, T6, T7, T8) -> R) =
    Curry8 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8 ->
        action(a1, a2, a3, a4, a5, a6, a7, a8)
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> curry(action: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R) =
    Curry9 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9 ->
        action(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> curry(action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R) =
    Curry10 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10 ->
        action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R
) =
    Curry11 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11 ->
        action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R
) = Curry12 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R
) = Curry13 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12,
              a13: T13 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R
) =
    Curry14 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14 ->
        action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R
) = Curry15 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12,
              a13: T13, a14: T14, a15: T15 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R
) = Curry16 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12,
              a13: T13, a14: T14, a15: T15, a16: T16 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R
) = Curry17 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12,
              a13: T13, a14: T14, a15: T15, a16: T16, a17: T17 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R
) = Curry18 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12,
              a13: T13, a14: T14, a15: T15, a16: T16, a17: T17, a18: T18 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R
) = Curry19 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12,
              a13: T13, a14: T14, a15: T15, a16: T16, a17: T17, a18: T18, a19: T19 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> curry(
    action: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R
) = Curry20 { a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12,
              a13: T13, a14: T14, a15: T15, a16: T16, a17: T17, a18: T18, a19: T19, a20: T20 ->
    action(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
}
