package cn.tursom.core.curry

fun example(f: (Int) -> (Int) -> (Int) -> Int) {
  f(1)(2)(3)

  f(1, 2, 3)
}

fun example2(f: (Int, Int, Int) -> Int) {
  f(1, 2, 3)

  f(1, 2)(3)
}
