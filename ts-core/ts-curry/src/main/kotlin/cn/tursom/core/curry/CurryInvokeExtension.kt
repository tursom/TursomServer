//package cn.tursom.core.curry
//
//import com.ddbes.kotlin.util.uncheckedCast
//
//operator fun <T1, T2, R> Curry1<T1, Curry1<T2, R>>.invoke(a1: T1, a2: T2): R {
//    return if (this is Curry2) {
//        uncheckedCast<Curry2<T1, T2, R>>()(a1, a2)
//    } else {
//        invoke(a1)(a2)
//    }
//}
//
//operator fun <T1, T2, T3, R> Curry1<T1, Curry1<T2, Curry1<T3, R>>>.invoke(a1: T1, a2: T2, a3: T3): R {
//    return if (this is Curry3) {
//        uncheckedCast<Curry3<T1, T2, T3, R>>()(a1, a2, a3)
//    } else {
//        invoke(a1)(a2)(a3)
//    }
//}
//
//operator fun <T1, T2, T3, T4, R> Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, R>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4
//): R {
//    return if (this is Curry4) {
//        uncheckedCast<Curry4<T1, T2, T3, T4, R>>()(a1, a2, a3, a4)
//    } else {
//        invoke(a1)(a2)(a3)(a4)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, R> Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, R>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5
//): R {
//    return if (this is Curry5) {
//        uncheckedCast<Curry5<T1, T2, T3, T4, T5, R>>()(a1, a2, a3, a4, a5)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, R>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6
//): R {
//    return if (this is Curry6) {
//        uncheckedCast<Curry6<T1, T2, T3, T4, T5, T6, R>>()(a1, a2, a3, a4, a5, a6)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, R>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7
//): R {
//    return if (this is Curry7) {
//        uncheckedCast<Curry7<T1, T2, T3, T4, T5, T6, T7, R>>()(a1, a2, a3, a4, a5, a6, a7)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, R>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8
//): R {
//    return if (this is Curry8) {
//        uncheckedCast<Curry8<T1, T2, T3, T4, T5, T6, T7, T8, R>>()(a1, a2, a3, a4, a5, a6, a7, a8)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, R>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9
//): R {
//    return if (this is Curry9) {
//        uncheckedCast<Curry9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>>()(a1, a2, a3, a4, a5, a6, a7, a8, a9)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10, R>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10
//): R {
//    return if (this is Curry10) {
//        uncheckedCast<Curry10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>>()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, R>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11
//): R {
//    return if (this is Curry11) {
//        uncheckedCast<Curry11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, R>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12
//): R {
//    return if (this is Curry12) {
//        uncheckedCast<Curry12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, R>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13
//): R {
//    return if (this is Curry13) {
//        uncheckedCast<Curry13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, Curry1<T14, R>>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13,
//    a14: T14
//): R {
//    return if (this is Curry14) {
//        uncheckedCast<Curry14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, Curry1<T14, Curry1<T15, R>>>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13,
//    a14: T14, a15: T15
//): R {
//    return if (this is Curry15) {
//        uncheckedCast<Curry15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, Curry1<T14, Curry1<T15, Curry1<T16, R>>>>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13,
//    a14: T14, a15: T15, a16: T16
//): R {
//    return if (this is Curry16) {
//        uncheckedCast<Curry16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, Curry1<T14, Curry1<T15, Curry1<T16, Curry1<T17, R>>>>>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13,
//    a14: T14, a15: T15, a16: T16, a17: T17
//): R {
//    return if (this is Curry17) {
//        uncheckedCast<Curry17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, Curry1<T14, Curry1<T15, Curry1<T16, Curry1<T17, Curry1<T18,
//            R>>>>>>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13,
//    a14: T14, a15: T15, a16: T16, a17: T17, a18: T18
//): R {
//    return if (this is Curry18) {
//        uncheckedCast<Curry18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, Curry1<T14, Curry1<T15, Curry1<T16, Curry1<T17, Curry1<T18, Curry1<T19,
//            R>>>>>>>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13,
//    a14: T14, a15: T15, a16: T16, a17: T17, a18: T18, a19: T19
//): R {
//    return if (this is Curry19) {
//        uncheckedCast<Curry19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)
//    }
//}
//
//operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>
//    Curry1<T1, Curry1<T2, Curry1<T3, Curry1<T4, Curry1<T5, Curry1<T6, Curry1<T7, Curry1<T8, Curry1<T9, Curry1<T10,
//        Curry1<T11, Curry1<T12, Curry1<T13, Curry1<T14, Curry1<T15, Curry1<T16, Curry1<T17, Curry1<T18, Curry1<T19,
//            Curry1<T20, R>>>>>>>>>>>>>>>>>>>>.invoke(
//    a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9, a10: T10, a11: T11, a12: T12, a13: T13,
//    a14: T14, a15: T15, a16: T16, a17: T17, a18: T18, a19: T19, a20: T20
//): R {
//    return if (this is Curry20) {
//        uncheckedCast<Curry20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>>()(
//            a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
//    } else {
//        invoke(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)
//    }
//}
