package cn.tursom.database

import cn.tursom.core.clone.Property
import cn.tursom.core.clone.inject
import cn.tursom.core.clone.instance
import cn.tursom.core.uncheckedCast
import com.baomidou.mybatisplus.annotation.TableField
import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

open class AutoTable<T : Any>(
  entityClass: KClass<T>,
  tableName: String = entityClass.tableName,
  alias: String? = null,
  catalog: String? = null,
  schema: String? = null,
  val unsafe: Boolean = true,
) : BaseTable<T>(tableName, alias, catalog, schema, entityClass) {
  private val fieldMap: Map<String, KProperty<*>>
  private val fieldColumns: MutableMap<KProperty<*>, Column<*>> = HashMap()
  private val fieldNameColumnMap: MutableMap<String, Column<*>> = HashMap()

  init {
    fieldMap = entityClass.memberProperties.associateBy { it.simpTableField }
    entityClass.memberProperties.forEach {
      val field = it.javaField ?: return@forEach
      val tableField: TableField? = field.getAnnotation(TableField::class.java)
      if (tableField?.exist == false) return@forEach
      //TypeAdapterFactory.register(this, it)
      val column = TypeAdapterFactory.register(this, it) ?: return@forEach
      fieldColumns[it] = column
      fieldNameColumnMap[it.name] = column
    }
  }

  override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): T {
    val instance = instance(unsafe, entityClass!!.java)
    columns.forEach {
      val field = fieldMap[it.name] ?: return@forEach
      row[it]?.inject(instance, field.uncheckedCast<Property<Any>>())
    }
    return instance
  }

  operator fun <R : Any> get(property: KProperty1<T, R?>): Column<R> = fieldColumns[property].uncheckedCast()
  //operator fun <R : Any> get(property: KProperty1<T, R?>): Column<R> = this[property.simpTableField].cast()

  fun <V : Any> field(): FieldProxy<T, V> = fieldProxyInstance.uncheckedCast()
  fun <V : Any> field(property: KProperty0<*>): Column<V> = fieldNameColumnMap[property.name].uncheckedCast()

  companion object {
    private val fieldProxyInstance = FieldProxy<Any, Any>()
    private val autoTableMap = ConcurrentHashMap<Class<*>, AutoTable<*>>()
    operator fun <T : Any> get(clazz: KClass<T>): AutoTable<T> = get(clazz.java)

    operator fun <T : Any> get(clazz: Class<T>): AutoTable<T> {
      var autoTable = autoTableMap[clazz]
      if (autoTable == null) {
        synchronized(autoTableMap) {
          autoTable = AutoTable(clazz.kotlin)
          autoTableMap[clazz] = autoTable.uncheckedCast()
        }
      }
      return autoTable.uncheckedCast()
    }

    class FieldProxy<T : Any, V : Any> {
      operator fun getValue(
        autoTable: AutoTable<T>,
        property: KProperty<*>,
      ): Column<V> = autoTable.fieldNameColumnMap[property.name].uncheckedCast()
    }
  }
}
