import java.math.BigInteger

fun main() {
  val two = BigInteger.valueOf(2)
  val three = BigInteger.valueOf(3)
  val ten = BigInteger.TEN
  var i = BigInteger.ONE
  repeat(1000) {
    i *= two
    print("${i / ten / ten / ten % ten}")
  }
}