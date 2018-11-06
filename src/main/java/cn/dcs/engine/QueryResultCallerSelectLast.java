package cn.dcs.engine;

import java.text.ParseException;
import java.util.List;

import cn.dcs.engine.result.ResultAuth;
import cn.dcs.engine.result.ResultAuthenticationInfo;
import cn.dcs.engine.sql.SelectPreparedStatement;

/**
 * 查询一个记录的过程，该过程的目的是返回ResultCode，Error_Code,Auth_Result三个代码：
 * 1首先根据MAC地址或用户名判断最近一次是否收到discover报文，
 * 如果收到就进入下一步，没有收到则返回ResultCode=1,Err_Code=0，Auth_Result=空
 * 2判断首先判断“信息”的内容，如果是：ignoring discover: denied by attribute-association
 * hook,则进入下一步， 如果是offer***，则进入第4步返回：ResultCode=0,Err_Code=空，Auth_Result=0
 * 如果是其他信息，则返回错误信息：ResultCode=1,Err_Code=4，Auth_Result=0 3 判断“AAA错误”的内容，返回不同的内容
 * AAA错误 ResultCode Err_Code Auth_Result can't find user. 1 2 1 Password error 1
 * 3 1 server suspend 1 5 1 其他内容 1 5 1 4查找在3秒内，是否收到该用户的select报文，如果有且“信息”内容为ack，
 * 则返回：ResultCode=0,Err_Code=空，Auth_Result=0，
 * 否则返回错误信息：ResultCode=1,Err_Code=1，Auth_Result=0
 * @author Administrator
 */
public class QueryResultCallerSelectLast extends QueryResultCaller {

    public QueryResultCallerSelectLast(Session session) throws ParseException {
        super(session);
    }

    public ResultAuth getResultAuth() {
        SelectPreparedStatement selectpreparedstatement = select.lastDiscoverStatement();
        List<EtlLog> discoverlogs = session.find(selectpreparedstatement);
        if (discoverlogs.size() != 0) {
            EtlLog discover = discoverlogs.get(0);

            /** 如果存在AAA Denied */
            if (!discover.getAaaDenied().equals("")) {
                return denied(discover);
            }

            // /** 如果nextTid不存在.... */
            // else if(discover.getNextTid().equals("")) {
            // return denied(discover);
            // }

            /** 如果dhcpSent不是offer */
            else if (!(discover.getDhcpSent().equals("offer") || discover.getDhcpSent().equals("ack"))) {
                return denied(discover);
            }

            else {
                if (discover.getRequestType() != null && discover.getRequestType().equals("rebind")) {
                    return successAsk(discover, null);
                } else {
                    selectpreparedstatement = select.lastSelectStatement(discover);
                    List<EtlLog> selectlogs = session.find(selectpreparedstatement);
                    if (selectlogs.size() != 0) {
                        EtlLog select = selectlogs.get(0);
                        return successAsk(discover, select);
                    } else {
                        return nullSelect(discover);
                    }
                }
            }
        } else {
            return nullAuth();
        }
    }

    private ResultAuth nullSelect(EtlLog discover) {
        ResultAuth auth = new ResultAuth();
        auth.MAC_Address = discover.getMacAddr();
        auth.ResultCode = "1";
        /***
         * discover information
         */
        ResultAuthenticationInfo discoverAuthenticationInfo = nullSelectAuthenticationInfo(discover);
        setErrorAuthTimeAndRequestTimes(discoverAuthenticationInfo, discover);
        auth.Authentication_Info.add(discoverAuthenticationInfo);
        return auth;
    }

    private ResultAuth successAsk(EtlLog discover, EtlLog select) {
        ResultAuth auth = new ResultAuth();
        auth.MAC_Address = discover.getMacAddr();
        auth.ResultCode = "0";
        auth.RecordNum = "1";
        
        /**
         * if discover dhcpsent = rebind , select is null 
         */
        ResultAuthenticationInfo authenticationInfo = successAuthenticationInfo(discover, select);
        auth.Authentication_Info.add(authenticationInfo);
        return auth;
    }

    private ResultAuth denied(EtlLog discover) {
        ResultAuth auth = new ResultAuth();
        auth.MAC_Address = discover.getMacAddr();
        auth.ResultCode = "1";
        /**
         * discover information denied aaadenied
         */
        ResultAuthenticationInfo authenticationInfo = deniedAuthenticationInfo(discover);
        setErrorAuthTimeAndRequestTimes(authenticationInfo, discover);
        auth.Authentication_Info.add(authenticationInfo);
        return auth;
    }

    /**
     * 针对只查询最近一次记录的信息，当报错的时候返回request times为1 , begin time == end time
     */
    protected void setErrorAuthTimeAndRequestTimes(ResultAuthenticationInfo authenticationInfo, EtlLog log) {
        authenticationInfo.Auth_Begin_Time = log.getTime();
        authenticationInfo.Auth_End_Time = log.getTime();
        authenticationInfo.RequestTimes = "1";
    }

}
