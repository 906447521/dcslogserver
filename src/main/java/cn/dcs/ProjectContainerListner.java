package cn.dcs;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cn.dcs.etoe.JobScheduler;
import cn.dcs.etoe.Statistics;

/**
 * 监听器
 * @author Administrator
 */
public class ProjectContainerListner implements ServletContextListener {

    private static Logger log = Logger.getLogger("容器监听");

    static {
        log.setLevel(Level.INFO);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        log.info("destroy container ....");
    }

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String path = context.getRealPath("") + File.separator + "logs" + File.separator;
        Log.setRootLoggerLocalePath(path);
        Proxool.config(context);
        JobScheduler.instance().start();
        Statistics.start();
        Log.warn("启动加载成功咯...");
    }

}
