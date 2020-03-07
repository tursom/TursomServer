package cn.tursom.mail

data class MailStructure(
  val to: String?, val subject: String?, val html: String?, val text: String? = null,
  val image: Map<String, String>? = null, val attachment: List<String>? = null
)