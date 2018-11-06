package cn.dcs.engine;

import java.text.ParseException;

/**
 * 获得查询规则
 * @author Administrator
 */
public class CallerBuildFactory {

    /**
     * 如果 QueryFlag为0 查询最近的一条记录 QueryFlag为1 查询一天内所有的
     * @param session
     * @return
     * @throws ParseException 
     */
    public static Caller createCaller(Session session) throws ParseException {
        if (session.attribute.queryFlag == 0)
            return new QueryResultCallerSelectLast(session);
        else
            return new QueryResultCallerSelectDay(session);
    }

}
