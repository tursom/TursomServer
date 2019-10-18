import cn.tursom.database.annotation.*
import cn.tursom.database.clauses.ClauseMaker
import cn.tursom.database.select
import cn.tursom.database.sqlite.SQLiteHelper
import cn.tursom.database.SqlUtils.tableName
import org.jetbrains.annotations.NotNull

/**
 * 用来访问数据库的数据类
 */
// 自定义表名
@TableName("demo")
// 如果未自定义表名，则以类名作为表名
data class Demo(
    // 定义一个非空，自增的整数列 `id` 作为主键
    @NotNull @AutoIncrement @PrimaryKey val id: Int? = null,
    // 定义一个非空，唯一的字符串列 `name`
    @NotNull @Unique @FieldType("varchar(32)") val name: String,
    // 定义一个非空小数列 `money`，其默认值为0
    @NotNull @Default("0") @FieldName("money") val money: Double = 0.0
)

fun main() {
  // 获取数据库访问协助对象
  val helper = SQLiteHelper("demo.db")

  // 插入数据
  helper.insert(Demo(name = "tursom"))

  // 更新数据
  helper.update(Demo(name = "tursom", money = 100.0), where = ClauseMaker.make {
    !Demo::name equal "tursom"
  })

  // 获取数据
  val data = helper.select(Demo::class.java, where = ClauseMaker {
    (!Demo::id greaterThan !0) and (!Demo::id lessThan !10)
  })

  // 删除数据
  helper.delete(Demo::class.java.tableName, where = ClauseMaker.make { !Demo::name equal "tursom" })
}