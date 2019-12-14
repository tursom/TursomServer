package cn.tursom.web.utils

import cn.tursom.core.datastruct.StringRadixTree

@Suppress("EnumEntryName", "unused", "SpellCheckingInspection")
enum class ContextTypeEnum(val key: String, val value: String) {
  aac("aac", "audio/aac"),
  abw("abw", "application/x-abiword"),
  arc("arc", "application/x-freearc"),
  avi("avi", "video/x-msvideo"),
  azw("azw", "aapplication/vnd.amazon.ebook"),
  bin("bin", "application/octet-stream"),
  bmp("bmp", "image/bmp"),
  bz("bz", "application/x-bzip"),
  bz2("bz2", "application/x-bzip2"),
  csh("csh", "application/x-csh"),
  css("css", "itext/css; charset=UTF-8"),
  csv("csv", "itext/csv; charset=UTF-8"),
  doc("doc", "application/msword"),
  docx("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
  eot("eot", "application/vnd.ms-fontobject"),
  epub("epub", "application/epub+zip"),
  gif("gif", "image/gif"),
  html("html", "text/html; charset=UTF-8"),
  htm("htm", "text/html; charset=UTF-8"),
  ico("ico", "image/vnd.microsoft.icon"),
  ics("ics", "text/calendar; charset=UTF-8"),
  jar("jar", "application/java-archive"),
  jpeg("jpeg", "image/jpeg"),
  jpg("jpg", "image/jpeg"),
  js("js", "text/javascript; charset=UTF-8"),
  json("json", "application/json; charset=UTF-8"),
  jsonld("jsonld", "application/ld+json"),
  mid("mid", "audio/midi"),
  midi("midi", "audio/midi"),
  mjs("mjs", "text/javascript"),
  mp3("mp3", "audio/mpeg"),
  mp4("mp4", "audio/mp4"),
  mpeg("mpeg", "video/mpeg"),
  mpkg("mpkg", "application/vnd.apple.installer+xml"),
  odp("odp", "application/vnd.oasis.opendocument.presentation"),
  ods("ods", "application/vnd.oasis.opendocument.spreadsheet"),
  odt("odt", "application/vnd.oasis.opendocument.text"),
  oga("oga", "audio/ogg"),
  ogg("ogg", "application/ogg"),
  ogv("ogv", "video/ogg"),
  ogx("ogx", "application/ogg"),
  otf("otf", "font/otf"),
  png("png", "image/png"),
  pdf("pdf", "application/pdf"),
  ppt("ppt", "application/vnd.ms-powerpoint"),
  pptx("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
  rar("rar", "application/x-rar-compressed"),
  rtf("rtf", "application/rtf"),
  sh("sh", "application/x-sh"),
  svg("svg", "image/svg+xml"),
  swf("swf", "application/x-shockwave-flash"),
  tar("tar", "application/x-tar"),
  tif("tif", "image/tiff"),
  tiff("tiff", "image/tiff"),
  ttf("ttf", "font/ttf"),
  txt("txt", "text/plain; charset=UTF-8"),
  vsd("vsd", "application/vnd.visio"),
  wav("wav", "audio/wav"),
  weba("weba", "audio/webm"),
  webm("webm", "video/webm"),
  webp("webp", "image/webp"),
  woff("woff", "font/woff"),
  woff2("woff2", "font/woff2"),
  xhtml("xhtml", "application/xhtml+xml"),
  xls("xls", "application/vnd.ms-excel"),
  xlsx("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
  xml("xml", "text/xml; charset=UTF-8"),
  xul("xul", "application/vnd.mozilla.xul+xml"),
  zip("zip", "application/zip"),
  `3gp`("3gp", "video/3gpp"),
  `3g2`("3g2", "ivideo/3gpp2"),
  `7z`("7z", "application/x-7z-compressed");

  companion object {
    private val router = StringRadixTree<ContextTypeEnum>()

    init {
      router["aac"] = aac
      router["abw"] = abw
      router["arc"] = arc
      router["avi"] = avi
      router["azw"] = azw
      router["bin"] = bin
      router["bmp"] = bmp
      router["bz"] = bz
      router["bz2"] = bz2
      router["csh"] = csh
      router["css"] = css
      router["csv"] = csv
      router["doc"] = doc
      router["docx"] = docx
      router["eot"] = eot
      router["epub"] = epub
      router["gif"] = gif
      router["html"] = html
      router["htm"] = htm
      router["ico"] = ico
      router["ics"] = ics
      router["jar"] = jar
      router["jpeg"] = jpeg
      router["jpg"] = jpg
      router["js"] = js
      router["json"] = json
      router["jsonld"] = jsonld
      router["mid"] = mid
      router["midi"] = midi
      router["mjs"] = mjs
      router["mp3"] = mp3
      router["mp4"] = mp4
      router["mpeg"] = mpeg
      router["mpkg"] = mpkg
      router["odp"] = odp
      router["ods"] = ods
      router["odt"] = odt
      router["oga"] = oga
      router["ogg"] = ogg
      router["ogv"] = ogv
      router["ogx"] = ogx
      router["otf"] = otf
      router["png"] = png
      router["pdf"] = pdf
      router["ppt"] = ppt
      router["pptx"] = pptx
      router["rar"] = rar
      router["rtf"] = rtf
      router["sh"] = sh
      router["svg"] = svg
      router["swf"] = swf
      router["tar"] = tar
      router["tif"] = tif
      router["tiff"] = tiff
      router["ttf"] = ttf
      router["txt"] = txt
      router["vsd"] = vsd
      router["wav"] = wav
      router["weba"] = weba
      router["webm"] = webm
      router["webp"] = webp
      router["woff"] = woff
      router["woff2"] = woff2
      router["xhtml"] = xhtml
      router["xls"] = xls
      router["xlsx"] = xlsx
      router["xml"] = xml
      router["xul"] = xul
      router["zip"] = zip
      router["3gp"] = `3gp`
      router["3g2"] = `3g2`
      router["7z"] = `7z`
    }

    operator fun get(value: String) = router[value] ?: throw IllegalAccessException()
  }
}
