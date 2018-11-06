package cn.dcs;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 主日志配置文件
 * @author Administrator
 */
public class Log {

    private static String rootLoggerFilePath = "";

    private static Logger log = Logger.getLogger("日志");
    
    public static Logger getLogger() {
    	return log;
    }

    public static synchronized void warn(String msg) {
        log.warn(msg);
    }

    public static synchronized void warn(String msg, Throwable t) {
        log.warn(msg, t);
    }

    public static synchronized void error(String msg) {
        log.error(msg);
    }

    public static synchronized void error(String msg, Throwable t) {
        log.error(msg, t);
    }

    public static void setRootLoggerLocalePath(String path) {
        Properties props = new Properties();
        try {
            props.load(Log.class.getClassLoader().getResourceAsStream("log4j.properties"));
            String logFile = path + props.getProperty("log4j.appender.file.File");// 设置路径
            props.setProperty("log4j.appender.file.File", logFile);
            PropertyConfigurator.configure(props);
            rootLoggerFilePath = logFile;
        } catch (IOException e) {}
    }

    public static String getRootLoggerLoaclePath() {
        return rootLoggerFilePath;
    }

}
