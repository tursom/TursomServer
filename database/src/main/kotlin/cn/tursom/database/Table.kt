package cn.tursom.database

import cn.tursom.core.cast
import cn.tursom.utils.clone.Property
import cn.tursom.utils.clone.inject
import cn.tursom.utils.clone.instance
import me.liuwj.ktorm.dsl.QueryRowSet
import me.liuwj.ktorm.schema.BaseTable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

open class Table<T : Any>(
  entityClass: KClass<T>,
  tableName: String = SqlUtils { entityClass.tableName },
  alias: String? = null,
  val unsafe: Boolean = true
) : BaseTable<T>(tableName, alias, entityClass) {
  private val fieldMap: Map<String, KProperty<*>>

  init {
    fieldMap = entityClass.memberProperties.associateBy { SqlUtils { it.tableField } }
    entityClass.memberProperties.forEach {
      TypeAdapterFactory.register(this, it)
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
