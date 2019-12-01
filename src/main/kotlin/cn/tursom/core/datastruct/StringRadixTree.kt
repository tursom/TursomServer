package cn.tursom.core.datastruct

/**
 * 基数树，查找优化
 * 插入的时候会自发保持基数树的特性，不需要额外的检查
 * 删除节点的时候可能会导致冗余，需要额外的检查，以保持基数树的特性
 */
class StringRadixTree<T> {
  private val root = Node<T>()

  /**
   * 为一个位置设置一个值
   * @param value 需要设置的值，为 null 时会自动删除这个节点
   * @return 节点的旧值
   */
  operator fun set(route: String, value: T?): T? {
    val context = Context(route)
    var node: Node<T>? = root
    var prev: Node<T> = root
    var oldValue: T? = null
    while (node != null) {
      var nodeLocation = 0  // 节点匹配位置的指针，本应该封装起来，但没必要
      // 查找直到节点或路径被搜索完毕
      while (nodeLocation < node.length && !context.end) {
        if (node[nodeLocation] != context.peek) {
          // 这里是一个适合插入的节点，进行插入操作
          insert(node, nodeLocation, context, value)
          return oldValue
        }
        context.add
        nodeLocation++
      }
      if (context.end) {
        // 如果路径被搜索完毕
        if (nodeLocation == node.length) {
          // 如果 node 与路径正好匹配
          oldValue = node.value
          node.value = value
          testNode(node)
        } else {
          // 不是正好匹配的，只能打断这个节点以插入数据
          branchNode(node, nodeLocation, context, value)
        }
        return oldValue
      }
      prev = node
      node = node.subNodes[context.peek]
    }
    // 没找到合适的节点，直接对查找的最后节点进行插入操作
    insert(prev, prev.length, context, value)
    return oldValue
  }

  /**
   * @return 节点是否有改变
   */
  private fun testNode(node: Node<T>): Boolean {
    if (node.value == null) {
      val nodeChanged = when (node.subNodes.size) {
        0 -> removeNode(node)
        1 -> mergeNode(node)
        else -> false
      }
      var parent = node.parent ?: return nodeChanged
      while (testNode(parent)) {
        parent = parent.parent ?: return nodeChanged
      }
    }
    return false
  }

  /**
   * 移除节点，不会进行后续的检查工作
   * @return 节点是否有改变
   */
  private fun removeNode(node: Node<T>): Boolean {
    val parent = node.parent
    return if (parent != null) {
      parent.remove(node[0])
      true
    } else {
      node.str = ""
      false
    }
  }

  /**
   * 合并节点与其子节点，不会进行后续的检查工作
   * @return 节点是否有改变
   */
  private fun mergeNode(node: Node<T>): Boolean {
    // 只有节点值为0且只有一个字节点的时候才会进行合并
    if (node.value != null || node.subNodes.size != 1) return false
    // 魔法操作（笑）
    val subNode = node.subNodes.first()!!
    node.str += subNode.str
    node.subNodes = subNode.subNodes
    node.subNodes.forEach { (_, u) -> u.parent = subNode }
    node.value = subNode.value
    return true
  }

  /**
   * 将一个节点打断成一对父子节点，并插入一个新子节点
   */
  private fun branchNode(node: Node<T>, nodeLocation: Int, context: Context, value: T?) {
    value ?: return
    Node(node, node.str.substring(nodeLocation, node.str.length), node.value)
    node.str = node.str.substring(0, nodeLocation)
    if (context.end) {
      node.value = value
    } else {
      node.value = null
      //node.subNodes[context.peek] = Node(context.remains, value, node)
      node.addSubNode(context.remains, value)
    }
  }

  /**
   * 针对一个节点插入数据
   */
  private fun insert(node: Node<T>, nodeLocation: Int, context: Context, value: T?) {
    value ?: return
    if (node.value == null) {
      if (node.subNodes.isEmpty()) {
        node.value = value
        node.str = context.remains
        return
      } else {
        if (node.str.isEmpty()) {
          //node.subNodes[context.peek] = Node(context.remains, value, node)
          node.addSubNode(context.remains, value)
          return
        } else if (node.str == context.remains) {
          node.value = value
          return
        }
      }
    }
    if (nodeLocation != node.length) {
      branchNode(node, nodeLocation, context, value)
    } else {
      //val subNode = Node(context.remains, value, node)
      //node.subNodes[subNode[0]] = subNode
      node.addSubNode(context.remains, value)
    }
  }

