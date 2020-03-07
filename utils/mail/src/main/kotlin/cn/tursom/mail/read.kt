package cn.tursom.mail

import java.nio.charset.Charset
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import javax.mail.*
import javax.mail.event.MessageCountListener
import javax.mail.internet.InternetAddress

private val threadPool by lazy {
  ScheduledThreadPoolExecutor(
    1,
    ThreadFactory { Thread(it, "MailLoop") })
}

fun addMailListener(
  folder: Folder,
  freq: Long, timeUnit: TimeUnit,
  listener: MessageCountListener,
  newFolder: () -> Folder? = { null }
): ScheduledFuture<*> {
  folder.addMessageCountListener(listener)
  var mailFOlder = folder
  return threadPool.scheduleAtFixedRate({
    try {
      mailFOlder.messageCount
    } catch (e: FolderClosedException) {
      mailFOlder = newFolder() ?: throw e
      mailFOlder.addMessageCountListener(listener)
    } catch (e: Exception) {
      e.printStackTrace()
      throw e
    }
  }, 0, freq, timeUnit)
}

fun addMailListener(
  newFolder: () -> Folder,
  freq: Long, timeUnit: TimeUnit,
  listener: MessageCountListener
): ScheduledFuture<*> = addMailListener(newFolder(), freq, timeUnit, listener, newFolder)

fun getStore(host: String, port: Int, account: String, password: String): Store {
  val props = System.getProperties()
  props["mail.imap.host"] = host
  props["mail.imap.port"] = port
  props["mail.imap.auth"] = "true"
  props["mail.imap.ssl.enable"] = "true"
  props["mail.imap.socketFactory.port"] = port
  props["mail.imap.socketFactory.fallback"] = "false"
  props["mail.imap.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
  val session = Session.getDefaultInstance(props)
  val store = session.getStore("imap")
  store.connect(account, password)
  return store
}

fun getFolder(host: String, port: Int, account: String, password: String): Pair<Store, Folder> {
  val store: Store = getStore(host, port, account, password)
  val folder: Folder = store.getFolder("INBOX")
  folder.open(Folder.READ_ONLY)
  return store to folder
}

fun forEachMail(host: String, port: Int, account: String, password: String, onMsg: (msg: Message) -> Unit) {
  val (store, folder) = getFolder(host, port, account, password)
  // 全部邮件数
  val messages: Array<Message> = folder.messages
  messages.forEach(onMsg)
  folder.close(true)
  store.close()
}

fun getText(message: Message): String? = when (val content: Any = message.content) {
  is String -> content
  is Multipart -> {
    val sb = StringBuilder()
    repeat(content.count) {
      val part: Part = content.getBodyPart(it)
      if (part.contentType.contains("text/plain")) {
        var charsetStart = part.contentType.indexOf("charset=", 0, true)
        if (charsetStart != -1) charsetStart += "charset=".length
        var charsetEnd = part.contentType.indexOf(';', charsetStart)
        if (charsetEnd == -1) {
          charsetEnd = part.contentType.length
        }
        val msg = part.inputStream.bufferedReader(
          if (charsetStart == -1) {
            Charsets.UTF_8
          } else {
            Charset.forName(part.contentType.substring(charsetStart, charsetEnd))
          }
        ).readText()
        sb.append(msg)
      }
    }
    if (sb.isEmpty()) {
      null
    } else {
      sb.toString()
    }
  }
  else -> null
}

fun forEachUnreadMail(host: String, port: Int, account: String, password: String, onMsg: (from: InternetAddress, msg: String) -> Boolean) {
  forEachMail(host, port, account, password) { message ->
    // 跳过已读邮件
    if (message.flags.contains(Flags.Flag.SEEN)) return@forEachMail
    val msg = getText(message)
    val read = when (val text = getText(message)) {
      null -> false
      else -> onMsg(message.from[0] as InternetAddress, text)
    }
    //未读邮件标记为已读
    message.setFlag(Flags.Flag.SEEN, read)
  }
}
