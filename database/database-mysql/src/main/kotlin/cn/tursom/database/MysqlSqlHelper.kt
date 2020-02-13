package cn.tursom.database

import cn.tursom.database.wrapper.MysqlWrapper
import java.io.Serializable

class MysqlSqlHelper<T> : SqlHelper<T, MysqlWrapper<T>> {
  override fun save(entity: T): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun saveBatch(entityList: Collection<T>, batchSize: Int): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun saveOrUpdateBatch(entityList: Collection<T>, batchSize: Int): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removeById(id: Serializable?): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removeByMap(columnMap: Map<String, Any?>): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun remove(queryWrapper: MysqlWrapper<T>): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removeByIds(idList: Collection<Serializable>): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun updateById(entity: T): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun update(entity: T?, updateWrapper: MysqlWrapper<T>): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun updateBatchById(entityList: Collection<T>, batchSize: Int): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun saveOrUpdate(entity: T?): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getById(id: Serializable?): T? {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun listByIds(idList: Collection<Serializable>): Collection<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun listByMap(columnMap: Map<String, Any?>): Collection<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getOne(queryWrapper: MysqlWrapper<T>, throwEx: Boolean): T? {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getMap(queryWrapper: MysqlWrapper<T>): Map<String, Any?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun count(queryWrapper: MysqlWrapper<T>): Int {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun list(queryWrapper: MysqlWrapper<T>): List<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getBaseMapper(): Mapper<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}