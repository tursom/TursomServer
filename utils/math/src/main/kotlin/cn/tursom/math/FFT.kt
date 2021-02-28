package cn.tursom.math

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun fft1(a: Array<Complex>): Array<Complex> {
  if (a.size == 1) return a
  val a0 = Array(a.size shr 1) {
    a[it shl 1]
  }
  val a1 = Array(a.size shr 1) {
    a[(it shl 1) + 1]
  }
  fft1(a0)
  fft1(a1)
  val wn = Complex(cos(2 * PI / a.size), sin(2 * PI / a.size))
  val w = Complex(1.0, 0.0)
  repeat(a.size shr 1) { k ->
    a[k] = a0[k] + w * a1[k]
    a[k + (a.size shr 1)] = a0[k] - w * a1[k]
    w.plusAssign(wn)
  }
  return a
}

fun main() {
  val source = Array(8) {
    Complex(sin(it.toDouble()))
  }
  println(source.asList())
  println(fft1(source).asList())
}