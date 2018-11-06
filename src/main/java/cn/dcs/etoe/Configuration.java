package cn.dcs.etoe;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.dcs.Log;

/**
 * @author wanghaiinfo
 * @version 1.0, Mar 29, 2012
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class);

    private Properties pro = null;

    public Configuration() {

        pro = new Properties();

        try {

            pro.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("warn.properties"));

        } catch (IOException e) {

            Log.error("load properties error");

        }
    }

    public String get(String key) {

        return pro.getProperty(key);

    }

    String start;

    String end;

    String day;

    public void initStart(Date date) {

        Calendar c = Calendar.getInstance();

        c.setTime(date);

        c.add(Calendar.MINUTE, -1);

        c.set(Calendar.SECOND, 59);

        c.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");

        SimpleDateFormat s2 = new SimpleDateFormat("yyyyMMddHHmmss");

        day = s.format(c.getTime());

        end = s2.format(c.getTime());

        c.add(Calendar.MINUTE, -4);

        c.set(Calendar.SECOND, 0);

        c.set(Calendar.MILLISECOND, 0);

        start = s2.format(c.getTime());

        if (logger.isDebugEnabled())

        logger.debug("set warn configuration time " + start + " / " + end + " / " + day);

    }

    public static void main(String[] args) throws ParseException {
        Configuration c = new Configuration();
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = s.parse("2012033000000");
        c.initStart(d);
        System.out.println(c.start);
        System.out.println(c.end);
        System.out.println(c.day);
    }

}
