package cn.tursom.mail

import com.sun.mail.util.MailSSLSocketFactory
import java.util.*
import javax.mail.Address
import javax.mail.Session
import javax.mail.internet.InternetAddress

data class MultipleEmailData(
  var host: String?,
  var port: Int?,
  var name: String?,
  var password: String?,
  var from: String?,
  var to: Collection<MailStructure>?
) {
  fun send() {
    val from = from ?: return
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
    //发送邮件
    val transport = session.transport
    transport.connect(host, name, password)
    to?.forEach { (to, subject, html, text, image, attachment) ->
      //邮件内容部分
      val msg = EmailData.getMsg(session, from, subject, html, text, image, attachment)
      transport.sendMessage(msg, arrayOf<Address>(InternetAddress(to)))
    }
    transport.close()
  }

  fun clone(): MultipleEmailData = MultipleEmailData(host, port, name, password, from, to)
}