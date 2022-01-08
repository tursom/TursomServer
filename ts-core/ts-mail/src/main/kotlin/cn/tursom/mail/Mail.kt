package cn.tursom.mail

import javax.mail.event.TransportListener

interface Mail {
  fun send(transportListener: TransportListener? = null)
}