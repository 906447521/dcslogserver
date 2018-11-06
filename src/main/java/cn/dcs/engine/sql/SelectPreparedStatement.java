package cn.dcs.engine.sql;

import java.io.Serializable;

public class SelectPreparedStatement {

    SelectPreparedStatement(String sql, Serializable... args) {
        sqlStatment = sql;
        values = args;
    }

    public String sqlStatment;

    public Serializable[] values;

}
