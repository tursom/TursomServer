package cn.tursom.http

import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.core.coroutine.MainDispatcher
import cn.tursom.core.util.Utils.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CoroutineLocalTest {
  @GET("/")
  suspend fun test(): Document

  @GET("/status")
  suspend fun status(): List<RoomStatus>

  @GET("status/{db}")
  suspend fun status(@Path("db") db: String): RoomStatus

  @GET("room/v1/Room/get_info")
  suspend fun getRoomInfo(@Query("id") roomId: Int, @Query("from") from: String = "room"): String
}

suspend fun main() {
  MainDispatcher.init()
  val retrofit = Retrofit.Builder()
    //.baseUrl("http://tursom.cn:15015")
    //.baseUrl("https://www.baidu.com")
    .baseUrl("https://api.live.bilibili.com")
    .addCallAdapterFactory(BlockingCallAdapterFactory)
    .addConverterFactory(StringConverterFactory)
    .addConverterFactory(HtmlConverterFactory)
    .addConverterFactory(XmlConverterFactory)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
  GlobalScope.launch(Dispatchers.Main) {
    launch {
      println(Thread.currentThread().name)
    }
    val coroutineLocalTest: CoroutineLocalTest = retrofit.create()
    //println(coroutineLocalTest.status())
    println(coroutineLocalTest.getRoomInfo(801580))
  }.join()
}

data class RoomStatus(
  val connected: Boolean,
  val db: String,
  val liveUser: LiveUser,
  val living: Boolean,
  val recvCount: Int,
  val roomId: Int,
  val roomInfo: RoomInfo,
  val totalCount: Int,
)

data class LiveUser(
  val info: Info,
  val level: Level,
  val san: Int,
)

data class RoomInfo(
  val allow_change_area_time: Int,
  val allow_upload_cover_time: Int,
  val area_id: Int,
  val area_name: String,
  val area_pendants: String,
  val attention: Int,
  val background: String,
  val battle_id: Int,
  val description: String,
  val hot_words: List<String>,
  val hot_words_status: Int,
  val is_anchor: Int,
  val is_portrait: Boolean,
  val is_strict_room: Boolean,
  val keyframe: String,
  val live_status: Int,
  val live_time: String,
  val new_pendants: NewPendants,
  val old_area_id: Int,
  val online: Int,
  val parent_area_id: Int,
  val parent_area_name: String,
  val pendants: String,
  val pk_id: Int,
  val pk_status: Int,
  val room_id: Int,
  val room_silent_level: Int,
  val room_silent_second: Int,
  val room_silent_type: String,
  val short_id: Int,
  val studio_info: StudioInfo,
  val tags: String,
  val title: String,
  val uid: Int,
  val up_session: String,
  val user_cover: String,
  val verify: String,
)

data class Info(
  val face: String,
  val gender: Int,
  val identification: Int,
  val mobile_verify: Int,
  val official_verify: OfficialVerify,
  val platform_user_level: Int,
  val rank: String,
  val uid: Int,
  val uname: String,
  val vip_type: Int,
)

data class Level(
  val anchor_score: Int,
  val color: Int,
  val cost: Int,
  val master_level: MasterLevel,
  val rcost: Long,
  val svip: Int,
  val svip_time: String,
  val uid: Int,
  val update_time: String,
  val user_level: Int,
  val user_score: String,
  val vip: Int,
  val vip_time: String,
)

data class OfficialVerify(
  val desc: String,
  val role: Int,
  val type: Int,
)

data class MasterLevel(
  val anchor_score: Int,
  val color: Int,
  val current: List<Int>,
  val level: Int,
  val master_level_color: Int,
  val next: List<Int>,
  val sort: String,
  val upgrade_score: Int,
)

data class NewPendants(
  val badge: Badge,
  val frame: Frame,
  val mobile_frame: MobileFrame,
)

data class StudioInfo(
  val master_list: List<Any>,
  val status: Int,
)

data class Badge(
  val desc: String,
  val name: String,
  val position: Double,
  val value: String,
)

data class Frame(
  val area: Int,
  val area_old: Int,
  val bg_color: String,
  val bg_pic: String,
  val desc: String,
  val name: String,
  val position: Int,
  val use_old_area: Boolean,
  val value: String,
)

data class MobileFrame(
  val area: Int,
  val area_old: Int,
  val bg_color: String,
  val bg_pic: String,
  val desc: String,
  val name: String,
  val position: Int,
  val use_old_area: Boolean,
  val value: String,
)