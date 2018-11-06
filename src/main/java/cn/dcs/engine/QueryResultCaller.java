package cn.dcs.engine;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Properties;

import cn.dcs.Log;
import cn.dcs.engine.result.ResultAuth;
import cn.dcs.engine.result.ResultAuthenticationInfo;
import cn.dcs.engine.result.ResultInteractiveLog;
import cn.dcs.engine.sql.Select;

/**
 * 一次Session查询器
 * 
 * @author Administrator
 */
public abstract class QueryResultCaller implements Caller {

	protected Session session;

	protected Select select;

	private final static Properties errorMap = new Properties();

	static {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("error.properties");
		try {
			errorMap.load(in);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	// protected abstract void
	// setErrorAuthTimeAndRequestTimes(ResultAuthenticationInfo
	// authenticationInfo, EtlLog log);

	/**
	 * 构造器 初始化一些 基本数据
	 * 
	 * @param session
	 * @throws ParseException
	 */
	public QueryResultCaller(Session session) throws ParseException {
		this.session = session;
		select = new Select(session);
		check();
	}

	/**
	 * 判断表是否存在
	 * 
	 * @return
	 */
	protected void check() {
		/**
		 * 2012年2月2号修改-验证不需要了，在new select的时候判断存在的表的集合
		 */
		// boolean priHasTable = session.check(select.getPRI());
		// boolean secHasTable = session.check(select.getSEC());
		// if (!(priHasTable && secHasTable)) throw new QueryException("104");
	}

	/**
	 * 没有查询到任何discover的数据
	 * 
	 * @return
	 */
	protected ResultAuth nullAuth() {
		ResultAuth auth = new ResultAuth();
		auth.ResultCode = "1";
		auth.MAC_Address = "";
		return auth;
	}

	/**
	 * 所有的返回ResultAuthenticationInfo的都没有设置authenticationInfo.Interactive_Log
	 * -1-其他错误 0-没收到discover请求； 1-提供了offer，没收到后续的select请求 2-用户名错误； 3-密码错误；
	 * 4-DHCP故障； 401-地址池已满 402-DHCP服务器暂停服务 403-没有合适的地址池
	 * 404-机顶盒发送discover过于频繁，DHCP服务器正在处理其上一个请求 5-AAA故障； 6-帐号暂停
	 * 
	 * @param discover
	 * @return
	 */
	protected ResultAuthenticationInfo deniedAuthenticationInfo(EtlLog log) {
		ResultAuthenticationInfo authenticationInfo = new ResultAuthenticationInfo();
		// setErrorAuthTimeAndRequestTimes(authenticationInfo, log);
		authenticationInfo.Auth_Result = "1"; // 所有的result返回为1，表示错误
		boolean other = true;
		Enumeration<?> e = errorMap.keys();
		while (e.hasMoreElements()) {
			String object = (String) e.nextElement();
			if (log.getAaaDenied().startsWith(errorMap.getProperty(object))) {
				authenticationInfo.Err_Code = object;
				other = false;
				break;
			}
		}
		if (other)
			authenticationInfo.Err_Code = "-1";
		authenticationInfo.IP_Address = yip(log.getYip());
		authenticationInfo.Interactive_Log.add(packInteractiveLog(log));
		return authenticationInfo;
	}

	/**
	 * status > offer , ack > rebind
	 * 
	 * @param log
	 * @param log2
	 * @return
	 */
	protected ResultAuthenticationInfo successAuthenticationInfo(EtlLog discover, EtlLog select) {
		ResultAuthenticationInfo authenticationInfo = new ResultAuthenticationInfo();
		authenticationInfo.Auth_Result = "0"; // 所有的result返回为1，表示错误
		authenticationInfo.Auth_Begin_Time = discover.getTime();
		authenticationInfo.Auth_End_Time = select != null ? select.getTime() : discover.getTime();
		authenticationInfo.RequestTimes = "1";
		authenticationInfo.Err_Code = "";
		authenticationInfo.IP_Address = yip(discover.getYip());
		if (discover != null)
			authenticationInfo.Interactive_Log.add(packInteractiveLog(discover));
		if (select != null)
			authenticationInfo.Interactive_Log.add(packInteractiveLog(select));
		return authenticationInfo;
	}

	protected ResultAuthenticationInfo nullSelectAuthenticationInfo(EtlLog log) {
		ResultAuthenticationInfo authenticationInfo = new ResultAuthenticationInfo();
		authenticationInfo.Auth_Result = "0"; // 所有的result返回为1，表示错误
		// setErrorAuthTimeAndRequestTimes(authenticationInfo, log);
		authenticationInfo.Err_Code = "1";
		authenticationInfo.IP_Address = yip(log.getYip());
		authenticationInfo.Interactive_Log.add(packInteractiveLog(log));
		return authenticationInfo;
	}

	protected ResultInteractiveLog packInteractiveLog(EtlLog log) {
		ResultInteractiveLog interactiveLog = new ResultInteractiveLog();
		if (log.getRequestType() != null && log.getRequestType().equals("rebind")) {
			interactiveLog.type = "rebind"; // rebind
		} else {
			interactiveLog.type = log.getMegType(); // 信息类型，这里只有discover和request
		}
		interactiveLog.date = log.getTime(); // 交互开始时间
		interactiveLog.macaddr = log.getMacAddr(); // MAC地址
		interactiveLog.network = log.getAccessNet(); // 用户所属网络
		interactiveLog.gateway = rip(log.getRaip()); // 网管
		interactiveLog.linktype = log.getLinkType(); // 连接类型
		interactiveLog.option82 = log.getNasInfo(); // OPTION82
		interactiveLog.stbtype = log.getStbType(); // 终端业务属性
		interactiveLog.stbname = log.getStbOption60(); // OPTION60内容
		interactiveLog.stbresult = log.getStbRule(); // 属性规则
		interactiveLog.AAAresult = log.getAaaResult(); // AAA结果
		interactiveLog.AAAerror = aaaerror(log.getAaaDenied()); // AAA错误原因
		interactiveLog.dhcpsent = log.getDhcpSent(); // 服务器应答,discover应该对应offer，request应该对应ack
		interactiveLog.errorinfo = log.getDhcpDenied(); // 服务器记录的错误信息
		interactiveLog.warninginfo = log.getDhcpWarning(); // 服务器记录的警告
		interactiveLog.leasestatus = log.getLeaseStatus(); // 租约状态
		interactiveLog.userip = yip(log.getYip()); // 用户IP
		interactiveLog.username = log.getUserid(); // 用户名
		return interactiveLog;
	}
	
	private String yip(String yip) {
		if(yip == null || yip.equals("-")) {
			return "";
		}
		return yip;
			
	} 
	
	private String rip(String rip) {
		if(rip == null) {
			return "";
		}
		return rip.split(" ")[0];
			
	} 
	
	private String aaaerror(String aaaerror) {
		if(aaaerror == null)
			return "";
		return aaaerror.replace("']", "");
	}

}
