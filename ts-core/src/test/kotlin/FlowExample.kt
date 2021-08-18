import cn.tursom.core.usingNanoTime
import org.junit.Test

data class Flow(
  val source: Edge,
  val sourceFlowChannel: Int,
  val targetFlowChannel: Int,
  val fraction: Int = 0,
)

enum class FlowChannel(val index: Int) {
  A(0), B(1)
}

class Edge {
  companion object {
    val invMap = intArrayOf(1, 0)
  }

  private val sourceList: ArrayList<Flow> = ArrayList()
  private val flow: IntArray = IntArray(2)
  private val flowCache: IntArray = IntArray(2)

  operator fun get(channel: FlowChannel) = flow[channel.index]
  operator fun set(channel: FlowChannel, value: Int) {
    flow[channel.index] = value
  }

  fun addFlow(flow: Flow) {
    sourceList.add(flow)
  }

  fun addFlow(vararg flows: Flow) {
    flows.forEach { flow ->
      sourceList.add(flow)
    }
  }

  fun calc() {
    flowCache.fill(0)
    sourceList.forEach { (source, sourceFlowId, targetFlowId, fraction) ->
      flowCache[targetFlowId] += source.flow[sourceFlowId] shr fraction
    }
  }

  fun finishCalc() {
    repeat(2) { index ->
      flow[index] = flowCache[index]
    }
  }

  fun finishCalc(channel: FlowChannel) {
    flow[channel.index] = flowCache[channel.index]
  }

  fun changed(): Boolean {
    repeat(2) { index ->
      if (flow[index] != flowCache[index]) {
        return true
      }
    }
    return false
  }

  fun clear() {
    repeat(2) {
      flow[it] = 0
      flowCache[it] = 0
    }
  }

  override fun toString(): String {
    return "Edge(flow=${flow.contentToString()}, flowCache=${flowCache.contentToString()})"
  }
}

class Graphic(
  var precision: Int = 8,
) : Iterable<Edge> {
  companion object {
    inline operator fun invoke(
      builder: GraphicBuilder.() -> Unit,
    ) = GraphicBuilder().also(builder).build()
  }

  @DslMarker
  annotation class Builder

  private val edgeList = ArrayList<Edge>()
  var input: Int = 0
  var inputFlowChannel: FlowChannel = FlowChannel.A

  fun addEdge(edge: Edge): Int {
    edgeList.add(edge)
    return edgeList.size - 1
  }

  fun getEdge(id: Int): Edge = edgeList[id]

  fun calc() {
    edgeList[input][inputFlowChannel] = 1 shl precision
    edgeList.forEach { edge ->
      edge.calc()
    }
  }

  fun changed(): Boolean {
    edgeList[input].finishCalc(inputFlowChannel)
    edgeList.forEach { edge ->
      if (edge.changed()) {
        return true
      }
    }
    return false
  }

  fun finishCalc() {
    edgeList.forEach { edge ->
      edge.finishCalc()
    }
  }

  fun clear() {
    edgeList.forEach { edge ->
      edge.clear()
    }
  }

  fun result(): List<Pair<Int, Int>> {
    return edgeList.map { it[FlowChannel.A] to it[FlowChannel.B] }
  }

  override fun iterator(): Iterator<Edge> = edgeList.iterator()

  @Builder
  class GraphicBuilder {
    var precision: Int = 8
    val edgeMap = ArrayList<EdgeBuilder>()
    inline fun edge(builder: EdgeBuilder.() -> Unit) {
      edgeMap.add(EdgeBuilder(edgeMap.size).also(builder))
    }

    fun build(): Graphic {
      val graphic = Graphic(precision)
      repeat(edgeMap.size) {
        graphic.addEdge(Edge())
      }
      edgeMap.forEach { builder ->
        builder.init(graphic)
      }
      return graphic
    }
  }

  @Builder
  class EdgeBuilder(private val id: Int) {
    private val flowList = ArrayList<FlowBuilder>()
    fun init(graphic: Graphic) {
      val edge = graphic.getEdge(id)
      flowList.forEach { (source, sourceFlowChannel, targetFlowChannel, fraction) ->
        edge.addFlow(Flow(
          graphic.getEdge(source),
          sourceFlowChannel, targetFlowChannel, fraction
        ))
      }
    }

    fun flow(
      source: Int,
      sourceFlowChannel: Int,
      targetFlowChannel: Int,
      fraction: Int = 0,
    ) {
      flowList.add(FlowBuilder(source, sourceFlowChannel, targetFlowChannel, fraction))
    }
  }

  @Builder
  data class FlowBuilder(
    val source: Int,
    val sourceFlowChannel: Int,
    val targetFlowChannel: Int,
    val fraction: Int = 0,
  )
}

