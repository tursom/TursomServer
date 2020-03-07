package cn.tursom.mail

import java.util.*

data class MailStructure(
  val to: String?, val subject: String?, val html: String?, val text: String? = null,
  val image: Map<String, String>? = null, val attachment: Array<String>? = null
) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as MailStructure

    if (to != other.to) return false
    if (subject != other.subject) return false
    if (html != other.html) return false
    if (text != other.text) return false
    if (image != other.image) return false
    if (!Arrays.equals(attachment, other.attachment)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = to?.hashCode() ?: 0
    result = 31 * result + (subject?.hashCode() ?: 0)
    result = 31 * result + (html?.hashCode() ?: 0)
    result = 31 * result + (text?.hashCode() ?: 0)
    result = 31 * result + (image?.hashCode() ?: 0)
    result = 31 * result + (attachment?.let { Arrays.hashCode(it) } ?: 0)
    return result
  }
}