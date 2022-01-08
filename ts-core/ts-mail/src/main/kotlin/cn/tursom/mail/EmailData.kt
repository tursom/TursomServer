package cn.tursom.mail

import com.sun.mail.util.MailSSLSocketFactory
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.Address
import javax.mail.Session
import javax.mail.event.TransportListener
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

/**
 * 用于发送一个邮件的所有信息
 *
 * @param host smtp服务器地址
 * @param port smtp服务器端口，默认465
 * @param name 邮箱用户名
 * @param password 邮箱密码
 * @param from 发送邮箱
 * @param to 目标邮箱
 * @param subject 邮件主题
 * @param html 邮件主题内容
 * @param text html为空时的邮件主题内容
 * @param image 图片
 * @param attachment 附件
 */
data class EmailData(
  var host: String?, var port: Int?, var name: String?, var password: String?, var from: String?,
  var to: String?, var subject: String?, var html: String? = null, var text: String? = null,
  var image: Collection<Image>? = null, var attachment: Collection<DataSource>? = null,
) : Mail {
  /**
   * 发送邮件
   */
  override fun send(transportListener: TransportListener?) {
    if (host == null || port == null || name == null || password == null || from == null || to == null || subject == null) return
    val props = Properties()
//		props["mail.debug"] = "true"  // 开启debug调试
    props["mail.smtp.auth"] = "true"  // 发送服务器需要身份验证
    props["mail.smtp.host"] = host  // 设置邮件服务器主机名
    props["mail.transport.protocol"] = "smtps"  // 发送邮件协议名称
    props["mail.smtp.port"] = port
    val sf = MailSSLSocketFactory()
    sf.isTrustAllHosts = true
    props["mail.smtp.ssl.enable"] = "true"
    props["mail.smtp.ssl.socketFactory"] = sf

    val session = Session.getInstance(props)
    //邮件内容部分
    val msg = getMsg(session, from!!, subject, html, text, image, attachment)
    //发送邮件
    val transport = session.transport
    transport.connect(host, name, password)
    transportListener?.apply { transport.addTransportListener(this) }
    transport.sendMessage(msg, arrayOf<Address>(InternetAddress(to)))
    transport.close()
  }

  fun clone(): EmailData = EmailData(host, port, name, password, from, to, subject, html, text, image, attachment)

  companion object {
    fun getMsg(
      session: Session,
      from: String,
      subject: String?,
      html: String?,
      text: String?,
      image: Collection<Image>?,
      attachment: Collection<DataSource>?,
    ): MimeMessage {
      //邮件内容部分
      val msg = MimeMessage(session)
      val multipart = MimeMultipart()
      // 添加文本
      when {
        html != null -> {
          val htmlBodyPart = MimeBodyPart()
          htmlBodyPart.setContent(html, "text/html;charset=UTF-8")
          multipart.addBodyPart(htmlBodyPart)
        }
        text != null -> {
          val textPart = MimeBodyPart()
          textPart.setText(text)
          multipart.addBodyPart(textPart)
        }
      }
      //添加图片
      image?.forEach { (contentID, dataHandler) ->
        //创建用于保存图片的MimeBodyPart对象，并将它保存到MimeMultipart中
        val gifBodyPart = MimeBodyPart()
        gifBodyPart.dataHandler = dataHandler
        gifBodyPart.contentID = contentID
        multipart.addBodyPart(gifBodyPart)
      }
      //添加附件
      attachment?.forEach {
        val adjunct = MimeBodyPart()
        adjunct.dataHandler = DataHandler(it)
        adjunct.fileName = it.name
        multipart.addBodyPart(adjunct)
      }
      msg.setContent(multipart)
      //邮件主题
      msg.subject = subject
      //邮件发送者
      msg.setFrom(InternetAddress(from))
      return msg
    }
  }
}