class FlowExample {
  init {
    val graphic = getTestGraphic()

    // 热车
    graphic.precision = 30
    repeat(10000) {
      graphic.clear()
      var changed = true
      while (changed) {
        graphic.calc()
        changed = graphic.changed()
        graphic.finishCalc()
      }
    }
  }

  @Test
  fun test() {
    val graphic = getTestGraphic()
    graphic.precision = 8

    var changed = true
    var step = 0
    while (changed) {
      step++
      graphic.calc()
      changed = graphic.changed()
      println("step $step changed $changed")
      graphic.finishCalc()
      println(graphic.result())
    }
  }

  /**
   * 性能测试
   */
  @Test
  fun testPerformance() {
    val graphic = getTestGraphic()

    // 测试
    repeat(10000) {
      intArrayOf(8, 12, 16, 20, 24, 28).forEach { precision ->
        graphic.clear()
        graphic.precision = precision

        var step = 0
        var changed = true
        val usingTime = usingNanoTime {
          while (changed) {
            step++
            graphic.calc()
            changed = graphic.changed()
            graphic.finishCalc()
          }
        }

        println("precision $precision step $step using $usingTime us")
      }
    }
  }

  fun getTestGraphic() = Graphic {
    // 0
    edge {
      flow(1, 1, 1, 1)
      flow(6, 1, 1, 1)
    }
    // 1
    edge {
      flow(0, 0, 0, 1)
      flow(6, 1, 0, 1)
      flow(7, 1, 1, 1)
      flow(2, 1, 1, 1)
    }
    // 2
    edge {
      flow(1, 0, 0, 1)
      flow(7, 1, 0, 1)
      flow(8, 1, 1, 1)
      flow(3, 1, 1, 1)
    }
    // 3
    edge {
      flow(1, 0, 0, 1)
      flow(8, 1, 0, 1)
      flow(9, 1, 1, 1)
      flow(4, 0, 1, 1)
    }
    // 4
    edge {
      flow(5, 0, 0, 1)
      flow(10, 1, 0, 1)
      flow(3, 0, 1, 1)
      flow(9, 1, 1, 1)
    }
    // 5
    edge {
      flow(6, 0, 0, 1)
      flow(11, 1, 0, 1)
      flow(4, 1, 1, 1)
      flow(0, 1, 1, 1)
    }
    // 6
    edge {
      flow(0, 0, 0, 1)
      flow(1, 1, 0, 1)
      flow(5, 1, 1, 1)
      flow(11, 1, 1, 1)
    }
    // 7
    edge {
      flow(1, 0, 0, 1)
      flow(2, 1, 0, 1)
    }
    // 8
    edge {
      flow(2, 0, 0, 1)
      flow(3, 1, 0, 1)
    }
    // 9
    edge {
      flow(3, 0, 0, 1)
      flow(4, 0, 0, 1)
    }
    // 10
    edge {
      flow(5, 0, 0, 1)
      flow(4, 1, 0, 1)
    }
    // 11
    edge {
      flow(6, 0, 0, 1)
      flow(5, 1, 0, 1)
    }
  }
}
