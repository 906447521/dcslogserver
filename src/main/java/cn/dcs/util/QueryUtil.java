package cn.dcs.util;

public class QueryUtil {

	public static String getTablename(String ip, String day) {
		return "dcsdbetl.log_" + ip.replace(".", "_") + "_" + day;
	}

	public static String stringtime14To19(String time) {
		return time.substring(0, 4) + "-" + time.substring(4, 6) + "-"
				+ time.substring(6, 8) + " " + time.substring(8, 10) + ":"
				+ time.substring(10, 12) + ":" + time.substring(12, 14);
	}

	public static String nullToStr(String str) {
		return str == null ? "" : str;
	}

	public static String nullToStr(Integer arg) {
		return arg == null ? "" : String.valueOf(arg);
	}

	public static String nullToStr(Long arg) {
		return arg == null ? "" : String.valueOf(arg);
	}

}
