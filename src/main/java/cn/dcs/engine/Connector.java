package cn.dcs.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.logicalcobwebs.proxool.ProxoolDataSource;

import cn.dcs.Log;

/**
 * 连接器 获得数据库连接 释放Connection、释放PreparedStatment、释放ResultSet
 */
public class Connector {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            ProxoolDataSource envCtx;
            Context initCtx = new InitialContext();
            envCtx = (ProxoolDataSource) initCtx.lookup("SERVICE.JNDI");
            conn = envCtx.getConnection();
        } catch (NamingException e) {
            Log.error(e.getMessage());
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
        return conn;
    }

    public static void closePreparedStatement(PreparedStatement ps) {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {}
    }

    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {}
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {}
    }
}
