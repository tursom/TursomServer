package cn.tursom.core

object TextColor {
  const val reset = "\u001b[0m"

  enum class DisplayType(val code: Int) {
    DEFAULT(0), HIGHLIGHT(1), INTENSITY(2), ITALIC(3), UNDERLINE(4),
    SLOW_BLINK(5), RAPID_BLINK(6), REVERSE(7), INVISIBLE(8), CROSSED_OUT(9),
    UNDERLINE_OFF(24), BLINK_OFF(25), REVERSE_OFF(27), INVISIBLE_OFF(28), CROSSED_OUT_OFF(29),
    OVER_LINE(53), OVER_LINE_OFF(55);

    val strCode = "\u001b[${code}m"
  }

  fun textColor(displayType: DisplayType, textColor: Int, backgroundColor: Int) =
    "\u001B[${displayType.code};$textColor;${backgroundColor}m"

  fun rgbTextColor(r: Int, g: Int, b: Int, displayType: DisplayType = DisplayType.DEFAULT) =
    "\u001B[${displayType.code};38;2;$r;$g;${b}m"

  fun rgbBackgroundColor(r: Int, g: Int, b: Int, displayType: DisplayType = DisplayType.DEFAULT) =
    "\u001B[${displayType.code};48;2;$r;$g;${b}m"

  const val black = "\u001b[30m"
  const val red = "\u001b[31m"
  const val green = "\u001b[32m"
  const val yellow = "\u001b[33m"
  const val blue = "\u001b[34m"
  const val magenta = "\u001b[35m"
  const val cyan = "\u001b[36m"
  const val white = "\u001b[37m"

  const val brightBlack = "\u001b[30;1m"
  const val brightRed = "\u001b[31;1m"
  const val brightGreen = "\u001b[32;1m"
  const val brightYellow = "\u001b[33;1m"
  const val brightBlue = "\u001b[34;1m"
  const val brightMagenta = "\u001b[35;1m"
  const val brightCyan = "\u001b[36;1m"
  const val brightWhite = "\u001b[37;1m"

  val textColor = Array(256) {
    "\u001b[38;5;${it}m"
  }

  fun textColor(color: Int) = textColor[color]
  fun textColor(color: Int, displayType: DisplayType = DisplayType.DEFAULT) =
    "\u001B[${displayType.code};38;5;${color}m"

  const val blackBackground = "\u001b[40m"
  const val redBackground = "\u001b[41m"
  const val greenBackground = "\u001b[42m"
  const val yellowBackground = "\u001b[43m"
  const val blueBackground = "\u001b[44m"
  const val magentaBackground = "\u001b[45m"
  const val cyanBackground = "\u001b[46m"
  const val whiteBackground = "\u001b[47m"

  const val brightBlackBackground = "\u001b[40;1m"
  const val brightRedBackground = "\u001b[41;1m"
  const val brightGreenBackground = "\u001b[42;1m"
  const val brightYellowBackground = "\u001b[43;1m"
  const val brightBlueBackground = "\u001b[44;1m"
  const val brightMagentaBackground = "\u001b[45;1m"
  const val brightCyanBackground = "\u001b[46;1m"
  const val brightWhiteBackground = "\u001b[47;1m"

  val backgroundColor = Array(256) {
    "\u001b[48;5;${it}m"
  }

  const val bold = "\u001b[1m"
  const val underline = "\u001b[4m"
  const val reverseColor = "\u001b[7m"

  const val up = "\u001b[1A"
  const val down = "\u001b[1B"
  const val left = "\u001b[1C"
  const val right = "\u001b[1D"

  const val downToNextLine = "\u001b[1E"
  const val upToPrevLine = "\u001b[1F"

  fun up(step: Int) = "\u001b[${step}A"
  fun down(step: Int) = "\u001b[${step}B"
  fun left(step: Int) = "\u001b[${step}C"
  fun right(step: Int) = "\u001b[${step}D"

  fun downToNextLine(step: Int) = "\u001b[${step}E"
  fun upToPrevLine(step: Int) = "\u001b[${step}F"

  fun jumpToLine(line: Int) = "\u001b[${line}G"
  fun jump(line: Int, row: Int) = "\u001b[${line};${row}H"

  const val cleanScreenToEnd = "\u001b[0J"
  const val cleanScreenFromStart = "\u001b[1J"
  const val cleanScreen = "\u001b[2J"

  const val cleanLineToEnd = "\u001b[0K"
  const val cleanLineFromStart = "\u001b[1K"
  const val cleanLine = "\u001b[2K"

  const val savePosition = "\u001b[s"
  const val loadPosition = "\u001b[u"

  fun rgbTo8Color(R: Int, G: Int, B: Int): Int {
    //8色化处理,取RGB的高1位相与。
    val r1 = R shr 5 and 0x4
    val g1 = G shr 6 and 0x2
    val b1 = B shr 7
    return (r1 or g1 or b1) + 1
  }

  fun rgbTo16Color(R: Int, G: Int, B: Int): Int {
    //16色化处理，取R、G的高1位和B的高2位相与
    val r1 = R shr 4 and 0x8
    val g1 = G shr 5 and 0x4
    val b1 = B shr 6 and 0x3
    return (r1 or g1 or b1) + 1
  }

  fun rgbTo256Color(r: Int, g: Int, b: Int): Int = ((r / 32 shl 5) + (g / 32 shl 2) + b / 64) and 0xFF
}
