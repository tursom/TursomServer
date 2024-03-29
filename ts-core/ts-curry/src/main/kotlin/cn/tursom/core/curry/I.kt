package cn.tursom.core.curry

object I {
  inline operator fun <R> invoke(f: () -> R) = f()
  inline operator fun <T1, R> invoke(f: (T1) -> R, a1: T1) = f(a1)
  inline operator fun <T1, T2, R> invoke(f: (T1, T2) -> R, a1: T1, a2: T2) = f(a1, a2)
  inline operator fun <T1, T2, T3, R> invoke(f: (T1, T2, T3) -> R, a1: T1, a2: T2, a3: T3) = f(a1, a2, a3)
  inline operator fun <T1, T2, T3, T4, R> invoke(f: (T1, T2, T3, T4) -> R, a1: T1, a2: T2, a3: T3, a4: T4) =
    f(a1, a2, a3, a4)

  inline operator fun <T1, T2, T3, T4, T5, R> invoke(
    f: (T1, T2, T3, T4, T5) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  inline operator fun <T1, T2, T3, T4, T5, T6, R> invoke(
    f: (T1, T2, T3, T4, T5, T6) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
    a20: T20,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
}

object F {
  inline operator fun <R> invoke(f: () -> R) = f()

  inline operator fun <T1, R> invoke(f: (T1) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, R> invoke(f: (T1, T2) -> R, a1: T1, a2: T2) = f(a1, a2)
  operator fun <T1, T2, R> invoke(f: (T1, T2) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, T3, R> invoke(f: (T1, T2, T3) -> R, a1: T1, a2: T2, a3: T3) = f(a1, a2, a3)
  operator fun <T1, T2, T3, R> invoke(f: (T1, T2, T3) -> R, a1: T1, a2: T2) = f(a1, a2)
  operator fun <T1, T2, T3, R> invoke(f: (T1, T2, T3) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, T3, T4, R> invoke(f: (T1, T2, T3, T4) -> R, a1: T1, a2: T2, a3: T3, a4: T4) =
    f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, R> invoke(f: (T1, T2, T3, T4) -> R, a1: T1, a2: T2, a3: T3) = f(a1, a2, a3)
  operator fun <T1, T2, T3, T4, R> invoke(f: (T1, T2, T3, T4) -> R, a1: T1, a2: T2) = f(a1, a2)
  operator fun <T1, T2, T3, T4, R> invoke(f: (T1, T2, T3, T4) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, R> invoke(
    f: (T1, T2, T3, T4, T5) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, R> invoke(f: (T1, T2, T3, T4, T5) -> R, a1: T1, a2: T2, a3: T3, a4: T4) =
    f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, R> invoke(f: (T1, T2, T3, T4, T5) -> R, a1: T1, a2: T2, a3: T3) = f(a1, a2, a3)
  operator fun <T1, T2, T3, T4, T5, R> invoke(f: (T1, T2, T3, T4, T5) -> R, a1: T1, a2: T2) = f(a1, a2)
  operator fun <T1, T2, T3, T4, T5, R> invoke(f: (T1, T2, T3, T4, T5) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, R> invoke(
    f: (T1, T2, T3, T4, T5, T6) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, R> invoke(
    f: (T1, T2, T3, T4, T5, T6) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, R> invoke(f: (T1, T2, T3, T4, T5, T6) -> R, a1: T1, a2: T2, a3: T3, a4: T4) =
    f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, R> invoke(f: (T1, T2, T3, T4, T5, T6) -> R, a1: T1, a2: T2, a3: T3) =
    f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, R> invoke(f: (T1, T2, T3, T4, T5, T6) -> R, a1: T1, a2: T2) = f(a1, a2)
  operator fun <T1, T2, T3, T4, T5, T6, R> invoke(f: (T1, T2, T3, T4, T5, T6) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(f: (T1, T2, T3, T4, T5, T6, T7) -> R, a1: T1, a2: T2, a3: T3) =
    f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(f: (T1, T2, T3, T4, T5, T6, T7) -> R, a1: T1, a2: T2) = f(a1, a2)
  operator fun <T1, T2, T3, T4, T5, T6, T7, R> invoke(f: (T1, T2, T3, T4, T5, T6, T7) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R, a1: T1, a2: T2) =
    f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> invoke(f: (T1, T2, T3, T4, T5, T6, T7, T8) -> R, a1: T1) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> invoke(f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R, a1: T1) =
    f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    a1: T1,
  ) = f(a1)

  inline operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
    a20: T20,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
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
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8, a9)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
  ) = f(a1, a2, a3, a4, a5, a6, a7, a8)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
  ) = f(a1, a2, a3, a4, a5, a6, a7)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
  ) = f(a1, a2, a3, a4, a5, a6)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
  ) = f(a1, a2, a3, a4, a5)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
  ) = f(a1, a2, a3, a4)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
    a3: T3,
  ) = f(a1, a2, a3)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
    a2: T2,
  ) = f(a1, a2)

  operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> invoke(
    f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    a1: T1,
  ) = f(a1)
}