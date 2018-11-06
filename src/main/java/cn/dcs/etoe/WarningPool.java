package cn.dcs.etoe;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author wanghaiinfo
 * @version 1.0, Mar 29, 2012
 */
public class WarningPool {

	private static final Logger logger = Logger.getLogger(WarningPool.class);

	// private static final String AVAILABLE_PERCENT_MIN_2 =
	// "pool.available.percent.min.2";
	// private static final String AVAILABLE_PERCENT_MIN_3 =
	// "pool.available.percent.min.3";
	private static final String AVAILABLE_PERCENT_MAX_2 = "pool.available.percent.max.2";

	private static final String AVAILABLE_PERCENT_MAX_3 = "pool.available.percent.max.3";

	private static final int LEVEL_NORMAL = 2;

	private static final int LEVEL_SERIOUS = 3;
	
	private static final int TYPE_NEWWARN = 0;
	
	private static final int TYPE_CANCELWARN = 1;

	private static final int FaultObjectType = 1;

	private Long id;

	private Long time;

	private String name;

	private Integer abandoned;

	private Integer available;

	private Integer active;

	private Double rate = 0d;

	private static String s18(String s14) {

		return s14.substring(0, 8) + " " + s14.substring(8, 10) + ":" + s14.substring(10, 12) + ":"
				+ s14.substring(12, 14);

	}

	public void rowAfter(ResultSet rs, Configuration conf, SqliteHelper sqlite, String ip) throws SQLException {

		id = rs.getLong("id");
		time = rs.getLong("time");
		name = rs.getString("name");
		abandoned = rs.getInt("dhcp-leases-dynamic-abandoned");
		available = rs.getInt("dhcp-leases-dynamic-available");
		active = rs.getInt("dhcp-leases-dynamic-active");

		if (abandoned + available == 0)
			return;

		rate = active.doubleValue() / (active + available + abandoned) * 100;

		if (logger.isDebugEnabled()) {

			logger.debug("pool start . id : " + id + " rate : " + rate + ", active : " + active + ", available : "
					+ available + ", abandoned : " + abandoned);

		}

		// int Threshold_min_2 = Integer.parseInt(conf
		// .get(AVAILABLE_PERCENT_MIN_2));
		// int Threshold_min_3 = Integer.parseInt(conf
		// .get(AVAILABLE_PERCENT_MIN_3));
		int Threshold_max_2 = Integer.parseInt(conf.get(AVAILABLE_PERCENT_MAX_2));
		int Threshold_max_3 = Integer.parseInt(conf.get(AVAILABLE_PERCENT_MAX_3));

		boolean is_warn = false;
		String p = "";
		int level = LEVEL_NORMAL;
		int Threshold = 0;
		// is warn
		// if (rate < Threshold_min_3) {
		// p = "低于";
		// level = LEVEL_SERIOUS;
		// Threshold = Threshold_min_3;
		// is_warn = true;
		// }
		//
		// else if (rate < Threshold_min_2) {
		// p = "低于";
		// Threshold = Threshold_min_2;
		// is_warn = true;
		// }

		if (rate > Threshold_max_3) {
			p = "高于";
			level = LEVEL_SERIOUS;
			Threshold = Threshold_max_3;
			is_warn = true;
		}

		else if (rate > Threshold_max_2) {
			p = "高于";
			Threshold = Threshold_max_2;
			is_warn = true;
		}

		Warn warn = sqlite.getKeyPair(FaultObjectType, name);

		if (is_warn) {

			Statistics.warn();

			if (logger.isDebugEnabled()) {

				logger.debug("is warn! if warn not in sqlite ,insert and send webservice, if warn in sqlite do nothing.");

			}

			if (warn == null) {

				Warn insert = new Warn();
				insert.setAlarmId(SqliteHelper.automic() + "");
				insert.setFaultStartTime(s18(time + ""));
				insert.setFaultObjectType(FaultObjectType);
				insert.setFaultObject(ip);
				insert.setFaultObjectName(name);
				insert.setFaultValue("地址池使用率" + ((double) Math.round(rate * 100) / 100) + "%");
				insert.setFaultThreshold("地址池使用率阀值" + Threshold + "%");
				insert.setFaultType(TYPE_NEWWARN);
				insert.setFaultLevel(level);
				insert.setFaultDescribe("当前地址池使用率" + p + "阀值");
				insert.setFaultClearTime("");

				if (WebserviceSender.send(insert) != null) {

					sqlite.insertKeyPair(insert);

					Statistics.push();

				}

				if (logger.isDebugEnabled()) {

					logger.debug("sqlite insert pool " + "rate :" + rate + ",threshold:" + Threshold + ",p:" + p);

				}

			}

		} else {

			if (logger.isDebugEnabled()) {

				logger.debug("not warn! if warn in sqlite, remove and send sqlite ,if warn not in sqlite do nothing.");

			}

			if (warn != null) {

				warn.setFaultType(TYPE_CANCELWARN);

				warn.setFaultClearTime(s18(time + ""));

				if (WebserviceSender.send(warn) != null) {

					sqlite.removeKeyPair(FaultObjectType, name);

					Statistics.remove();

				}

				if (logger.isDebugEnabled()) {

					logger.debug("sqlite remove pool , rate:" + rate);

				}

			}

		}

	}

	public static void main(String[] args) {

		System.out.println((double) Math.round(222.207868567 * 100) / 100);
	}
}
