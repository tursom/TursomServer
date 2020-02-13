package cn.tursom.database

import cn.tursom.database.annotations.Param
import cn.tursom.database.wrapper.Wrapper
import java.io.Serializable

/**
 * Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得CRUD功能
 *
 * 这个 Mapper 支持 id 泛型
 *
 * @author hubin
 * @since 2016-01-23
 */
interface Mapper<T> {
  /**
   * 插入一条记录
   *
   * @param entity 实体对象
   */
  fun insert(entity: T): Int

  /**
   * 根据 ID 删除
   *
   * @param id 主键ID
   */
  fun deleteById(id: Serializable): Int

  /**
   * 根据 columnMap 条件，删除记录
   *
   * @param columnMap 表字段 map 对象
   */
  fun deleteByMap(@Param(Constants.COLUMN_MAP) columnMap: Map<String, Any?>): Int

  /**
   * 根据 entity 条件，删除记录
   *
   * @param wrapper 实体对象封装操作类（可以为 null）
   */
  fun delete(@Param(Constants.WRAPPER) wrapper: Wrapper<T>): Int

  /**
   * 删除（根据ID 批量删除）
   *
   * @param idList 主键ID列表(不能为 null 以及 empty)
   */
  fun deleteBatchIds(@Param(Constants.COLLECTION) idList: Collection<Serializable>): Int

  /**
   * 根据 ID 修改
   *
   * @param entity 实体对象
   */
  fun updateById(@Param(Constants.ENTITY) entity: T): Int

  /**
   * 根据 whereEntity 条件，更新记录
   *
   * @param entity        实体对象 (set 条件值,可以为 null)
   * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
   */
  fun update(@Param(Constants.ENTITY) entity: T, @Param(Constants.WRAPPER) updateWrapper: Wrapper<T>): Int

  /**
   * 根据 ID 查询
   *
   * @param id 主键ID
   */
  fun selectById(id: Serializable): T

  /**
   * 查询（根据ID 批量查询）
   *
   * @param idList 主键ID列表(不能为 null 以及 empty)
   */
  fun selectBatchIds(@Param(Constants.COLLECTION) idList: Collection<Serializable>): List<T>

  /**
   * 查询（根据 columnMap 条件）
   *
   * @param columnMap 表字段 map 对象
   */
  fun selectByMap(@Param(Constants.COLUMN_MAP) columnMap: Map<String?, Any?>?): List<T>

  /**
   * 根据 entity 条件，查询一条记录
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   */
  fun selectOne(@Param(Constants.WRAPPER) queryWrapper: Wrapper<T>): T

  /**
   * 根据 Wrapper 条件，查询总记录数
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   */
  fun selectCount(@Param(Constants.WRAPPER) queryWrapper: Wrapper<T>): Int

  /**
   * 根据 entity 条件，查询全部记录
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   */
  fun selectList(@Param(Constants.WRAPPER) queryWrapper: Wrapper<T>): List<T>

  /**
   * 根据 Wrapper 条件，查询全部记录
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   */
  fun selectMaps(@Param(Constants.WRAPPER) queryWrapper: Wrapper<T>): List<Map<String, Any?>>

  /**
   * 根据 Wrapper 条件，查询全部记录
   *
   * 注意： 只返回第一个字段的值
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   */
  fun selectObjs(@Param(Constants.WRAPPER) queryWrapper: Wrapper<T>): List<Any>
}