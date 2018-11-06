package cn.dcs.engine;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import cn.dcs.engine.result.ResultAuth;
import cn.dcs.engine.result.ResultAuthenticationInfo;
import cn.dcs.engine.sql.SelectPreparedStatement;

public class QueryResultCallerSelectDay extends QueryResultCaller {

    HashMap<String, Boolean> hashKeyMap = new HashMap<String, Boolean>();
    HashMap<String, ResultAuthenticationInfo> hashAuthenticationMap = new HashMap<String, ResultAuthenticationInfo>();
    int successNum = 0;

    boolean sussessed = false;

    public QueryResultCallerSelectDay(Session session) throws ParseException {
        super(session);
    }

    public ResultAuth getResultAuth() {
        /**
         * 最多只从传入的时间向上查询Per_Size条记录/org.apache.coyote.http11.Http11Protocol
         */
        SelectPreparedStatement selectpreparedstatement = select.dayDiscoverStatement();
        List<EtlLog> discoverlogs = session.find(selectpreparedstatement);
        if (discoverlogs.size() != 0) {
            ResultAuth auth = new ResultAuth();
            auth.MAC_Address = discoverlogs.get(0).getMacAddr();
            /**
             * 初始化ResultCode为0
             */
            auth.ResultCode = "0";
            for (EtlLog discover : discoverlogs) {
                /** 如果存在AAA Denied */
                if (!discover.getAaaDenied().equals("")) {
                    appendDenied(auth, discover);
                }

                // /** 如果nextTid不存在.... */
                // else if(discover.getNextTid().equals("")) {
                // appendDenied(auth, discover);
                // }

                /** 如果dhcpSent不是offer */
                else if (!(discover.getDhcpSent().equals("offer") || discover.getDhcpSent().equals("ack"))) {
                    appendDenied(auth, discover);
                }

                else {
                    if (discover.getRequestType() != null && discover.getRequestType().equals("rebind")) {
                        appendSuccessAsk(auth, discover, null);
                    } else {
                        selectpreparedstatement = select.daySelectStatement(discover);
                        List<EtlLog> selectlogs = session.find(selectpreparedstatement);
                        if (selectlogs.size() != 0) {
                            EtlLog select = selectlogs.get(0);
                            appendSuccessAsk(auth, discover, select);
                        } else {
                            appendNullSelect(auth, discover);
                        }
                    }
                }
            }
//            auth.RecordNum = discoverlogs.size() + "";
            auth.RecordNum = hashAuthenticationMap.size() + "";
            return pushResultAuthInfomation(auth);
        } else {
            return nullAuth();
        }
    }
    
    private ResultAuth pushResultAuthInfomation(ResultAuth auth) {
    	for (ResultAuthenticationInfo authentication : hashAuthenticationMap.values()) {
			auth.Authentication_Info.add(authentication);
		}
    	return auth;
    }
    
    private String codeKey(String ResultCode, String Auth_Result, String Err_Code) {
    	return ResultCode + "." + Auth_Result + "." + Err_Code;
    }
    
    private boolean canAppend(String key) {
        // 如果这3个code是一样的只返回一条。从最后开始。也就是查询之后的第一条，因为是倒序的。
        if (hashKeyMap.get(key) == null) {
            hashKeyMap.put(key, true);
            return true;
        } else {
            return false;
        }
    }

    private void appendNullSelect(ResultAuth auth, EtlLog discover) {
        auth.ResultCode = "1";
        /***
         * discover information
         */
        ResultAuthenticationInfo discoverAuthenticationInfo = nullSelectAuthenticationInfo(discover);
        String codeKey = codeKey(auth.ResultCode, discoverAuthenticationInfo.Auth_Result, discoverAuthenticationInfo.Err_Code);
        if (canAppend(codeKey)) {
        	discoverAuthenticationInfo.Auth_Begin_Time = discover.getTime();
        	discoverAuthenticationInfo.Auth_End_Time = discover.getTime();
        	discoverAuthenticationInfo.RequestTimes = "1";
        }
        else {
        	discoverAuthenticationInfo = hashAuthenticationMap.get(codeKey);
        	/**
        	 * 因为是倒序排列的，所以begintime才是最后一个值
        	 */
        	discoverAuthenticationInfo.Auth_Begin_Time = discover.getTime();
        	discoverAuthenticationInfo.RequestTimes = "" + (Integer.valueOf(discoverAuthenticationInfo.getRequestTimes()) + 1);
        }
        hashAuthenticationMap.put(codeKey, discoverAuthenticationInfo);
    }

    private void appendSuccessAsk(ResultAuth auth, EtlLog discover, EtlLog select) {
        /**
         * 只要有一条错误,ErrorCode为1
         */
        if (!auth.ResultCode.equals("1")) {
            auth.ResultCode = "0";
        }
        
        ResultAuthenticationInfo authenticationInfo = null;

        authenticationInfo = successAuthenticationInfo(discover, select);
        String codeKey = "";
        if(select == null) {
        	codeKey = codeKey("rebind", authenticationInfo.Auth_Result, authenticationInfo.Err_Code);
        }
        else {
        	codeKey = codeKey("notrebind", authenticationInfo.Auth_Result, authenticationInfo.Err_Code);
        }
        if (canAppend(codeKey)) {
        	authenticationInfo.RequestTimes = "1";
        } else {
        	authenticationInfo = hashAuthenticationMap.get(codeKey);
        	authenticationInfo.RequestTimes = "" + (Integer.valueOf(authenticationInfo.getRequestTimes()) + 1);
        }
        hashAuthenticationMap.put(codeKey, authenticationInfo);
    }

    private void appendDenied(ResultAuth auth, EtlLog discover) {
        auth.ResultCode = "1";
        /**
         * discover information denied aaadenied
         */
        ResultAuthenticationInfo authenticationInfo = deniedAuthenticationInfo(discover);
        String codeKey = codeKey(auth.ResultCode, authenticationInfo.Auth_Result, authenticationInfo.Err_Code);
        if (canAppend(codeKey)) {
        	authenticationInfo.Auth_Begin_Time = discover.getTime();
        	authenticationInfo.Auth_End_Time = discover.getTime();
        	authenticationInfo.RequestTimes = "1";
        }
        else {
        	authenticationInfo = hashAuthenticationMap.get(codeKey);
        	authenticationInfo.Auth_Begin_Time = discover.getTime();
        	authenticationInfo.RequestTimes = "" + (Integer.valueOf(authenticationInfo.getRequestTimes()) + 1);
        }
        hashAuthenticationMap.put(codeKey, authenticationInfo);
    }

    protected void setErrorAuthTimeAndRequestTimes(ResultAuthenticationInfo authenticationInfo, EtlLog log) {
    }
}
