package cn.dcs.etoe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.dcs.engine.Connector;

/**
 * @author wanghaiinfo
 * @version 1.0, Mar 29, 2012
 */
public class EtldbSelector {

    private static final Logger logger = Logger.getLogger(EtldbSelector.class);

    public void release() {

        Connector.closeConnection(connection);

    }

    private Connection connection = Connector.getConnection();

    public List<String> getTables() {

        ResultSet rs = null;

        List<String> tables = new ArrayList<String>();

        try {

            rs = connection.getMetaData().getTables(null, null, null, null);

            while (rs.next()) {

                tables.add(rs.getString("TABLE_NAME"));

            }

        } catch (SQLException e) {

            logger.error(e);

        } finally {

            Connector.closeResultSet(rs);

        }
        return tables;
    }

    private String tableIp(String table) {
        String[] s = table.split("_");
        return s[2] + "." + s[3] + "." + s[4] + "." + s[5];
    }

    public void doSearchPool(String table, Configuration conf, SqliteHelper sqlite) {

        ResultSet rs = null;

        PreparedStatement ps = null;

        try {

            String sql = "select * from " + table + " where  ts >= " + conf.start + " and ts <= " + conf.end;

            String ip = tableIp(table);

            ps = connection.prepareStatement(sql);

            rs = ps.executeQuery();

            while (rs.next()) {

                WarningPool pool = new WarningPool();

                pool.rowAfter(rs, conf, sqlite, ip);

            }

            if (logger.isDebugEnabled()) {

                logger.debug("extract pool info over, sql:" + sql);

            }

        } catch (SQLException e) {

            logger.error(e);

        } finally {

            Connector.closeResultSet(rs);

            Connector.closePreparedStatement(ps);

        }
    }

}
