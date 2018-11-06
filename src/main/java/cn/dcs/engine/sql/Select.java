package cn.dcs.engine.sql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.dcs.Log;
import cn.dcs.engine.CallerAttribute;
import cn.dcs.engine.EtlLog;
import cn.dcs.engine.Session;
import cn.dcs.util.GroupServer;
import cn.dcs.util.QueryUtil;

public class Select {

	private static final String USERID = "`userid`";

	private static final String MACADDR = "`mac_addr`";

	protected static final int PER_SIZE = 200; // 最多查询200条

	private String key;

	private String keyValue;

	private List<String> exsitsTables;

	private Session session;

	private CallerAttribute attribute;

	private Date sdate;

	private Date edate;

	private String sdate_;

	private String edate_;

	private SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private SimpleDateFormat s2 = new SimpleDateFormat("yyyyMMddHHmmss");

	private Date getDate(String date) throws ParseException {
		return s1.parse(date);
	}

	private String date19To14(Date date) {
		return s2.format(date);
	}

	public Select(Session session) throws ParseException {
		this.session = session;
		this.attribute = session.getAttribute();
		if (attribute.queryFlag == 0) {
			Calendar c = Calendar.getInstance();
			this.edate = c.getTime();
			c.add(Calendar.DATE, -4);
			this.sdate = c.getTime();
			this.sdate_ = date19To14(sdate);
			this.edate_ = date19To14(edate);
		} else {
			this.sdate = getDate(attribute.queryBeginTime);
			this.edate = getDate(attribute.queryEndTime);
			this.sdate_ = date19To14(sdate);
			this.edate_ = date19To14(edate);
		}
		doExsitsTableFilter(attribute);
		keyValue = attribute.queryValue;
		if (attribute.queryNumber == 0) {
			key = USERID; // 如果是[0]查询接入号
		} else
			key = MACADDR; // 如果是[1]查询MAC地址
	}

	private void doExsitsTableFilter(CallerAttribute attribute) {
		String s = sdate_.substring(0, 8);
		String e = edate_.substring(0, 8);
		String p_ = null;
		String s_ = null;
		exsitsTables = new ArrayList<String>();
		for (int i = Integer.parseInt(s); i <= Integer.parseInt(e); i++) {
			if(i % 100 > 31)
				return;
			p_ = QueryUtil.getTablename(GroupServer.getPRI(), i + "");
			s_ = QueryUtil.getTablename(GroupServer.getSEC(), i + "");
			if (session.check(p_))
				exsitsTables.add(p_);
			if (session.check(s_))
				exsitsTables.add(s_);
		}
		if (exsitsTables.size() == 0) {
			throw new RuntimeException("null table");
		}
		if(Log.getLogger().isInfoEnabled()) {
			Log.getLogger().info(exsitsTables + "\r\n" + sdate_ + "/" + edate_);
		}
	}

	/**
	 * 合并所有可用的表，包括主用和备用表，包括所有按时间分的表
	 * 
	 * @param command
	 * @return
	 */
	private String selectUnionInExsitsTable(String command) {
		StringBuilder s = new StringBuilder();
		int i = 0;
		int len = exsitsTables.size();
		for (String table : exsitsTables) {
			i++;
			s.append("select * from ").append(table).append(" where ")
					.append(command);
			if (i != len)
				s.append(" union all ");
		}
		return s.toString();
	}

	private String necessaryCommand() {
		return "`time` >=" + sdate_ + " and `time` <=" + edate_ + " and " + key
				+ "='" + keyValue;
	}

	private String necessaryCommand(String[] discoverDeepFindTime) {
		return "`time` >=" + discoverDeepFindTime[0] + " and `time` <="
				+ discoverDeepFindTime[1] + " and " + key + "='" + keyValue;
	}

	private String[] discoverDeepFindTime(EtlLog discover) {
		String[] backTime = new String[2];
		try {
			Date time = s2.parse(discover.getTime());
			backTime[0] = discover.getTime();
			Calendar c = Calendar.getInstance();
			c.setTime(time);
			c.add(Calendar.SECOND, 60);
			backTime[1] = s2.format(c.getTime());
		} catch (ParseException e) {
			throw new RuntimeException("discover deep find select time error.");
		}
		return backTime;
	}

	/**
	 * 以 load balancing 结尾的 dhcp_sent表示双机的另外一台服务器正在处理，而不是真正的报错。可以忽略
	 * 
	 * @return
	 */
	public SelectPreparedStatement lastDiscoverStatement() {
		String command = necessaryCommand()
				+ "' and (`meg_type`='discover' or (`meg_type`='request' and `request_type`='rebind')) and (`dhcp_denied` is null or `dhcp_denied` not like '%load balancing')";
		StringBuilder s = new StringBuilder("select * from (");
		s.append(selectUnionInExsitsTable(command));
		s.append(") t order by `time` desc limit 1");
		return new SelectPreparedStatement(s.toString());
	}

	public SelectPreparedStatement lastSelectStatement(EtlLog discover) {
		String command = necessaryCommand(discoverDeepFindTime(discover))
				+ "' and `meg_type`='request' and `request_type`='select' and dhcp_sent='ack'";
		StringBuilder s = new StringBuilder("select * from (");
		s.append(selectUnionInExsitsTable(command));
		s.append(") t order by `time` desc limit 1");
		return new SelectPreparedStatement(s.toString());
	}

	public SelectPreparedStatement dayDiscoverStatement() {
		String command = necessaryCommand()
				+ "' and (`meg_type`='discover' or (`meg_type`='request' and `request_type`='rebind')) and (`dhcp_denied` is null or `dhcp_denied` not like '%load balancing')";
		StringBuilder s = new StringBuilder("select * from (");
		s.append(selectUnionInExsitsTable(command));
		s.append(") t order by `time` desc limit " + PER_SIZE);
		return new SelectPreparedStatement(s.toString());
	}

	public SelectPreparedStatement daySelectStatement(EtlLog discover) {
		String command = necessaryCommand(discoverDeepFindTime(discover))
				+ "' and `meg_type`='request' and `request_type`='select' and dhcp_sent='ack'";
		StringBuilder s = new StringBuilder("select * from (");
		s.append(selectUnionInExsitsTable(command));
		s.append(") t order by `time` desc limit 1");
		return new SelectPreparedStatement(s.toString());
	}

}
