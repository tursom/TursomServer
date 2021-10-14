package cn.tursom.core.curry

open class Curry1<T1, R>(
    val action1: (a1: T1) -> R,
) {
    open operator fun invoke(a1: T1) = action1(a1)
}

open class Curry2<T1, T2, R>(
    val action2: (a1: T1, a2: T2) -> R,
) : Curry1<T1, Curry1<T2, R>>({ a1 ->
    Curry1 { a2 ->
        action2(a1, a2)
    }
}) {
    open operator fun invoke(a1: T1, a2: T2): R {
        return action2(a1, a2)
    }
}

open class Curry3<T1, T2, T3, R>(
    val action3: (a1: T1, a2: T2, a3: T3) -> R,
) : Curry2<T1, T2, Curry1<T3, R>>({ a1, a2 ->
    Curry1 { a3 ->
        action3(a1, a2, a3)
    }
}) {
    override operator fun invoke(a1: T1): Curry2<T2, T3, R> = Curry2 { a2, a3 ->
        action3(a1, a2, a3)
    }

    open operator fun invoke(a1: T1, a2: T2, a3: T3): R = action3(a1, a2, a3)
}

open class Curry4<T1, T2, T3, T4, R>(
    val action4: (a1: T1, a2: T2, a3: T3, a4: T4) -> R,
) : Curry3<T1, T2, T3, Curry1<T4, R>>({ a1, a2, a3 ->
    Curry1 { a4 ->
        action4(a1, a2, a3, a4)
    }
}) {
    override operator fun invoke(a1: T1): Curry3<T2, T3, T4, R> = Curry3 { a2, a3, a4 ->
        action4(a1, a2, a3, a4)
    }

    override operator fun invoke(a1: T1, a2: T2): Curry2<T3, T4, R> = Curry2 { a3, a4 ->
        action4(a1, a2, a3, a4)
    }

    open operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): R = action4(a1, a2, a3, a4)
}

open class Curry5<T1, T2, T3, T4, T5, R>(
    val action5: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5) -> R,
) : Curry4<T1, T2, T3, T4, Curry1<T5, R>>({ a1, a2, a3, a4 ->
    Curry1 { a5 ->
        action5(a1, a2, a3, a4, a5)
    }
}) {
    override operator fun invoke(a1: T1): Curry4<T2, T3, T4, T5, R> = Curry4 { a2, a3, a4, a5 ->
        action5(a1, a2, a3, a4, a5)
    }

    override operator fun invoke(a1: T1, a2: T2): Curry3<T3, T4, T5, R> = Curry3 { a3, a4, a5 ->
        action5(a1, a2, a3, a4, a5)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry2<T4, T5, R> = Curry2 { a4, a5 ->
        action5(a1, a2, a3, a4, a5)
    }

    open operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): R = action5(a1, a2, a3, a4, a5)
}

