package cn.tursom.database

import cn.tursom.database.wrapper.IUpdateWrapper
import cn.tursom.database.wrapper.Wrapper
import java.io.Serializable

@Suppress("unused")
interface SqlHelper<T> {
  /**
   * 插入一条记录（选择字段，策略插入）
   *
   * @param entity 实体对象
   */
  fun save(entity: T): Boolean

  /**
   * 插入（批量）
   *
   * @param entityList 实体对象集合
   */
  fun saveBatch(entityList: Collection<T>): Int

  ///**
  // * 批量修改插入
  // *
  // * @param entityList 实体对象集合
  // */
  //fun saveOrUpdateBatch(entityList: Collection<T>): Boolean {
  //  return saveOrUpdateBatch(entityList, 1000)
  //}
  //
  ///**
  // * 批量修改插入
  // *
  // * @param entityList 实体对象集合
  // * @param batchSize  每次的数量
  // */
  //fun saveOrUpdateBatch(entityList: Collection<T>, batchSize: Int): Boolean

  /**
   * 根据 ID 删除
   *
   * @param id 主键ID
   */
  fun removeById(id: Serializable?): Boolean

  /**
   * 根据 columnMap 条件，删除记录
   *
   * @param columnMap 表字段 map 对象
   */
  fun removeByMap(columnMap: Map<String, Any?>): Boolean

  /**
   * 根据 entity 条件，删除记录
   */
  fun remove(queryWrapper: Wrapper<T>): Boolean

  /**
   * 删除（根据ID 批量删除）
   *
   * @param idList 主键ID列表
   */
  fun removeByIds(idList: Collection<Serializable>): Boolean

  /**
   * 根据 ID 选择修改
   *
   * @param entity 实体对象
   */
  fun updateById(entity: T): Boolean

  /**
   * 根据 whereEntity 条件，更新记录
   *
   * @param entity        实体对象
   * @param updateWrapper 实体对象封装操作类
   */
  fun update(entity: T?, updateWrapper: IUpdateWrapper<T>): Boolean

  /**
   * 根据 UpdateWrapper 条件，更新记录 需要设置sqlset
   *
   * @param updateWrapper 实体对象封装操作类
   */
  fun update(updateWrapper: IUpdateWrapper<T>): Boolean {
    return update(null, updateWrapper)
  }

  /**
   * 根据ID 批量更新
   *
   * @param entityList 实体对象集合
   */
  fun updateBatchById(entityList: Collection<T>): Boolean {
    return updateBatchById(entityList, 1000)
  }

  /**
   * 根据ID 批量更新
   *
   * @param entityList 实体对象集合
   * @param batchSize  更新批次数量
   */
  fun updateBatchById(entityList: Collection<T>, batchSize: Int): Boolean

  /**
   * TableId 注解存在更新记录，否插入一条记录
   *
   * @param entity 实体对象
   */
  fun saveOrUpdate(entity: T?): Boolean

  /**
   * 根据 ID 查询
   *
   * @param id 主键ID
   */
  fun getById(id: Serializable?): T?

  /**
   * 查询（根据ID 批量查询）
   *
   * @param idList 主键ID列表
   */
  fun listByIds(idList: Collection<Serializable>): Collection<T>

  /**
   * 查询（根据 columnMap 条件）
   *
   * @param columnMap 表字段 map 对象
   */
  fun listByMap(columnMap: Map<String, Any?>): Collection<T>

  /**
   * 根据 Wrapper，查询一条记录
   *
   * @param queryWrapper 实体对象封装操作类
   * @param throwEx      有多个 result 是否抛出异常
   */
  fun getOne(queryWrapper: Wrapper<T>, throwEx: Boolean = true): T?

  /**
   * 根据 Wrapper，查询一条记录
   *
   * @param queryWrapper 实体对象封装操作类
   */
  fun getMap(queryWrapper: Wrapper<T>): Map<String, Any?>

  ///**
  // * 根据 Wrapper，查询一条记录
  // *
  // * @param queryWrapper 实体对象封装操作类
  // * @param mapper       转换函数
  // */
  //fun <V> getObj(queryWrapper: W, mapper: Function<in Any?, V?>?): V?

  /**
   * 根据 Wrapper 条件，查询总记录数
   *
   * @param queryWrapper 实体对象封装操作类
   */
  fun count(queryWrapper: Wrapper<T>): Int

  /**
   * 查询列表
   *
   * @param queryWrapper 实体对象封装操作类
   */
  fun list(queryWrapper: Wrapper<T>): List<T>

  /**
   * 获取对应 entity 的 BaseMapper
   *
   * @return BaseMapper
   */
  fun getBaseMapper(): Mapper<T>

  /**
   *
   *
   * 根据updateWrapper尝试更新，否继续执行saveOrUpdate(T)方法
   * 此次修改主要是减少了此项业务代码的代码量（存在性验证之后的saveOrUpdate操作）
   *
   *
   * @param entity 实体对象
   */
  fun saveOrUpdate(entity: T?, updateWrapper: IUpdateWrapper<T>): Boolean {
    return update(entity, updateWrapper) || saveOrUpdate(entity)
  }
}
