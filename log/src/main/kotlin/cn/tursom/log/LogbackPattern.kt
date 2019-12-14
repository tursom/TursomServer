package cn.tursom.log

@Suppress("unused", "MemberVisibilityCanBePrivate")
object LogbackPattern {
  enum class ColorEnum(val code: String) {
    BLACK("black"), RED("red"), GREEN("green"), YELLOW("yellow"), BLUE("blue"),
    MAGENTA("magenta"), CYAN("cyan"), WHITE("white"), GRAY("gray"),
    BOLD_RED("boldRed"), BOLD_GREEN("boldGreen"), BOLD_YELLOW("boldYellow"),
    BOLD_BLUE("boldBlue"), BOLD_MAGENTA("boldMagenta"), BOLD_CYAN("boldCyan"),
    BOLD_WHITE("boldWhite"), HIGHLIGHT("highlight");

    operator fun unaryPlus() = "%$this"
    operator fun invoke(content: String) = "$this($content)"
    operator fun get(content: String) = "$this{$content}"
    override fun toString(): String = code
  }

  object Color {
    val black = ColorEnum.BLACK
    val red = ColorEnum.RED
    val green = ColorEnum.GREEN
    val yellow = ColorEnum.YELLOW
    val blue = ColorEnum.BLUE
    val magenta = ColorEnum.MAGENTA
    val cyan = ColorEnum.CYAN
    val white = ColorEnum.WHITE
    val gray = ColorEnum.GRAY
    val boldRed = ColorEnum.BOLD_RED
    val boldGreen = ColorEnum.BOLD_GREEN
    val boldYellow = ColorEnum.BOLD_YELLOW
    val boldBlue = ColorEnum.BOLD_BLUE
    val boldMagenta = ColorEnum.BOLD_MAGENTA
    val boldCyan = ColorEnum.BOLD_CYAN
    val boldWhite = ColorEnum.BOLD_WHITE
    val highlight = ColorEnum.HIGHLIGHT
  }

  inline fun color(action: Color.() -> ColorEnum) = Color.action()

  const val nextLine = "n"
  const val logger = "c"
  const val logger_ = "lo"
  const val logger__ = "logger"
  const val clazz = "C"
  const val class_ = "class"
  const val date = "d"
  const val date_ = "date"
  const val caller = "caller"
  const val line = "L"
  const val line_ = "line"
  const val message = "m"
  const val message_ = "msg"
  const val message__ = "message"
  const val method = "M"
  const val method_ = "method"
  const val level = "p"
  const val level_ = "le"
  const val level__ = "level"
  const val relative = "r"
  const val relative_ = "relative"
  const val thread = "t"
  const val thread_ = "thread"
  const val mdc = "X"
  const val mdc_ = "mdc"
  const val throwable = "throwable"
  const val exception = "ex"
  const val exception_ = "exception"
  const val exception_short = "ex{short}"
  const val exception_full = "ex{full}"

  fun String.finish() = +this
  operator fun String.unaryPlus() = "%$this"
  operator fun String.invoke(content: Any) = "$this($content)"
  operator fun String.get(content: Any) = "$this{$content}"

  fun String.size(min: Int) = "$min$this"
  fun String.size(min: Int? = null, max: Int) = "${min?.toString() ?: ""}.$max$this"

  fun String.left() = "-$this"
  fun String.left(min: Int) = "-$min$this"
  fun String.left(min: Int? = null, max: Int) = "-${min?.toString() ?: ""}.$max$this"
  fun String.right() = this
  fun String.right(min: Int) = "$min$this"
  fun String.right(min: Int? = null, max: Int) = "${min?.toString() ?: ""}.$max$this"

  inline fun make(action: LogbackPattern.() -> String) = this.action()
}