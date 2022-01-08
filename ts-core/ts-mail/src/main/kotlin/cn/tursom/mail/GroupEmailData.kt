package cn.tursom.mail

import com.sun.mail.util.MailSSLSocketFactory
import java.util.*
import javax.activation.DataSource
import javax.mail.Session
import javax.mail.event.TransportListener
import javax.mail.internet.InternetAddress

/**
 * 用于表示群发邮件的数据类
 * 拥有一个send函数，当运行在TreeDiagram服务器上时可以直接发送邮件
 */
data class GroupEmailData(
  var host: String?, var port: Int?, var name: String?, var password: String?, var from: String?,
  var to: Collection<String>?, var subject: String?, var html: String? = null, var text: String? = null,
  var image: Collection<Image>? = null, var attachment: Collection<DataSource>? = null,
) : Mail {
  override fun send(transportListener: TransportListener?) {
    if (host == null || port == null || name == null || password == null || from == null || to?.isEmpty() != false || subject == null) return
    val props = Properties()
    // props["mail.debug"] = "true"  // 开启debug调试
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
    val msg = EmailData.getMsg(session, from!!, subject, html, text, image, attachment)
    //发送邮件
    val transport = session.transport
    transport.connect(host, name, password)
    transportListener?.apply { transport.addTransportListener(this) }
    transport.sendMessage(msg, to!!.map { InternetAddress(it) }.toTypedArray())
    transport.close()
  }

  fun clone(): GroupEmailData =
    GroupEmailData(host, port, name, password, from, to, subject, html, text, image, attachment)
}