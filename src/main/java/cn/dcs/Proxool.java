package cn.dcs;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;

/**
 * 连接池加载
 * @author Administrator
 */
public class Proxool {

    private static final Log LOG = LogFactory.getLog(Proxool.class);

    private static final String XML_FILE_PROPERTY = "xmlFile";

    private static final String PROPERTY_FILE_PROPERTY = "propertyFile";

    private static final String AUTO_SHUTDOWN_PROPERTY = "autoShutdown";

    public static void config(ServletContext context) {
        String appDir = context.getRealPath("/");
        Properties properties = new Properties();
        Enumeration<?> names = context.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = context.getInitParameter(name);
            if (name.equals(XML_FILE_PROPERTY)) {
                try {
                    File file = new File(value);
                    if (file.isAbsolute())
                        JAXPConfigurator.configure(value, false);
                    else
                        JAXPConfigurator.configure(appDir + "/" + value, false);
                } catch (ProxoolException e) {
                    LOG.error("Problem configuring " + value, e);
                }
            } else if (name.equals(PROPERTY_FILE_PROPERTY)) {
                try {
                    File file = new File(value);
                    if (file.isAbsolute())
                        PropertyConfigurator.configure(value);
                    else
                        PropertyConfigurator.configure(appDir + "/" + value);
                } catch (ProxoolException e) {
                    LOG.error("Problem configuring " + value, e);
                }
            } else if (name.equals(AUTO_SHUTDOWN_PROPERTY)) {} else if (name.startsWith("jdbc")) { // 此处以前是PropertyConfigurator.PREFIX改为jdbc
                properties.setProperty(name, value);
            }
        }
        if (properties.size() > 0) {
            try {
                PropertyConfigurator.configure(properties);
            } catch (ProxoolException e) {
                LOG.error("Problem configuring using init properties", e);
            }
        }
    }
}
