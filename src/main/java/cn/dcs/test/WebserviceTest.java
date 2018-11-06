package cn.dcs.test;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.chinatelecom.iptv.dhcp.IptvDhcpEngineCall;
import com.chinatelecom.iptv.dhcp.rsp.DhcpAlarmPushRsp;

public class WebserviceTest {

	public static void main(String[] args) throws RemoteException, ServiceException {
		DhcpAlarmPushRsp rep = IptvDhcpEngineCall.call("http://localhost:8080/webserver/services/IptvDhcpEngine?wsdl", "request");
		System.out.println(rep.getResponse());
	}
}
