package cn.dcs.engine;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.dcs.Log;
import cn.dcs.engine.exception.QueryException;
import cn.dcs.engine.sql.SelectPreparedStatement;

/**
 * 一次request 包含Connection和attribute参数等
 * 
 * @author Administrator
 */
public class Session {

    public void release() {
        Connector.closeConnection(connection);
    }

    public CallerAttribute getAttribute() {
        return attribute;
    }

    public void validation() {
        attribute.validation();
    }

    CallerAttribute attribute;

    private Connection connection;

    public Session(String queryNumber, String queryBeginTime, String queryEndTime, int queryFlag) {
        attribute = new CallerAttribute(queryNumber, queryBeginTime, queryEndTime, queryFlag);
        connection = Connector.getConnection();
    }

    /**
     * 判断表是否存在
     * 
     * @return
     */
    public boolean check(String tableName) {
        ResultSet rs = null;
        try {
            String[] tn = tableName.split("\\.");
            if (tn.length == 2) {
                rs = connection.getMetaData().getTables(tn[0], null, tn[1], null);
            } else {
                rs = connection.getMetaData().getTables(null, null, tableName, null);
            }
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.error("\r\nREASON : " + e.getMessage() + "\r\nTABLE : " + tableName, e);
            throw new QueryException("104");
        } finally {
            Connector.closeResultSet(rs);
        }
        return false;
    }

    protected List<EtlLog> find(SelectPreparedStatement selectpreparedstatement) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        SessionRow logRow = new SessionRow();
        List<EtlLog> logs = new ArrayList<EtlLog>();
        try {
            ps = connection.prepareStatement(selectpreparedstatement.sqlStatment);
            int i = 1;
            for (Serializable serializable : selectpreparedstatement.values) {
                ps.setObject(i++, serializable);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                logs.add(logRow.row(rs));
            }
            return logs;
        } catch (Exception e) {
            Log.error("\r\nREASON : " + e.getMessage() + "\r\nSQL : " + selectpreparedstatement.sqlStatment, e);
            throw new QueryException("103");
        } finally {
            Connector.closeResultSet(rs);
            Connector.closePreparedStatement(ps);
        }

    }
}
