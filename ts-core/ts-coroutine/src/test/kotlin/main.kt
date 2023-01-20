import kotlinx.coroutines.delay

tailrec fun pow(i: Int, n: Int = 1): Int = if (i <= 0) n else pow(i - 1, i * n)

suspend fun main() {
  delay(pow(10).toLong())
  println("finished")
}