open class Curry6<T1, T2, T3, T4, T5, T6, R>(
    val action6: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6) -> R,
) : Curry5<T1, T2, T3, T4, T5, Curry1<T6, R>>({ a1, a2, a3, a4, a5 ->
    Curry1 { a6 ->
        action6(a1, a2, a3, a4, a5, a6)
    }
}) {
    override operator fun invoke(a1: T1): Curry5<T2, T3, T4, T5, T6, R> = Curry5 { a2, a3, a4, a5, a6 ->
        action6(a1, a2, a3, a4, a5, a6)
    }

    override operator fun invoke(a1: T1, a2: T2): Curry4<T3, T4, T5, T6, R> = Curry4 { a3, a4, a5, a6 ->
        action6(a1, a2, a3, a4, a5, a6)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry3<T4, T5, T6, R> = Curry3 { a4, a5, a6 ->
        action6(a1, a2, a3, a4, a5, a6)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry2<T5, T6, R> = Curry2 { a5, a6 ->
        action6(a1, a2, a3, a4, a5, a6)
    }

    open operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6): R = action6(a1, a2, a3, a4, a5, a6)
}

open class Curry7<T1, T2, T3, T4, T5, T6, T7, R>(
    val action7: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7) -> R,
) : Curry6<T1, T2, T3, T4, T5, T6, Curry1<T7, R>>({ a1, a2, a3, a4, a5, a6 ->
    Curry1 { a7 ->
        action7(a1, a2, a3, a4, a5, a6, a7)
    }
}) {
    override operator fun invoke(a1: T1): Curry6<T2, T3, T4, T5, T6, T7, R> = Curry6 { a2, a3, a4, a5, a6, a7 ->
        action7(a1, a2, a3, a4, a5, a6, a7)
    }

    override operator fun invoke(a1: T1, a2: T2): Curry5<T3, T4, T5, T6, T7, R> = Curry5 { a3, a4, a5, a6, a7 ->
        action7(a1, a2, a3, a4, a5, a6, a7)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry4<T4, T5, T6, T7, R> = Curry4 { a4, a5, a6, a7 ->
        action7(a1, a2, a3, a4, a5, a6, a7)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry3<T5, T6, T7, R> = Curry3 { a5, a6, a7 ->
        action7(a1, a2, a3, a4, a5, a6, a7)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): Curry2<T6, T7, R> = Curry2 { a6, a7 ->
        action7(a1, a2, a3, a4, a5, a6, a7)
    }

    open operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7): R =
        action7(a1, a2, a3, a4, a5, a6, a7)
}

open class Curry8<T1, T2, T3, T4, T5, T6, T7, T8, R>(
    val action8: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8) -> R,
) : Curry7<T1, T2, T3, T4, T5, T6, T7, Curry1<T8, R>>({ a1, a2, a3, a4, a5, a6, a7 ->
    Curry1 { a8 ->
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
    }
}) {
    override operator fun invoke(a1: T1): Curry7<T2, T3, T4, T5, T6, T7, T8, R> = Curry7 { a2, a3, a4, a5, a6, a7, a8 ->
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
    }

    override operator fun invoke(a1: T1, a2: T2): Curry6<T3, T4, T5, T6, T7, T8, R> = Curry6 { a3, a4, a5, a6, a7, a8 ->
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry5<T4, T5, T6, T7, T8, R> = Curry5 { a4, a5, a6, a7, a8 ->
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry4<T5, T6, T7, T8, R> = Curry4 { a5, a6, a7, a8 ->
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): Curry3<T6, T7, T8, R> = Curry3 { a6, a7, a8 ->
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
    }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6): Curry2<T7, T8, R> = Curry2 { a7, a8 ->
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
    }

    open operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8): R =
        action8(a1, a2, a3, a4, a5, a6, a7, a8)
}

