package cn.tursom.mail

import java.io.File
import java.net.URL
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.util.ByteArrayDataSource

data class Image(val contentID: String, val dataHandler: DataHandler) {
  constructor(contentID: String, data: DataSource) : this(contentID, DataHandler(data))
  constructor(contentID: String, file: File) : this(contentID, FileDataSource(file))
  constructor(contentID: String, url: URL) : this(contentID, DataHandler(url))
  constructor(contentID: String, data: Any, mimeType: String) : this(contentID, DataHandler(data, mimeType))

  constructor(contentID: String, bytes: ByteArray, type: String, name: String = "") :
    this(contentID, ByteArrayDataSource(bytes, type).apply { this.name = name })

  constructor(contentID: String, data: String, type: String, name: String = "") :
    this(contentID, ByteArrayDataSource(data, type).apply { this.name = name })
}