package cn.tursom.mail

import cn.tursom.core.base64
import com.sun.mail.util.MailSSLSocketFactory
import java.net.URL
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Address
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

data class MultipleEmailData(
  var host: String?,
  var port: Int?,
  var name: String?,
  var password: String?,
  var from: String?,
  var to: List<MailStructure>?
) {
  fun send() {
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
    to?.forEach { structure ->
      //邮件内容部分
      val msg = MimeMessage(session)
      val multipart = MimeMultipart()
      // 添加文本
      if (structure.html ?: "null" != "null") {
        val htmlBodyPart = MimeBodyPart()
        htmlBodyPart.setContent(structure.html, "text/html;charset=UTF-8")
        multipart.addBodyPart(htmlBodyPart)
      } else {
        val textPart = MimeBodyPart()
        textPart.setText(structure.text)
        multipart.addBodyPart(textPart)
      }
      //添加图片
      structure.image?.forEach {
        //创建用于保存图片的MimeBodyPart对象，并将它保存到MimeMultipart中
        val gifBodyPart = MimeBodyPart()
        if (it.value.startsWith("http://") or it.value.startsWith("https://")) {
          gifBodyPart.dataHandler = DataHandler(URL(it.value))
        } else {
          val fds = FileDataSource(it.value)//图片所在的目录的绝对路径
          gifBodyPart.dataHandler = DataHandler(fds)
        }
        gifBodyPart.contentID = it.key   //cid的值
        multipart.addBodyPart(gifBodyPart)
      }
      //添加附件
      structure.attachment?.forEach { fileName ->
        val adjunct = MimeBodyPart()
        val fileDataSource = FileDataSource(fileName)
        adjunct.dataHandler = DataHandler(fileDataSource)
        adjunct.fileName = fileDataSource.name.base64()
        multipart.addBodyPart(adjunct)
      }
      msg.setContent(multipart)
      //邮件主题
      msg.subject = structure.subject
      //邮件发送者
      msg.setFrom(InternetAddress(from))
      transport.sendMessage(msg, arrayOf<Address>(InternetAddress(structure.to)))
    }
    transport.close()
  }

  fun clone(): MultipleEmailData = MultipleEmailData(host, port, name, password, from, to)
}