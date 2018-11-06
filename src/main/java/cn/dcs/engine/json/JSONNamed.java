package cn.dcs.engine.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存JSON对象对应的JAVA对象的Class和Method
 * @author Administrator
 */
public class JSONNamed {

    private static Map<String, Map<String, String>> classMap = new HashMap<String, Map<String, String>>();

    public static String get(Class<?> clazz, String name) {
        String clazzName = clazz.getName();
        if (classMap.get(clazzName) == null) {
            Field[] fields = clazz.getDeclaredFields();
            Map<String, String> fieldMap = new HashMap<String, String>();
            for (Field field : fields) {
                fieldMap.put(field.getName().toLowerCase(), field.getName());
            }
            classMap.put(clazzName, fieldMap);
        }
        /**
         * 加入的方法，取原始的字段名。而不需要当第二个字母为小写的时候 对第一个字母小写 PS : 会出现 name 传入 为 class 的情况
         * @link JSONWriter private void bean(Object object) throws
         *       JSONException PropertyDescriptor prop = props[i]; String name =
         *       prop.getName();
         */
        return classMap.get(clazzName).get(name.toLowerCase());
    }

}
