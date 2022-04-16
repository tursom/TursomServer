package cn.tursom.core

class Switch(
  val value: Any? = null,
  var consider: Boolean = false,
) {
  object BreakException : Exception()

  inline val `break`: Unit
    inline get() {
      throw BreakException
    }

  inline fun case(value: Any?, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(block: Switch.() -> Unit) {
  val switch = Switch()
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

inline fun <T : Any?> switch(value: T, block: Switch.() -> Unit) {
  val switch = Switch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

class ByteSwitch(
  val value: Byte,
  var consider: Boolean = false,
) {
  inline val `break`: Unit
    inline get() {
      throw Switch.BreakException
    }

  inline fun case(value: Byte, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(value: Byte, block: ByteSwitch.() -> Unit) {
  val switch = ByteSwitch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

class ShortSwitch(
  val value: Short,
  var consider: Boolean = false,
) {
  inline val `break`: Unit
    inline get() {
      throw Switch.BreakException
    }

  inline fun case(value: Short, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(value: Short, block: ShortSwitch.() -> Unit) {
  val switch = ShortSwitch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

class IntSwitch(
  val value: Int,
  var consider: Boolean = false,
) {
  inline val `break`: Unit
    inline get() {
      throw Switch.BreakException
    }

  inline fun case(value: Int, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(value: Int, block: IntSwitch.() -> Unit) {
  val switch = IntSwitch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

class LongSwitch(
  val value: Long,
  var consider: Boolean = false,
) {
  inline val `break`: Unit
    inline get() {
      throw Switch.BreakException
    }

  inline fun case(value: Long, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(value: Long, block: LongSwitch.() -> Unit) {
  val switch = LongSwitch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

class FloatSwitch(
  val value: Float,
  var consider: Boolean = false,
) {
  inline val `break`: Unit
    inline get() {
      throw Switch.BreakException
    }

  inline fun case(value: Float, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(value: Float, block: FloatSwitch.() -> Unit) {
  val switch = FloatSwitch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

class DoubleSwitch(
  val value: Double,
  var consider: Boolean = false,
) {
  inline val `break`: Unit
    inline get() {
      throw Switch.BreakException
    }

  inline fun case(value: Double, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(value: Double, block: DoubleSwitch.() -> Unit) {
  val switch = DoubleSwitch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

class CharSwitch(
  val value: Char,
  var consider: Boolean = false,
) {
  inline val `break`: Unit
    inline get() {
      throw Switch.BreakException
    }

  inline fun case(value: Char, block: () -> Unit = {}) {
    if (consider || value == value) {
      consider = true
      block()
    }
  }

  inline fun case(condition: Boolean = false, block: () -> Unit = {}) {
    if (consider || condition) {
      consider = true
      block()
    }
  }
}

inline fun switch(value: Char, block: CharSwitch.() -> Unit) {
  val switch = CharSwitch(value)
  try {
    switch.block()
  } catch (e: Switch.BreakException) {
  }
}

