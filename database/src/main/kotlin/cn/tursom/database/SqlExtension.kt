@file:Suppress("unused")

package cn.tursom.database

import com.google.gson.Gson
import me.liuwj.ktorm.schema.*
import java.math.BigDecimal
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.time.*
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaType

fun <C : Any, E : Any> BaseTable<E>.json(
  field: KProperty1<E, C?>,
  type: Int = Types.VARCHAR,
  gson: Gson = cn.tursom.utils.gson
): BaseTable<E>.ColumnRegistration<C> {
  val sqlType = JsonType<C>(field.returnType.javaType, type, gson)
  return this.registerColumn(SqlUtils { field.name.sqlName }, sqlType)
}

fun <E : Any> BaseTable<E>.boolean(
  field: KProperty1<E, Boolean?>
) = boolean(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.int(
  field: KProperty1<E, Int?>
) = int(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.long(
  field: KProperty1<E, Long?>
) = long(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.float(
  field: KProperty1<E, Float?>
) = float(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.double(
  field: KProperty1<E, Double?>
) = double(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.decimal(
  field: KProperty1<E, BigDecimal?>
) = decimal(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.varchar(
  field: KProperty1<E, String?>
) = varchar(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.text(
  field: KProperty1<E, String?>
) = text(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.blob(
  field: KProperty1<E, ByteArray?>
) = blob(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.bytes(
  field: KProperty1<E, ByteArray?>
) = bytes(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.jdbcTimestamp(
  field: KProperty1<E, Timestamp?>
) = jdbcTimestamp(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.jdbcDate(
  field: KProperty1<E, Date?>
) = jdbcDate(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.jdbcTime(
  field: KProperty1<E, Time?>
) = jdbcTime(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.timestamp(
  field: KProperty1<E, Instant?>
) = timestamp(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.datetime(
  field: KProperty1<E, LocalDateTime?>
) = datetime(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.date(
  field: KProperty1<E, LocalDate?>
) = date(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.time(
  field: KProperty1<E, LocalTime?>
) = time(SqlUtils { field.name.sqlName })

fun <E : Any> BaseTable<E>.monthDay(
  field: KProperty1<E, MonthDay?>
) = monthDay(SqlUtils { field.name.sqlName })


fun <E : Any> BaseTable<E>.yearMonth(
  field: KProperty1<E, YearMonth?>
) = yearMonth(SqlUtils { field.name.sqlName })


fun <E : Any> BaseTable<E>.year(
  field: KProperty1<E, Year?>
) = year(SqlUtils { field.name.sqlName })

fun <E : Any, C : Enum<C>> BaseTable<E>.enum(
  field: KProperty1<E, C?>,
  typeRef: TypeReference<C>
) = enum(SqlUtils { field.name.sqlName }, typeRef)

fun <E : Any> BaseTable<E>.uuid(
  field: KProperty1<E, UUID?>
) = uuid(SqlUtils { field.name.sqlName })

