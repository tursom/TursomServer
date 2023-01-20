import java.math.BigInteger

enum class Status(val id: Int) : (Int) -> Int {
  S1(1), S2(2), S3(3);

  override fun invoke(i: Int): Int {
    return 0
  }

  infix fun or(status: Status) = status.id or id
  infix fun or(status: Int) = id or status
}

infix fun Int.or(status: Status) = this or status.id

inline fun <reified T> Any.instanceOf() = this is T

fun main() {
  println(1.instanceOf<Int>())
  println(1L.instanceOf<Int>())
  println(1f.instanceOf<Int>())

  //var i1 = 1 or Status.S1
  //
  //val two = BigInteger.valueOf(2)
  //val three = BigInteger.valueOf(3)
  //val ten = BigInteger.TEN
  //var i = BigInteger.ONE
  //repeat(1000) {
  //  i *= two
  //  print("${i / ten / ten / ten % ten}")
  //}
}