open class Curry9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>(
    val action9: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9) -> R,
) : Curry8<T1, T2, T3, T4, T5, T6, T7, T8, Curry1<T9, R>>({ a1, a2, a3, a4, a5, a6, a7, a8 ->
    Curry1 { a9 ->
        action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
}) {
    override operator fun invoke(a1: T1): Curry8<T2, T3, T4, T5, T6, T7, T8, T9, R> =
        Curry8 { a2, a3, a4, a5, a6, a7, a8, a9 ->
            action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
        }

    override operator fun invoke(a1: T1, a2: T2): Curry7<T3, T4, T5, T6, T7, T8, T9, R> =
        Curry7 { a3, a4, a5, a6, a7, a8, a9 ->
            action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry6<T4, T5, T6, T7, T8, T9, R> =
        Curry6 { a4, a5, a6, a7, a8, a9 ->
            action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry5<T5, T6, T7, T8, T9, R> =
        Curry5 { a5, a6, a7, a8, a9 ->
            action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): Curry4<T6, T7, T8, T9, R> =
        Curry4 { a6, a7, a8, a9 ->
            action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6): Curry3<T7, T8, T9, R> =
        Curry3 { a7, a8, a9 ->
            action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7): Curry2<T8, T9, R> =
        Curry2 { a8, a9 ->
            action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
        }

    open operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9): R =
        action9(a1, a2, a3, a4, a5, a6, a7, a8, a9)
}

open class Curry10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>(
    val action10: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10) -> R,
) : Curry9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Curry1<T10, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
    Curry1 { a10 ->
        action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
}) {
    override operator fun invoke(a1: T1): Curry9<T2, T3, T4, T5, T6, T7, T8, T9, T10, R> =
        Curry9 { a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    override operator fun invoke(a1: T1, a2: T2): Curry8<T3, T4, T5, T6, T7, T8, T9, T10, R> =
        Curry8 { a3, a4, a5, a6, a7, a8, a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry7<T4, T5, T6, T7, T8, T9, T10, R> =
        Curry7 { a4, a5, a6, a7, a8, a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry6<T5, T6, T7, T8, T9, T10, R> =
        Curry6 { a5, a6, a7, a8, a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): Curry5<T6, T7, T8, T9, T10, R> =
        Curry5 { a6, a7, a8, a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6): Curry4<T7, T8, T9, T10, R> =
        Curry4 { a7, a8, a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7): Curry3<T8, T9, T10, R> =
        Curry3 { a8, a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8): Curry2<T9, T10, R> =
        Curry2 { a9, a10 ->
            action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
        }

    open operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10): R =
        action10(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
}

open class Curry11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>(
    val action11: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11) -> R,
) : Curry10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Curry1<T11, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
    Curry1 { a11 ->
        action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
}) {
    override operator fun invoke(a1: T1): Curry10<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> =
        Curry10 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
            action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
        }

    override operator fun invoke(a1: T1, a2: T2): Curry9<T3, T4, T5, T6, T7, T8, T9, T10, T11, R> =
        Curry9 { a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
            action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry8<T4, T5, T6, T7, T8, T9, T10, T11, R> =
        Curry8 { a4, a5, a6, a7, a8, a9, a10, a11 ->
            action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry7<T5, T6, T7, T8, T9, T10, T11, R> =
        Curry7 { a5, a6, a7, a8, a9, a10, a11 ->
            action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): Curry6<T6, T7, T8, T9, T10, T11, R> =
        Curry6 { a6, a7, a8, a9, a10, a11 ->
            action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6): Curry5<T7, T8, T9, T10, T11, R> =
        Curry5 { a7, a8, a9, a10, a11 ->
            action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7): Curry4<T8, T9, T10, T11, R> =
        Curry4 { a8, a9, a10, a11 ->
            action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry3<T9, T10, T11, R> = Curry3 { a9, a10, a11 ->
        action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry2<T10, T11, R> = Curry2 { a10, a11 ->
        action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): R = action11(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
}

open class Curry12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>(
    val action12: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12) -> R,
) : Curry11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Curry1<T12, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
    Curry1 { a12 ->
        action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
}) {
    override operator fun invoke(a1: T1): Curry11<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> =
        Curry11 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
            action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
        }

    override operator fun invoke(a1: T1, a2: T2): Curry10<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> =
        Curry10 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
            action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry9<T4, T5, T6, T7, T8, T9, T10, T11, T12, R> =
        Curry9 { a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
            action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry8<T5, T6, T7, T8, T9, T10, T11, T12, R> =
        Curry8 { a5, a6, a7, a8, a9, a10, a11, a12 ->
            action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): Curry7<T6, T7, T8, T9, T10, T11, T12, R> =
        Curry7 { a6, a7, a8, a9, a10, a11, a12 ->
            action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6): Curry6<T7, T8, T9, T10, T11, T12, R> =
        Curry6 { a7, a8, a9, a10, a11, a12 ->
            action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry5<T8, T9, T10, T11, T12, R> = Curry5 { a8, a9, a10, a11, a12 ->
        action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry4<T9, T10, T11, T12, R> = Curry4 { a9, a10, a11, a12 ->
        action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry3<T10, T11, T12, R> = Curry3 { a10, a11, a12 ->
        action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry2<T11, T12, R> = Curry2 { a11, a12 ->
        action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): R = action12(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
}

open class Curry13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>(
    val action13: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13) -> R,
) : Curry12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Curry1<T13, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
    Curry1 { a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
}) {
    override operator fun invoke(a1: T1): Curry12<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
        Curry12 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
            action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
        }

    override operator fun invoke(a1: T1, a2: T2): Curry11<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
        Curry11 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
            action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry10<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
        Curry10 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
            action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4): Curry9<T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
        Curry9 { a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
            action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry8<T6, T7, T8, T9, T10, T11, T12, T13, R> = Curry8 { a6, a7, a8, a9, a10, a11, a12, a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry7<T7, T8, T9, T10, T11, T12, T13, R> = Curry7 { a7, a8, a9, a10, a11, a12, a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry6<T8, T9, T10, T11, T12, T13, R> = Curry6 { a8, a9, a10, a11, a12, a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry5<T9, T10, T11, T12, T13, R> = Curry5 { a9, a10, a11, a12, a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry4<T10, T11, T12, T13, R> = Curry4 { a10, a11, a12, a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry3<T11, T12, T13, R> = Curry3 { a11, a12, a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry2<T12, T13, R> = Curry2 { a12, a13 ->
        action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13
    ): R = action13(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
}

open class Curry14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>(
    val action14: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14) -> R,
) : Curry13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, Curry1<T14, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
    Curry1 { a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
}) {
    override operator fun invoke(a1: T1): Curry13<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
        Curry13 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
            action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
        }

    override operator fun invoke(a1: T1, a2: T2): Curry12<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
        Curry12 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
            action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
        }

    override operator fun invoke(a1: T1, a2: T2, a3: T3): Curry11<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
        Curry11 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
            action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4
    ): Curry10<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
        Curry10 { a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
            action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry9<T6, T7, T8, T9, T10, T11, T12, T13, T14, R> = Curry9 { a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry8<T7, T8, T9, T10, T11, T12, T13, T14, R> = Curry8 { a7, a8, a9, a10, a11, a12, a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry7<T8, T9, T10, T11, T12, T13, T14, R> = Curry7 { a8, a9, a10, a11, a12, a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry6<T9, T10, T11, T12, T13, T14, R> = Curry6 { a9, a10, a11, a12, a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry5<T10, T11, T12, T13, T14, R> = Curry5 { a10, a11, a12, a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry4<T11, T12, T13, T14, R> = Curry4 { a11, a12, a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry3<T12, T13, T14, R> = Curry3 { a12, a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): Curry2<T13, T14, R> = Curry2 { a13, a14 ->
        action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14
    ): R = action14(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
}

open class Curry15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>(
    val action15: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14, a15: T15) -> R,
) : Curry14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, Curry1<T15, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
    Curry1 { a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
}) {
    override operator fun invoke(a1: T1): Curry14<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
        Curry14 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
            action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
        }

    override operator fun invoke(a1: T1, a2: T2): Curry13<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
        Curry13 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
            action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3
    ): Curry12<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
        Curry12 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
            action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4
    ): Curry11<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
        Curry11 { a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
            action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry10<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
        Curry10 { a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
            action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry9<T7, T8, T9, T10, T11, T12, T13, T14, T15, R> = Curry9 { a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry8<T8, T9, T10, T11, T12, T13, T14, T15, R> = Curry8 { a8, a9, a10, a11, a12, a13, a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry7<T9, T10, T11, T12, T13, T14, T15, R> = Curry7 { a9, a10, a11, a12, a13, a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry6<T10, T11, T12, T13, T14, T15, R> = Curry6 { a10, a11, a12, a13, a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry5<T11, T12, T13, T14, T15, R> = Curry5 { a11, a12, a13, a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry4<T12, T13, T14, T15, R> = Curry4 { a12, a13, a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): Curry3<T13, T14, T15, R> = Curry3 { a13, a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13
    ): Curry2<T14, T15, R> = Curry2 { a14, a15 ->
        action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15
    ): R = action15(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
}

open class Curry16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>(
    val action16: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14, a15: T15, a16: T16) -> R,
) : Curry15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, Curry1<T16, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
    Curry1 { a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
}) {
    override operator fun invoke(a1: T1): Curry15<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        Curry15 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
            action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2
    ): Curry14<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        Curry14 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
            action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3
    ): Curry13<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        Curry13 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
            action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4
    ): Curry12<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        Curry12 { a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
            action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry11<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        Curry11 { a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
            action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry10<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        Curry10 { a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
            action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry9<T8, T9, T10, T11, T12, T13, T14, T15, T16, R> = Curry9 { a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry8<T9, T10, T11, T12, T13, T14, T15, T16, R> = Curry8 { a9, a10, a11, a12, a13, a14, a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry7<T10, T11, T12, T13, T14, T15, T16, R> = Curry7 { a10, a11, a12, a13, a14, a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry6<T11, T12, T13, T14, T15, T16, R> = Curry6 { a11, a12, a13, a14, a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry5<T12, T13, T14, T15, T16, R> = Curry5 { a12, a13, a14, a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): Curry4<T13, T14, T15, T16, R> = Curry4 { a13, a14, a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13
    ): Curry3<T14, T15, T16, R> = Curry3 { a14, a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14
    ): Curry2<T15, T16, R> = Curry2 { a15, a16 ->
        action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16
    ): R = action16(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
}

open class Curry17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>(
    val action17: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14, a15: T15, a16: T16, a17: T17) -> R,
) : Curry16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, Curry1<T17, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
    Curry1 { a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
}) {
    override operator fun invoke(a1: T1): Curry16<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        Curry16 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
            action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2
    ): Curry15<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        Curry15 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
            action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3
    ): Curry14<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        Curry14 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
            action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4
    ): Curry13<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        Curry13 { a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
            action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry12<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        Curry12 { a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
            action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry11<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        Curry11 { a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
            action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry10<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        Curry10 { a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
            action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry9<T9, T10, T11, T12, T13, T14, T15, T16, T17, R> = Curry9 { a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry8<T10, T11, T12, T13, T14, T15, T16, T17, R> = Curry8 { a10, a11, a12, a13, a14, a15, a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry7<T11, T12, T13, T14, T15, T16, T17, R> = Curry7 { a11, a12, a13, a14, a15, a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry6<T12, T13, T14, T15, T16, T17, R> = Curry6 { a12, a13, a14, a15, a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): Curry5<T13, T14, T15, T16, T17, R> = Curry5 { a13, a14, a15, a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13
    ): Curry4<T14, T15, T16, T17, R> = Curry4 { a14, a15, a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14
    ): Curry3<T15, T16, T17, R> = Curry3 { a15, a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15
    ): Curry2<T16, T17, R> = Curry2 { a16, a17 ->
        action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16,
        a17: T17
    ): R = action17(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
}

open class Curry18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>(
    val action18: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14, a15: T15, a16: T16, a17: T17, a18: T18) -> R,
) : Curry17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, Curry1<T18, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
    Curry1 { a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
}) {
    override operator fun invoke(a1: T1): Curry17<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry17 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2
    ): Curry16<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry16 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3
    ): Curry15<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry15 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4
    ): Curry14<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry14 { a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry13<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry13 { a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry12<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry12 { a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry11<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry11 { a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry10<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        Curry10 { a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
            action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry9<T10, T11, T12, T13, T14, T15, T16, T17, T18, R> = Curry9 { a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry8<T11, T12, T13, T14, T15, T16, T17, T18, R> = Curry8 { a11, a12, a13, a14, a15, a16, a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry7<T12, T13, T14, T15, T16, T17, T18, R> = Curry7 { a12, a13, a14, a15, a16, a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): Curry6<T13, T14, T15, T16, T17, T18, R> = Curry6 { a13, a14, a15, a16, a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13
    ): Curry5<T14, T15, T16, T17, T18, R> = Curry5 { a14, a15, a16, a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14
    ): Curry4<T15, T16, T17, T18, R> = Curry4 { a15, a16, a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15
    ): Curry3<T16, T17, T18, R> = Curry3 { a16, a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16
    ): Curry2<T17, T18, R> = Curry2 { a17, a18 ->
        action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16,
        a17: T17,
        a18: T18
    ): R = action18(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
}

open class Curry19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>(
    val action19: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14, a15: T15, a16: T16, a17: T17, a18: T18, a19: T19) -> R,
) : Curry18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, Curry1<T19, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
    Curry1 { a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
}) {
    override operator fun invoke(a1: T1): Curry18<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry18 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2
    ): Curry17<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry17 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3
    ): Curry16<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry16 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4
    ): Curry15<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry15 { a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry14<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry14 { a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry13<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry13 { a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry12<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry12 { a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry11<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry11 { a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry10<T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        Curry10 { a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
            action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry9<T11, T12, T13, T14, T15, T16, T17, T18, T19, R> = Curry9 { a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry8<T12, T13, T14, T15, T16, T17, T18, T19, R> = Curry8 { a12, a13, a14, a15, a16, a17, a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): Curry7<T13, T14, T15, T16, T17, T18, T19, R> = Curry7 { a13, a14, a15, a16, a17, a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13
    ): Curry6<T14, T15, T16, T17, T18, T19, R> = Curry6 { a14, a15, a16, a17, a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14
    ): Curry5<T15, T16, T17, T18, T19, R> = Curry5 { a15, a16, a17, a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15
    ): Curry4<T16, T17, T18, T19, R> = Curry4 { a16, a17, a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16
    ): Curry3<T17, T18, T19, R> = Curry3 { a17, a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16,
        a17: T17
    ): Curry2<T18, T19, R> = Curry2 { a18, a19 ->
        action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16,
        a17: T17,
        a18: T18,
        a19: T19
    ): R = action19(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
}

open class Curry20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>(
    val action20: (a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13, a14: T14, a15: T15, a16: T16, a17: T17, a18: T18, a19: T19, a20: T20) -> R,
) : Curry19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, Curry1<T20, R>>({ a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
    Curry1 { a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
}) {
    override operator fun invoke(a1: T1): Curry19<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry19 { a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2
    ): Curry18<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry18 { a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3
    ): Curry17<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry17 { a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4
    ): Curry16<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry16 { a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5
    ): Curry15<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry15 { a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6
    ): Curry14<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry14 { a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7
    ): Curry13<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry13 { a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8
    ): Curry12<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry12 { a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9
    ): Curry11<T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry11 { a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10
    ): Curry10<T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        Curry10 { a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
            action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
        }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11
    ): Curry9<T12, T13, T14, T15, T16, T17, T18, T19, T20, R> = Curry9 { a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12
    ): Curry8<T13, T14, T15, T16, T17, T18, T19, T20, R> = Curry8 { a13, a14, a15, a16, a17, a18, a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13
    ): Curry7<T14, T15, T16, T17, T18, T19, T20, R> = Curry7 { a14, a15, a16, a17, a18, a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14
    ): Curry6<T15, T16, T17, T18, T19, T20, R> = Curry6 { a15, a16, a17, a18, a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15
    ): Curry5<T16, T17, T18, T19, T20, R> = Curry5 { a16, a17, a18, a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16
    ): Curry4<T17, T18, T19, T20, R> = Curry4 { a17, a18, a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16,
        a17: T17
    ): Curry3<T18, T19, T20, R> = Curry3 { a18, a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    override operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16,
        a17: T17,
        a18: T18
    ): Curry2<T19, T20, R> = Curry2 { a19, a20 ->
        action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }

    open operator fun invoke(
        a1: T1,
        a2: T2,
        a3: T3,
        a4: T4,
        a5: T5,
        a6: T6,
        a7: T7,
        a8: T8,
        a9: T9,
        a10: T10,
        a11: T11,
        a12: T12,
        a13: T13,
        a14: T14,
        a15: T15,
        a16: T16,
        a17: T17,
        a18: T18,
        a19: T19,
        a20: T20
    ): R = action20(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
}
