package cn.tursom.proxy


fun b() {
  repeat(9) { i -> repeat(i) { j -> print("$j*$i=${i * j} ") };println() }
}

fun a() {
  repeat(9) { i -> repeat(i) { j -> print("$j*$i=${i * j} ") };println() }
}

fun main() {
  (1..9).map { i -> println((1..i).map { "$it*$i=${i * it}" }) }
}

