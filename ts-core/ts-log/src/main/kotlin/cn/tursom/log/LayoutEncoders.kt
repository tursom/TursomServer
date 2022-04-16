package cn.tursom.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Layout
import ch.qos.logback.core.encoder.Encoder
import ch.qos.logback.core.encoder.LayoutWrappingEncoder

object LayoutEncoders {
  class JsonLayoutEncoder : CustomLayoutEncoder(JsonLayout())

  operator fun invoke(
    layout: Layout<ILoggingEvent>,
  ): Encoder<ILoggingEvent> = CustomLayoutEncoder(layout)

  open class CustomLayoutEncoder(
    layout: Layout<ILoggingEvent>,
  ) : LayoutWrappingEncoder<ILoggingEvent>() {
    init {
      this.layout = layout
    }
  }
}