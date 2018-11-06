package cn.dcs.engine.result;

import java.util.ArrayList;
import java.util.List;

import cn.dcs.util.QueryUtil;

public class ResultAuthenticationInfo {

    public String IP_Address;

    public String Auth_Result;

    public String Err_Code;

    public String Auth_Begin_Time;

    public String Auth_End_Time;

    public String RequestTimes;

    public List<ResultInteractiveLog> Interactive_Log = new ArrayList<ResultInteractiveLog>();

    public String getIP_Address() {
        return IP_Address;
    }

    public String getAuth_Result() {
        return Auth_Result;
    }

    public String getErr_Code() {
        return Err_Code;
    }

    public String getAuth_End_Time() {
        return QueryUtil.stringtime14To19(Auth_End_Time);
    }

    public String getAuth_Begin_Time() {
        return QueryUtil.stringtime14To19(Auth_Begin_Time);
    }

    public String getRequestTimes() {
        return RequestTimes;
    }

    public List<ResultInteractiveLog> getInteractive_Log() {
        return Interactive_Log;
    }

}
