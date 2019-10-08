@file:Suppress("SqlDialectInspection", "SqlNoDataSourceInspection")

package cn.tursom.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

//////////////////////////////// 数据库连接池类 ConnectionPool.java ////////////////////////////////////////

/*
     这个例子是根据POSTGRESQL数据库写的，
     请用的时候根据实际的数据库调整。
     调用方法如下：
     ①　
     ConnectionPool connPool
     = new ConnectionPool("com.microsoft.jdbc.sqlserver.SQLServerDriver"
     ,"jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=MyDataForTest"
     ,"Username"
     ,"Password");
     ②　
     Connection conn = connPool .getConnection();
     connPool.returnConnection(conn);
     connPool.refreshConnections();
     connPool.closeConnectionPool();
     */

class ConnectionPool
// 它中存放的对象为 PooledConnection 型
/**
 * 构造函数
 *
 * @param jdbcDriver
 * String JDBC 驱动类串
 * @param dbUrl
 * String 数据库 URL
 * @param dbUsername
 * String 连接数据库用户名
 * @param dbPassword
 * String 连接数据库用户的密码
 */
(
	private val jdbcDriver: String = "",// 数据库驱动
	private val dbUrl: String = "",// 数据库驱动
	private val dbUsername: String = "",// 数据库用户名
	private val dbPassword: String = ""// 数据库用户密码
) {
	
	/**
	 * 测试表的名字
	 */
	var testTable = "" // 测试连接是否可用的测试表名，默认没有测试表
	
	/**
	 * 连接池中最大可用的连接数量
	 */
	var maxConnections = 50 // 连接池最大的大小
	
	private var connections = Vector<PooledConnection>(0) // 存放连接池中数据库连接的向量 , 初始时为 null
	
	/**
	 * 通过调用 getFreeConnection() 函数返回一个可用的数据库连接 , 如果当前没有可用的数据库连接，并且更多的数据库连接不能创
	 * 建（如连接池大小的限制），此函数等待一会再尝试获取。
	 *
	 * @return 返回一个可用的数据库连接对象
	 */
	val connection: Connection?
		@Synchronized @Throws(SQLException::class)
		get() = findFreeConnection()
	
	
	/**
	 * 创建一个新的数据库连接并返回它
	 *
	 * @return 返回一个新创建的数据库连接
	 */
	private fun newConnection(): Connection {
		// 创建一个数据库连接
		val conn = DriverManager.getConnection(dbUrl, dbUsername,
			dbPassword)
		// 如果这是第一次创建数据库连接，即检查数据库，获得此数据库允许支持的
		// 最大客户连接数目
		// connections.size()==0 表示目前没有连接己被创建
		if (connections.size == 0) {
			val metaData = conn.metaData
			val driverMaxConnections = metaData.maxConnections
			// 数据库返回的 driverMaxConnections 若为 0 ，表示此数据库没有最大
			// 连接限制，或数据库的最大连接限制不知道
			// driverMaxConnections 为返回的一个整数，表示此数据库允许客户连接的数目
			// 如果连接池中设置的最大连接数量大于数据库允许的连接数目 , 则置连接池的最大
			// 连接数目为数据库允许的最大数目
			if (driverMaxConnections > 0 && this.maxConnections > driverMaxConnections) {
				this.maxConnections = driverMaxConnections
			}
		}
		return conn // 返回创建的新的数据库连接
	}
	
	/**
	 * 查找连接池中所有的连接，查找一个可用的数据库连接， 如果没有可用的连接，返回 null
	 *
	 * @return 返回一个可用的数据库连接
	 */
	private fun findFreeConnection(): Connection? {
		var conn: Connection?
		connections.forEach {
			if (!it.isBusy) {
				// 如果此对象不忙，则获得它的数据库连接并把它设为忙
				conn = it.connection
				it.isBusy = true
				// 测试此连接是否可用
				if (!testConnection(conn)) {
					// 如果此连接不可再用了，则创建一个新的连接，
					// 并替换此不可用的连接对象，如果创建失败，返回 null
					try {
						conn = newConnection()
					} catch (e: SQLException) {
						System.err.println(" 创建数据库连接失败！ " + e.message)
						return null
					}
					it.connection = conn
				}
				return conn// 己经找到一个可用的连接，退出
			}
		}
		conn = newConnection()
		return conn// 返回找到到的可用连接
	}
	
	/**
	 * 测试一个连接是否可用，如果不可用，关掉它并返回 false 否则可用返回 true
	 *
	 * @param conn 需要测试的数据库连接
	 * @return 返回 true 表示此连接可用， false 表示不可用
	 */
	private fun testConnection(conn: Connection?): Boolean {
		try {
			// 判断测试表是否存在
			if (testTable == "") {
				// 如果测试表为空，试着使用此连接的 setAutoCommit() 方法
				// 来判断连接否可用（此方法只在部分数据库可用，如果不可用 ,
				// 抛出异常）。注意：使用测试表的方法更可靠
				conn!!.autoCommit = true
			} else {// 有测试表的时候使用测试表测试
				// check if this connection is valid
				val stmt = conn!!.createStatement()
				stmt.execute("select count(*) from $testTable")
			}
		} catch (e: SQLException) {
			// 上面抛出异常，此连接己不可用，关闭它，并返回 false;
			closeConnection(conn!!)
			return false
		}
		
		// 连接可用，返回 true
		return true
	}
	
	/**
	 * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
	 *
	 * @param conn 需返回到连接池中的连接对象
	 */
	
	fun returnConnection(conn: Connection) {
		// 遍历连接池中的所有连接，找到这个要返回的连接对象
		connections.forEach {
			// 先找到连接池中的要返回的连接对象
			if (conn === it.connection) {
				// 找到了 , 设置此连接为空闲状态
				it.isBusy = false
				return
			}
		}
	}
	
	/**
	 * 刷新连接池中所有的连接对象
	 *
	 */
	@Synchronized
	@Throws(SQLException::class)
	fun refreshConnections() {
		connections.forEach {
			// 如果对象忙则等 5 秒 ,5 秒后直接刷新
			if (it.isBusy) {
				wait(5000) // 等 5 秒
			}
			// 关闭此连接，用一个新的连接代替它。
			closeConnection(it.connection!!)
			it.connection = newConnection()
			it.isBusy = false
		}
	}
	
	/**
	 * 关闭连接池中所有的连接，并清空连接池。
	 */
	@Synchronized
	@Throws(SQLException::class)
	fun closeConnectionPool() {
		var pConn: PooledConnection
		val enumerate = connections.elements()
		while (enumerate.hasMoreElements()) {
			pConn = enumerate.nextElement() as PooledConnection
			// 如果忙，等 5 秒
			if (pConn.isBusy) {
				wait(5000) // 等 5 秒
			}
			// 5 秒后直接关闭它
			closeConnection(pConn.connection!!)
			// 从连接池向量中删除它
			connections.removeElement(pConn)
		}
	}
	
	/**
	 * 关闭一个数据库连接
	 *
	 * @param conn 需要关闭的数据库连接
	 */
	private fun closeConnection(conn: Connection) {
		try {
			conn.close()
		} catch (e: SQLException) {
			System.err.println(" 关闭数据库连接出错： " + e.message)
		}
		
	}
	
	/**
	 * 使程序等待给定的毫秒数
	 *
	 * @param mSeconds 给定的毫秒数
	 */
	
	private fun wait(mSeconds: Int) {
		try {
			Thread.sleep(mSeconds.toLong())
		} catch (e: InterruptedException) {
		}
	}
	
	/**
	 *
	 * 内部使用的用于保存连接池中连接对象的类 此类中有两个成员，一个是数据库的连接，另一个是指示此连接是否 正在使用的标志。
	 */
	
	class PooledConnection// 构造函数，根据一个 Connection 构告一个 PooledConnection 对象
	(connection: Connection) {
		// 返回此对象中的连接
		// 设置此对象的，连接
		var connection: Connection? = null// 数据库连接
		// 获得对象连接是否忙
		// 设置对象的连接正在忙
		var isBusy = false // 此连接是否正在使用的标志，默认没有正在使用
		
		init {
			this.connection = connection
		}
	}
	
}