package cn.tursom.web.utils

fun parseRange(range: String): ArrayList<Pair<Int, Int>> {
  var index = 6
  val rangeList = ArrayList<Pair<Int, Int>>()
  var start = index
  var first = -1
  while (index < range.length) {
    when (range[index]) {
      '-' -> {
        first = range.substring(start, index).toInt()
        start = ++index
      }
      ',' -> {
        rangeList.add(first to range.substring(start, index).toInt())
        index += 2
        start = index
      }
      else -> index++
    }
  }
  if (range.last() != '-') {
    rangeList.add(first to range.substring(start, index).toInt())
  } else {
    rangeList.add(first to -1)
  }
  return rangeList
}

fun parseCookie(cookies: List<String>): Map<String, String> {
  var index = 6
  val cookiesMap = HashMap<String, String>()
  var start = index
  var key = ""
  cookies.forEach { cookie ->
    while (index < cookie.length) {
      when (cookie[index]) {
        '-' -> {
          key = cookie.substring(start, index)
          start = ++index
        }
        ';' -> {
          cookiesMap[key] = cookie.substring(start, index)
          index += 2
          start = index
        }
        else -> index++
      }
    }
    cookiesMap[key] = cookie.substring(start, index)
  }
  return cookiesMap
}