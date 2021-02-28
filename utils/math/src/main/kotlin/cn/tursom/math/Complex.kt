package cn.tursom.math

import java.util.*
import kotlin.math.cos
import kotlin.math.sin


data class Complex(
  var r: Double = 0.0,
  var i: Double = 0.0,
) {
  constructor(r: Int, i: Int) : this(r.toDouble(), i.toDouble())

  operator fun plus(complex: Complex) = Complex(r + complex.r, i + complex.i)
  operator fun minus(complex: Complex) = Complex(r - complex.r, i - complex.i)
  operator fun times(complex: Complex) = Complex(r * complex.r, i * complex.i)
  operator fun plusAssign(complex: Complex) {
    r += complex.r
    i += complex.i
  }

  operator fun timesAssign(complex: Complex) {
    r *= complex.r
    i *= complex.i
  }

  // return abs/modulus/magnitude
  fun abs(): Double {
    return Math.hypot(r, i)
  }

  // return angle/phase/argument, normalized to be between -pi and pi
  fun phase(): Double {
    return Math.atan2(i, r)
  }

  // return a new object whose value is (this * alpha)
  fun scale(alpha: Double): Complex {
    return Complex(alpha * r, alpha * i)
  }

  fun conjugate(): Complex {
    return Complex(r, -i)
  }

  fun reciprocal(): Complex {
    val scale = r * r + i * i
    return Complex(r / scale, -i / scale)
  }

  // return the real or imaginary part
  fun re(): Double {
    return r
  }

  fun im(): Double {
    return r
  }

  // return a / b
  fun divides(b: Complex): Complex {
    val a = this
    return a.times(b.reciprocal())
  }

  // return a new Complex object whose value is the complex exponential of this
  fun exp(): Complex {
    return Complex(Math.exp(r) * Math.cos(i), Math.exp(r) * Math.sin(i))
  }

  // return a new Complex object whose value is the complex sine of this
  fun sin(): Complex {
    return Complex(Math.sin(r) * Math.cosh(i), Math.cos(r) * Math.sinh(i))
  }

  // return a new Complex object whose value is the complex cosine of this
  fun cos(): Complex {
    return Complex(Math.cos(r) * Math.cosh(i), -Math.sin(r) * Math.sinh(i))
  }

  // return a new Complex object whose value is the complex tangent of this
  fun tan(): Complex {
    return sin().divides(cos())
  }

  override fun toString(): String {
    return "($r,$i)"
  }
}

object FFT {
  fun fft(x: Array<out Complex>): Array<Complex> {
    val n = x.size
    if (n == 1) return arrayOf(x[0])
    require(n % 2 == 0) { "n is not a power of 2" }
    val even = Array(n / 2) { k ->
      x[2 * k]
    }
    val evenFFT = fft(even)
    for (k in 0 until n / 2) {
      even[k] = x[2 * k + 1]
    }
    val oddFFT = fft(even)
    val y = arrayOfNulls<Complex>(n)
    for (k in 0 until n / 2) {
      val kth = -2 * k * Math.PI / n
      val wk = Complex(cos(kth), sin(kth))
      y[k] = evenFFT[k].plus(wk.times(oddFFT[k]))
      y[k + n / 2] = evenFFT[k].minus(wk.times(oddFFT[k]))
    }
    @Suppress("UNCHECKED_CAST")
    return y as Array<Complex>
  }

  // compute the inverse FFT of x[], assuming its length n is a power of 2
  fun ifft(x: Array<out Complex>): Array<out Complex> {
    val n = x.size
    var y = Array(n) { i ->
      x[i].conjugate()
    }

    // compute forward FFT
    y = fft(y as Array<out Complex>)

    // take conjugate again
    for (i in 0 until n) {
      y[i] = y[i].conjugate()
    }

    // divide by n
    for (i in 0 until n) {
      y[i] = y[i].scale(1.0 / n)
    }
    return y
  }

  fun cconvolve(x: Array<out Complex>, y: Array<out Complex>): Array<out Complex> {
    require(x.size == y.size) { "Dimensions don't agree" }
    val n = x.size

    val a = fft(x)
    val b = fft(y)

    val c = Array(n) { i ->
      a[i].times(b[i])
    }
    return ifft(c)
  }

  fun convolve(x: Array<out Complex>, y: Array<out Complex>): Array<out Complex> {
    val ZERO = Complex(0, 0)
    val a = Array(2 * x.size) { i ->
      if (i in x.indices) {
        x[i]
      } else {
        ZERO
      }
    }
    val b = Array(2 * y.size) { i ->
      if (i in y.indices) {
        y[i]
      } else {
        ZERO
      }
    }
    return cconvolve(a, b)
  }

  // compute the DFT of x[] via brute force (n^2 time)
  fun dft(x: Array<out Complex>): Array<out Complex> {
    val n = x.size
    val ZERO = Complex(0, 0)
    val y = Array<Complex>(n) { k ->
      val data = ZERO
      for (j in 0 until n) {
        val power = k * j % n
        val kth = -2 * power * Math.PI / n
        val wkj = Complex(cos(kth), sin(kth))
        data.plusAssign(x[j] * wkj)
      }
      data
    }
    return y
  }

  // display an array of Complex numbers to standard output
  fun show(x: Array<out Complex?>, title: String?) {
    println(title)
    println("-------------------")
    for (i in x.indices) {
      println(x[i])
    }
    println()
  }

  /***************************************************************************
   * Test client and sample execution
   *
   * % java FFT 4
   * x
   * -------------------
   * -0.03480425839330703
   * 0.07910192950176387
   * 0.7233322451735928
   * 0.1659819820667019
   *
   * y = fft(x)
   * -------------------
   * 0.9336118983487516
   * -0.7581365035668999 + 0.08688005256493803i
   * 0.44344407521182005
   * -0.7581365035668999 - 0.08688005256493803i
   *
   * z = ifft(y)
   * -------------------
   * -0.03480425839330703
   * 0.07910192950176387 + 2.6599344570851287E-18i
   * 0.7233322451735928
   * 0.1659819820667019 - 2.6599344570851287E-18i
   *
   * c = cconvolve(x, x)
   * -------------------
   * 0.5506798633981853
   * 0.23461407150576394 - 4.033186818023279E-18i
   * -0.016542951108772352
   * 0.10288019294318276 + 4.033186818023279E-18i
   *
   * d = convolve(x, x)
   * -------------------
   * 0.001211336402308083 - 3.122502256758253E-17i
   * -0.005506167987577068 - 5.058885073636224E-17i
   * -0.044092969479563274 + 2.1934338938072244E-18i
   * 0.10288019294318276 - 3.6147323062478115E-17i
   * 0.5494685269958772 + 3.122502256758253E-17i
   * 0.240120239493341 + 4.655566391833896E-17i
   * 0.02755001837079092 - 2.1934338938072244E-18i
   * 4.01805098805014E-17i
   *
   */
  @JvmStatic
  fun main(args: Array<String>) {
    //val n = args[0].toInt()
    val n = 8
    val x = Array(n) { i ->
      Complex(sin(i.toDouble()), 0.0)
    }

    show(x, "x")

    // FFT of original data
    val y = fft(x)
    show(y, "y = fft(x)")

    // FFT of original data
    val y2 = dft(x)
    show(y2, "y2 = dft(x)")

    // take inverse FFT
    val z = ifft(y)
    show(z, "z = ifft(y)")

    // circular convolution of x with itself
    val c = cconvolve(x, x)
    show(c, "c = cconvolve(x, x)")

    // linear convolution of x with itself
    val d = convolve(x, x)
    show(d, "d = convolve(x, x)")
  }
}