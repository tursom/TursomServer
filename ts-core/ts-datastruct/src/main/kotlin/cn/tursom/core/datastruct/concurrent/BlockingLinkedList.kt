package cn.tursom.core.datastruct.concurrent

import java.util.*

class BlockingLinkedList<T> : BlockingList<T>(LinkedList<T>())
