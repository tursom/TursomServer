@file:Suppress("unused")

package cn.tursom.database

import cn.tursom.core.Utils
import cn.tursom.core.uncheckedCast
import com.google.gson.Gson
import me.liuwj.ktorm.dsl.Query
import me.liuwj.ktorm.dsl.QueryRowSet
import me.liuwj.ktorm.schema.*
import java.math.BigDecimal
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.time.*
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

val KProperty<*>.table
  get() = AutoTable[javaField!!.declaringClass].uncheckedCast<AutoTable<Any>>()
val <T : Any> KProperty<T>.sql
  get() = table[this.uncheckedCast<KProperty1<Any, T>>()]

inline val <reified T : Any> KProperty1<T, *>.table
  get() = AutoTable[T::class.java]

inline val <reified T : Any, R : Any> KProperty1<T, R?>.sql
  get() = table[this]

fun <T> Query.getOne(transform: (rowSet: QueryRowSet) -> T): T? = if (rowSet.next()) {
  transform(rowSet)
} else {
  null
}

inline fun <reified T : Any> Query.getOne(): T? = if (rowSet.next()) {
  AutoTable[T::class].createEntity(rowSet)
} else {
  null
}

fun <C : Any, E : Any> BaseTable<E>.json(
  field: KProperty1<E, C?>,
  type: Int = Types.VARCHAR,
  gson: Gson = Utils.gson,
): Column<C> {
  val sqlType = JsonType<C>(field.returnType.javaType, type, gson)
  return this.registerColumn(field.simpTableField, sqlType)
}

fun <E : Any> BaseTable<E>.boolean(
  field: KProperty1<E, Boolean?>,
) = boolean(field.simpTableField)

fun <E : Any> BaseTable<E>.int(
  field: KProperty1<E, Int?>,
) = int(field.simpTableField)

fun <E : Any> BaseTable<E>.long(
  field: KProperty1<E, Long?>,
) = long(field.simpTableField)

fun <E : Any> BaseTable<E>.float(
  field: KProperty1<E, Float?>,
) = float(field.simpTableField)

fun <E : Any> BaseTable<E>.double(
  field: KProperty1<E, Double?>,
) = double(field.simpTableField)

fun <E : Any> BaseTable<E>.decimal(
  field: KProperty1<E, BigDecimal?>,
) = decimal(field.simpTableField)

fun <E : Any> BaseTable<E>.varchar(
  field: KProperty1<E, String?>,
) = varchar(field.simpTableField)

fun <E : Any> BaseTable<E>.text(
  field: KProperty1<E, String?>,
) = text(field.simpTableField)

fun <E : Any> BaseTable<E>.blob(
  field: KProperty1<E, ByteArray?>,
) = blob(field.simpTableField)

fun <E : Any> BaseTable<E>.bytes(
  field: KProperty1<E, ByteArray?>,
) = bytes(field.simpTableField)

fun <E : Any> BaseTable<E>.jdbcTimestamp(
  field: KProperty1<E, Timestamp?>,
) = jdbcTimestamp(field.simpTableField)

fun <E : Any> BaseTable<E>.jdbcDate(
  field: KProperty1<E, Date?>,
) = jdbcDate(field.simpTableField)

fun <E : Any> BaseTable<E>.jdbcTime(
  field: KProperty1<E, Time?>,
) = jdbcTime(field.simpTableField)

fun <E : Any> BaseTable<E>.timestamp(
  field: KProperty1<E, Instant?>,
) = timestamp(field.simpTableField)

fun <E : Any> BaseTable<E>.datetime(
  field: KProperty1<E, LocalDateTime?>,
) = datetime(field.simpTableField)

fun <E : Any> BaseTable<E>.date(
  field: KProperty1<E, LocalDate?>,
) = date(field.simpTableField)

fun <E : Any> BaseTable<E>.time(
  field: KProperty1<E, LocalTime?>,
) = time(field.simpTableField)

fun <E : Any> BaseTable<E>.monthDay(
  field: KProperty1<E, MonthDay?>,
) = monthDay(field.simpTableField)


fun <E : Any> BaseTable<E>.yearMonth(
  field: KProperty1<E, YearMonth?>,
) = yearMonth(field.simpTableField)


fun <E : Any> BaseTable<E>.year(
  field: KProperty1<E, Year?>,
) = year(field.simpTableField)

fun <E : Any> BaseTable<E>.uuid(
  field: KProperty1<E, UUID?>,
) = uuid(field.simpTableField)

fun <E : Any, C : Enum<C>> BaseTable<E>.enum(
  field: KProperty1<E, C?>,
  typeRef: TypeReference<C>,
) = enum(field.simpTableField, typeRef)

fun <E : Any, C : Enum<C>> BaseTable<E>.enum(
  field: KProperty1<E, C?>,
  type: Class<C>,
) = registerColumn(field.simpTableField, EnumSqlType(type))

