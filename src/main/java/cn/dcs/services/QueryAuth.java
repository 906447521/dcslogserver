package cn.dcs.services;

import cn.dcs.engine.CallerBuildFactory;
import cn.dcs.engine.Session;
import cn.dcs.engine.Writer;

public class QueryAuth {

    /**
     * queryNumber 
     *  [0]XXX-接入号 
     *  [1]XXX-机顶盒MAC地址 
     * 
     * queryFlag 
     *  0-查最近一次的记录
     *  1-查一天所有的记录
     */
    public String receive(String queryNumber, String queryBeginTime, String queryEndTime, int queryFlag) {
    	
    	// MAC 不用转换为大写
    	if(queryNumber != null && queryNumber.startsWith("[0"))
    		queryNumber = queryNumber.toUpperCase();
    	
    	Session session = new Session(queryNumber, queryBeginTime, queryEndTime, queryFlag);
        try {
            session.validation();
            return Writer.info(CallerBuildFactory.createCaller(session).getResultAuth());
        } catch (Exception e) {
            return Writer.error(e);
        } finally {
            session.release();
        }
    }

}
