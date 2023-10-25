import cn.tursom.core.util.usingNanoTime
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.math.abs
import kotlin.math.roundToInt

interface Type<T>

fun <T> f1(t: Type<T>) {
}

fun f2(t: Type<Int>) {
}

class TestType(
  val type: Type<Int>,
)

fun getErasure(method: Method) {
  method.parameters.forEach { parameter ->
    val type = parameter.parameterizedType
    if (type !is ParameterizedType) return@forEach
    println(type.actualTypeArguments.asList())
  }
}

fun getErasure(field: Field) {
  val type = field.genericType
  if (type !is ParameterizedType) return
  println(type.actualTypeArguments.asList())
}

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
  private val flow = FloatArray(2)
  private val flowCache = FloatArray(2)
  //private val deltaRecord = FloatArray(2)
  //private var upCount = IntArray(2)

  operator fun get(channel: FlowChannel) = flow[channel.index]
  operator fun set(channel: FlowChannel, value: Float) {
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
    flowCache.fill(0f)
    sourceList.forEach { (source, sourceFlowId, targetFlowId, fraction) ->
      flowCache[targetFlowId] += source.flow[sourceFlowId] / (1 shl fraction)
      val d = abs(flowCache[targetFlowId] - flow[targetFlowId])
      //println("$d:${flow[targetFlowId]}")
      //if (flow[targetFlowId] != 0f && d >= flow[targetFlowId]) {
      //  ++upCount[targetFlowId]
      //} else {
      //  upCount[targetFlowId] = 0
      //}
    }
  }

  fun finishCalc() {
    repeat(2) { index ->
      //deltaRecord[index] = flowCache[index] - flow[index]
      flow[index] = flowCache[index]
    }
  }

  fun finishCalc(channel: FlowChannel) {
    flow[channel.index] = flowCache[channel.index]
  }

  fun changed(): Boolean {
    repeat(2) { index ->
      //if (upCount[index] > 10 || abs(flow[index] - flowCache[index]) > 1e-6) {
      if (flow[index] > 100 || abs(flow[index] - flowCache[index]) > 1e-6) {
        return true
      }
    }
    return false
  }

  fun clear() {
    repeat(2) {
      flow[it] = 0f
      flowCache[it] = 0f
    }
  }

  override fun toString(): String {
    return "Edge(flow=${flow.contentToString()}, flowCache=${flowCache.contentToString()})"
  }
}

class Graphic(
  //var precision: Int = 8,
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
    edgeList[input][inputFlowChannel] = 1f
    edgeList.forEach { edge ->
      edge.calc()
    }
  }

  fun changed(): Boolean {
    edgeList[input].finishCalc(inputFlowChannel)
    edgeList.forEach { edge ->
      if (edge.changed()) {
        //println("$edge changed")
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

  fun result(): List<Pair<Float, Float>> {
    return edgeList.map { it[FlowChannel.A] to it[FlowChannel.B] }
  }

  override fun iterator(): Iterator<Edge> = edgeList.iterator()

  @Builder
  class GraphicBuilder {
    //var precision: Int = 8
    val edgeMap = ArrayList<EdgeBuilder>()
    inline fun edge(builder: EdgeBuilder.() -> Unit) {
      edgeMap.add(EdgeBuilder(edgeMap.size).also(builder))
    }

    fun build(): Graphic {
      val graphic = Graphic()
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

fun Double.toFraction(): Pair<Int, Int> {
  var minI = 0
  var min = 1.0
  var minA = 0
  var i = 1
  for (n in 2..100) {
    i++
    val a = (this * n).roundToInt()
    val d = abs(1 - n * this / a)
    if (d < min) {
      min = d
      minA = a
      minI = i
    }
  }
  return minA to minI
}

class FlowExample {
  fun <R> execute(func: Function<R>) {
    val type = func.javaClass.genericInterfaces[0] as ParameterizedType
    println(type)
    println(type.actualTypeArguments[0])
  }

  fun <R> executeMatryoshka(func: Function<R>) = execute {
    func
  }

  @Test
  fun testGetErasure() {
    executeMatryoshka {
    }
    //getErasure(TestType::type.javaField!!)
  }

  @Test
  fun testToFraction() {
    println((17684.0 / 65534).toFraction())
  }

  @Test
  fun test() {
    val graphic = getDeadTestGraphic()
    //graphic.precision = 13

    var changed = true
    var step = 0
    while (changed && step < 1000) {
      step++
      graphic.calc()
      changed = graphic.changed()
      println("step $step changed $changed")
      graphic.finishCalc()
      println(graphic.result())
    }
    //val sum = graphic.getEdge(0)[FlowChannel.B] + (7..11).sumOf {
    //  graphic.getEdge(it)[FlowChannel.A].toDouble()
    //}
    //println(sum.toInt())
    //println((graphic.getEdge(0)[FlowChannel.B] / sum).toFraction())
    //(7..11).forEach {
    //  println((graphic.getEdge(it)[FlowChannel.A] / sum).toFraction())
    //}
  }

  /**
   * 性能测试
   */
  @Test
  fun testPerformance() {
    val graphic = getTestGraphic()

    // 热车
    //graphic.precision = 16
    repeat(1000) {
      graphic.clear()
      var changed = true
      while (changed) {
        graphic.calc()
        changed = graphic.changed()
        graphic.finishCalc()
      }
    }

    // 测试
    repeat(5) {
      intArrayOf(8, 12, 16, 20, 24, 28).forEach { precision ->
        graphic.clear()
        //graphic.precision = precision

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

  fun getDeadTestGraphic() = Graphic {
    //0
    edge {
    }
    //1
    edge {
      flow(3, 1, 0)
      flow(5, 1, 1)
    }
    //2
    edge {
      flow(4, 1, 0)
      flow(5, 0, 1)
      flow(0, 0, 0, 1)
    }
    //3
    edge {
      flow(1, 1, 0)
      flow(6, 0, 1)
      flow(0, 0, 0, 1)
    }
    //4
    edge {
      flow(2, 1, 0)
      flow(6, 1, 1)
    }
    //5
    edge {
      flow(1, 0, 0)
      flow(2, 0, 1)
    }
    //6
    edge {
      flow(4, 0, 0)
      flow(3, 0, 1)
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
      flow(2, 0, 0, 1)
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
      flow(10, 1, 1, 1)
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

