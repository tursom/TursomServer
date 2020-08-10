package cn.tursom.database

import cn.tursom.core.cast
import cn.tursom.database.annotations.Json
import cn.tursom.utils.clone.Property
import cn.tursom.utils.clone.inject
import cn.tursom.utils.clone.instance
import me.liuwj.ktorm.dsl.QueryRowSet
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.EnumSqlType
import java.math.BigDecimal
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

open class Table<T : Any>(
  entityClass: KClass<T>,
  tableName: String = SqlUtils { entityClass.tableName },
  alias: String? = null,
  val unsafe: Boolean = true
) : BaseTable<T>(tableName, alias, entityClass) {
  private enum class EnumType

  private val fieldMap: Map<String, KProperty<*>>

  init {
    fieldMap = entityClass.memberProperties.associateBy { SqlUtils { it.tableField } }
    entityClass.memberProperties.forEach {
      when (val kClass = it.returnType.jvmErasure) {
        Boolean::class -> boolean(it.cast())
        //Byte::class->registerColumn(SqlUtils { it.tableField }, ByteSqlType)
        //Short::class->registerColumn(SqlUtils { it.tableField }, ShortSqlType)
        Int::class -> int(it.cast())
        Long::class -> long(it.cast())
        Float::class -> float(it.cast())
        Double::class -> double(it.cast())
        BigDecimal::class -> decimal(it.cast())
        String::class -> varchar(it.cast())
        ByteArray::class -> bytes(it.cast())
        Timestamp::class -> jdbcTimestamp(it.cast())
        Date::class -> jdbcDate(it.cast())
        Time::class -> jdbcTime(it.cast())
        Instant::class -> timestamp(it.cast())
        LocalDateTime::class -> datetime(it.cast())
        LocalDate::class -> date(it.cast())
        LocalTime::class -> time(it.cast())
        MonthDay::class -> monthDay(it.cast())
        YearMonth::class -> yearMonth(it.cast())
        Year::class -> year(it.cast())
        UUID::class -> uuid(it.cast())
        else -> {
          if (kClass.isSubclassOf(Enum::class)) {
            registerColumn(SqlUtils { it.tableField }, EnumSqlType<EnumType>(kClass.java.cast()))
          } else {
            if (it.findAnnotation<Json>() != null) {
              json(it.cast())
            }
          }
        }
      }
    }
  }

  override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): T {
    val instance = instance(unsafe, entityClass!!.java)!!
    columns.forEach {
      val field = fieldMap[it.name] ?: return@forEach
      row[it]?.inject(instance, field.cast<Property<Any>>())
    }
    return instance
  }

  operator fun <R> get(property: KProperty1<T, R>) = this[SqlUtils { property.tableField }]
}

