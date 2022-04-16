package cn.tursom.mail

import javax.activation.DataSource

data class MailStructure(
  val to: String?, val subject: String?, val html: String?, val text: String? = null,
  val image: Collection<Image>? = null, val attachment: Collection<DataSource>? = null,
)