  operator fun get(route: String): T? {
    val context = Context(route)
    var node: Node<T>? = root
    while (node != null) {
      var nodeLocation = 0
      while (nodeLocation < node.length) {
        if (context.end || node[nodeLocation] != context.get) return null
        nodeLocation++
      }
      if (context.end) return node.value
      node = node.subNodes[context.peek]
    }
    return null
  }

  fun listGet(route: String): List<Pair<T?, Int>> {
    val context = Context(route)
    var node: Node<T>? = root
    val result = ArrayList<Pair<T?, Int>>()
    var recodedSize = 0
    while (node != null) {
      var nodeLocation = 0
      while (nodeLocation < node.length) {
        if (node[nodeLocation] != context.get) {
          result.add(node.value to recodedSize)
          result.add(null to route.length)
          return result
        }
        nodeLocation++
        recodedSize++
      }
      result.add(node.value to recodedSize)
      if (context.end) {
        return result
      }
      node = node.subNodes[context.peek]
    }
    return result
  }

  private fun toString(node: Node<T>, stringBuilder: StringBuilder, indentation: String) {
    if (indentation.isEmpty()) {
      stringBuilder.append("\"${node.str.replace("\"", "\"\"")}\": ${node.value}\n")
      node.subNodes.forEach { subNode ->
        toString(subNode.value, stringBuilder, " ")
      }
    } else {
      stringBuilder.append("$indentation|--\"${node.str.replace("\"", "\"\"")}\": ${node.value}\n")
      node.subNodes.forEach { subNode ->
        toString(subNode.value, stringBuilder, "$indentation|  ")
      }
    }
  }

  override fun toString(): String {
    val stringBuilder = StringBuilder()
    toString(root, stringBuilder, "")
    if (stringBuilder.isNotEmpty()) stringBuilder.deleteCharAt(stringBuilder.length - 1)
    return stringBuilder.toString()
  }

  /**
   * 基数树的节点，用来储存数据和子节点
   */
  private data class Node<T>(var str: String = "", var value: T? = null, var parent: Node<T>? = null, var subNodes: SimpMap<Char, Node<T>> = ArrayMap(0)) {
    constructor(parent: Node<T>, str: String = "", value: T? = null) : this(str, value, parent, parent.subNodes) {
      parent.subNodes = ArrayMap(1)
      parent.subNodes.setAndGet(this[0], this)
      subNodes.forEach { (_, u) -> u.parent = this }
    }

    val length get() = str.length
    operator fun get(index: Int) = str[index]
    fun addSubNode(key: String, value: T?) {
      if (subNodes is ArrayMap && subNodes.size > 16) {
        val oldNodes = subNodes
        subNodes = SimpHashMap()
        subNodes.putAll(oldNodes)
      }
      subNodes.setAndGet(key[0], Node(key, value, this))
    }

    fun remove(key: Char) {
      if (subNodes is SimpHashMap && subNodes.size < 8) {
        val oldNodes = subNodes
        subNodes = ArrayMap()
        subNodes.putAll(oldNodes)
      }
      subNodes.delete(key)
    }

    override fun toString(): String {
      val sb = StringBuilder("Node(str=\"$str\", value=\"$value\", parent=\"${parent?.str}\", subNodes=")
      subNodes.forEach { (t, _) ->
        sb.append(t)
      }
      return sb.toString()
    }
  }


  /**
   * 路径匹配时需要的环境
   */
  private data class Context(private val route: String, private var location: Int = 0) {
    val peek get() = route[location]
    val get get() = route[location++]
    val add get() = location++
    val end get() = location == route.length
    val remains get() = if (location <= 0) route else route.substring(location, route.length)
  }
}

//fun main() {
//	val tree = StringRadixTree<Int>()
//	//listOf("1" to 1, "2" to 2, "11" to 11, "12" to 12, "21" to 21, "22" to 22).forEach { (k, v) ->
//	//	tree[k] = v
//	//}
//	val list = listOf("romane", "romanus", "romulus", "rubens", "ruber", "rubicon", "rubicundus", "海星", "大屁股裂了",
//		"尼玛死", "是乌拉啊我死了", "是薄荷啊我死了", "是村正，呕 ", "是村正，呕 ")
//	list.forEachIndexed { index, s ->
//		tree[s] = index + 1
//		println(tree)
//		println()
//	}
//
//  println(listOf<String>().last())
//  println(tree.listGet("roman"))
//
//	//list.forEach {
//	//	tree[it] = null
//	//	println(tree)
//	//	println()
//	//}
//  //
//	//list.forEachIndexed { index, s ->
//	//	tree[s] = index + 1
//	//	println(tree)
//	//	println()
//	//}
//}
