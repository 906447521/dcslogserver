/*
 * Copyright (c) 2012 www.360buy.com. All rights reserved.
 * 本软件源代码版权归京东商城-后台成都研究院所有,未经许可不得任意复制与传播.
 */
package cn.dcs.etoe;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author wanghaiinfo
 * @version 1.0, Mar 30, 2012
 */
public class SqliteHelper {
	
	private static final Object lock = new Object();

	private static final Logger logger = Logger.getLogger(SqliteHelper.class);

	private static String DB_NAME = "WARN";

	private static String DB_PATH;

	private final static String TABLE_NAME = "MCACHE";

	private static int AlarmId;

	static {
		try {
			Class.forName("org.sqlite.JDBC").newInstance();
			DB_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "sqlite";
			File dbFile = new File(DB_PATH);
			if (!dbFile.exists()) {
				dbFile.mkdirs();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查数据库表结构
	 */
	public static void init() {
		String checkExists = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' and name = ?";
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(checkExists);
			pstmt.setString(1, TABLE_NAME);
			ResultSet rs = pstmt.executeQuery();
			boolean exists = false;
			if (rs.next()) {
				exists = rs.getInt(1) > 0;
			}
			close(rs, pstmt);
			if (!exists) {
				StringBuffer table = new StringBuffer("CREATE TABLE ");
				table.append(TABLE_NAME).append(" (");
				table.append("AlarmId varchar(12) PRIMARY KEY,");
				table.append("FaultStartTime varchar(18),");
				table.append("FaultObjectType int,");
				table.append("FaultObject varchar(200),");
				table.append("FaultObjectName varchar(200),");
				table.append("FaultValue varchar(50),");
				table.append("FaultThreshold varchar(50),");
				table.append("FaultType int,");
				table.append("FaultLevel int,");
				table.append("FaultDescribe varchar(200),");
				table.append("FaultClearTime varchar(18)");
				table.append(")");// 结束
				pstmt = conn.prepareStatement(table.toString());
				pstmt.executeUpdate();
			}
			close(pstmt);

			pstmt = conn.prepareStatement("SELECT COUNT(1) FROM " + TABLE_NAME);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				AlarmId = rs.getInt(1);
			}
			close(rs, pstmt, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @param conn
	 */
	public static void rollback(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH + "/" + DB_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param dbObj
	 *            数据库对象,conn,pstmt,stmt,rs等
	 */
	public static void close(Object... dbObj) {
		if (dbObj != null) {
			for (Object obj : dbObj) {
				try {
					if (obj != null && obj instanceof ResultSet) {
						((ResultSet) obj).close();
					} else if (obj != null && obj instanceof PreparedStatement) {
						((PreparedStatement) obj).close();
					} else if (obj != null && obj instanceof Statement) {
						((Statement) obj).close();
					} else if (obj != null && obj instanceof Connection) {
						((Connection) obj).close();
					}
					obj = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void release() {
		close(conn);
	}

	private Connection conn = getConnection();

	private Warn setWarn(ResultSet rs) throws SQLException {
		Warn warn = new Warn();
		warn.setAlarmId(rs.getString("AlarmId"));
		warn.setFaultStartTime(rs.getString("FaultStartTime"));
		warn.setFaultObjectType(rs.getInt("FaultObjectType"));
		warn.setFaultObject(rs.getString("FaultObject"));
		warn.setFaultObjectName(rs.getString("FaultObjectName"));
		warn.setFaultValue(rs.getString("FaultValue"));
		warn.setFaultThreshold(rs.getString("FaultThreshold"));
		warn.setFaultType(rs.getInt("FaultType"));
		warn.setFaultLevel(rs.getInt("FaultLevel"));
		warn.setFaultDescribe(rs.getString("FaultDescribe"));
		warn.setFaultClearTime(rs.getString("FaultClearTime"));
		return warn;

	}

	public Warn getKeyPair(int FaultObjectType, String FaultObjectName) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM " + TABLE_NAME
					+ " WHERE FaultObjectType = ? and FaultObjectName = ? ");
			ps.setInt(1, FaultObjectType);
			ps.setString(2, FaultObjectName);
			rs = ps.executeQuery();
			if (rs.next()) {
				return setWarn(rs);
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			close(rs, ps);
		}
		return null;
	}

	public void removeKeyPair(int FaultObjectType, String FaultObjectName) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE FaultObjectType = ? and FaultObjectName = ?");
			ps.setInt(1, FaultObjectType);
			ps.setString(2, FaultObjectName);
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
		}
		close(ps);
	}

	public static int automic() {
		synchronized (lock) {
			return AlarmId++;
		}
	}

	public void insertKeyPair(Warn warn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO " + TABLE_NAME + " VALUES(?,?,?,?,?,?,?,?,?,?,?)");
			int i = 0;
			ps.setString(++i, warn.getAlarmId());
			ps.setString(++i, warn.getFaultStartTime());
			ps.setInt(++i, warn.getFaultObjectType());
			ps.setString(++i, warn.getFaultObject());
			ps.setString(++i, warn.getFaultObjectName());
			ps.setString(++i, warn.getFaultValue());
			ps.setString(++i, warn.getFaultThreshold());
			ps.setInt(++i, warn.getFaultType());
			ps.setInt(++i, warn.getFaultLevel());
			ps.setString(++i, warn.getFaultDescribe());
			ps.setString(++i, warn.getFaultClearTime());
			ps.executeUpdate();

		} catch (SQLException e) {
			logger.error(e);
		}
		close(ps);
	}
	
	public static void main(String[] args) {
		SqliteHelper.automic();
		System.out.println(SqliteHelper.AlarmId);
	}
}
