package cn.tursom.database.ktorm

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring

inline fun <reified T : Any> Database.update(
  noinline block: UpdateStatementBuilder.(AutoTable<T>) -> Unit,
): Int {
  return update(AutoTable[T::class], block)
}

inline fun <reified T : Any> Database.batchUpdate(
  noinline block: BatchUpdateStatementBuilder<AutoTable<T>>.() -> Unit,
): IntArray {
  return batchUpdate(AutoTable[T::class], block)
}

inline fun <reified T : Any> Database.insert(
  noinline block: AssignmentsBuilder.(AutoTable<T>) -> Unit,
): Int {
  return insert(AutoTable[T::class], block)
}

inline fun <reified T : Any> Database.insert(
  value: T,
): Int {
  val table = AutoTable[T::class]
  return insert(table) {
    set(table, value)
  }
}

inline fun <reified T : Any> Database.insertAndGenerateKey(
  noinline block: AssignmentsBuilder.(AutoTable<T>) -> Unit,
): Any {
  return insertAndGenerateKey(AutoTable[T::class], block)
}

inline fun <reified T : Any> Database.batchInsert(
  noinline block: BatchInsertStatementBuilder<AutoTable<T>>.() -> Unit,
): IntArray {
  return batchInsert(AutoTable[T::class], block)
}

inline fun <reified T : Any> Database.batchInsert(
  values: Iterable<T>,
): IntArray = batchInsert<T> {
  val table = AutoTable[T::class]
  values.forEach { value ->
    item {
      set(table, value)
    }
  }
}

inline fun <reified T : Any> Database.batchInsert(
  values: Iterable<T>,
  noinline block: AssignmentsBuilder.(AutoTable<T>, value: T) -> Unit,
): IntArray = batchInsert<T> {
  val table = AutoTable[T::class]
  values.forEach { value ->
    item {
      block(table, value)
    }
  }
}

inline fun <reified T : Any> Query.insertTo(vararg columns: Column<*>): Int {
  return insertTo(AutoTable[T::class], columns = columns)
}

inline fun <reified T : Any> Database.delete(
  noinline predicate: (AutoTable<T>) -> ColumnDeclaring<Boolean>,
): Int {
  return delete(AutoTable[T::class], predicate)
}

inline fun <reified T : Any> Database.deleteAll(): Int {
  return deleteAll(AutoTable[T::class])
}
