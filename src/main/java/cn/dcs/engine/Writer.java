package cn.dcs.engine;

import cn.dcs.Log;
import cn.dcs.engine.json.JSON;
import cn.dcs.engine.result.ResultAuth;

/**
 * JSON输出器
 * @author Administrator
 */
public class Writer {

    /**
     * 正常输出
     * @param e
     * @return
     */
    public static String error(Exception e) {
        ResultAuth result = new ResultAuth();
        result.ResultCode = "2";
        result.MAC_Address = "";
        Log.error(e.getMessage(), e);
        return JSON.serialize(result);
    }

    /**
     * 其他报错
     * @param result
     * @return
     */
    public static String info(ResultAuth result) {
        return JSON.serialize(result);
    }
}
