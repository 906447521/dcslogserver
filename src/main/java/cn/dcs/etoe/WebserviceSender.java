package cn.dcs.etoe;

import org.apache.log4j.Logger;

import cn.dcs.engine.json.JSON;

import com.chinatelecom.iptv.dhcp.IptvDhcpEngineCall;
import com.chinatelecom.iptv.dhcp.rsp.DhcpAlarmPushRsp;

public class WebserviceSender {

    private static final Logger logger = Logger.getLogger(WebserviceSender.class);

    private static String endpoint;

    static {

        Configuration c = new Configuration();
        endpoint = c.get("warn.wsdl");
        
    }

    public static String send(Warn entry) {

        long s = System.currentTimeMillis();

        try {

            String json = JSON.serialize(entry);

            DhcpAlarmPushRsp result = (DhcpAlarmPushRsp) IptvDhcpEngineCall.call(endpoint, json);

            if (result == null) {
            	
            	logger.warn("e to e, null result");
            	
            } else if (
            	   result.getResponse().indexOf("OK") != -1 
        		|| result.getResponse().indexOf("ok") != -1 
        		|| result.getResponse().equalsIgnoreCase("ok") ) {

                return "ok";

            } else {

                logger.warn("e to e, receive message : " + result.getResponse());

            }

        } catch (Exception e) {

            logger.error(e.getMessage());

        } finally {

            if (logger.isDebugEnabled()) {
                
                logger.debug("WSDL ," + (System.currentTimeMillis() - s) + " ms.");
                
            }
        }

        return null;
    }

    public static void main(String[] args) {

    }
}
