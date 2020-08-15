package cn.tursom.database

import cn.tursom.core.cast
import cn.tursom.database.SqlUtils.tableField
import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.annotations.TableField
import cn.tursom.utils.clone.Property
import cn.tursom.utils.clone.inject
import cn.tursom.utils.clone.instance
import me.liuwj.ktorm.dsl.QueryRowSet
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
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
  val unsafe: Boolean = true
) : BaseTable<T>(tableName, alias, entityClass) {
    private val fieldMap: Map<String, KProperty<*>>
    private val fieldColumns: MutableMap<KProperty<*>, Column<*>> = HashMap()
    private val fieldNameColumnMap: MutableMap<String, Column<*>> = HashMap()

    init {
        fieldMap = entityClass.memberProperties.associateBy { it.tableField }
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
        val instance = instance(unsafe, entityClass!!.java)!!
        columns.forEach {
            val field = fieldMap[it.name] ?: return@forEach
            row[it]?.inject(instance, field.cast<Property<Any>>())
        }
        return instance
    }

    operator fun <R : Any> get(property: KProperty1<T, R?>): Column<R> = fieldColumns[property].cast()
    //operator fun <R : Any> get(property: KProperty1<T, R?>): Column<R> = this[property.simpTableField].cast()

    fun <V : Any> field(): FieldProxy<T, V> = fieldProxyInstance.cast()
    fun <V : Any> field(property: KProperty0<*>): Column<V> = fieldNameColumnMap[property.name].cast()

    companion object {
        private val fieldProxyInstance = FieldProxy<Any, Any>()
        private val autoTableMap = ConcurrentHashMap<Class<*>, AutoTable<*>>()
        operator fun <T : Any> get(clazz: KClass<T>): AutoTable<T> = get(clazz.java)

        operator fun <T : Any> get(clazz: Class<T>): AutoTable<T> {
            var autoTable = autoTableMap[clazz]
            if (autoTable == null) {
                synchronized(autoTableMap) {
                    autoTable = AutoTable(clazz.kotlin)
                    autoTableMap[clazz] = autoTable.cast()
                }
            }
            return autoTable.cast()
        }

        class FieldProxy<T : Any, V : Any> {
            operator fun getValue(
              autoTable: AutoTable<T>,
              property: KProperty<*>
            ): Column<V> = autoTable.fieldNameColumnMap[property.name].cast()
        }
    }
}
