package cn.dcs.engine.json;

import cn.dcs.Log;
import cn.dcs.engine.exception.JSONException;

/**
 * 转换JAVA对象为JSON对象
 * @author Administrator
 */
public class JSON {

    public static String serialize(Object object) {
        try {
            return new JSONWriter().write(object);
        } catch (JSONException e) {
            Log.error(e.getMessage());
        }
        return null;
    }

}
