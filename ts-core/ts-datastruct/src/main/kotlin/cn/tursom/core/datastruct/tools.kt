package cn.tursom.core.datastruct

operator fun <E> List<E>.get(startIndex: Int = 0, endIndex: Int = size, step: Int = 1): List<E> {
  if (step <= 0) throw IllegalArgumentException("step($step) is negative or zero")
  val fromIndex = when {
    startIndex < 0 -> size + startIndex
    startIndex >= size -> size
    else -> startIndex
  }
  val toIndex = when {
    endIndex < 0 -> size + endIndex + 1
    endIndex >= size -> size
    else -> endIndex
  }
  var targetList = if (fromIndex > toIndex) ReversedList(subList(toIndex, fromIndex)) else subList(fromIndex, toIndex)
  if (step != 1) targetList = targetList step step
  return targetList
}

operator fun <E> List<E>.get(intProgression: IntProgression): List<E> {
  val first = intProgression.first
  val last = intProgression.last
  val step = intProgression.step
  return when {
    step == 0 -> get(first, last + if (last < 0) 0 else 1, 1)
    step < 0 -> get(first + if (last > 0 && first >= 0) 1 else 0, last, -step)
    else -> get(first, last + if (last < 0) 0 else 1, step)
  }
}

infix fun <E> List<E>.step(step: Int): List<E> = StepList(this, step)