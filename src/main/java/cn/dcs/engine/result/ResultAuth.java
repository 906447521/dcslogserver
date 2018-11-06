package cn.dcs.engine.result;

import java.util.ArrayList;
import java.util.List;

public class ResultAuth {

	public String MAC_Address;

	public String ResultCode;

	public String RecordNum = "0";

	public List<ResultAuthenticationInfo> Authentication_Info = new ArrayList<ResultAuthenticationInfo>();

	public String getRecordNum() {
		return RecordNum;
	}

	public String getMAC_Address() {
		return MAC_Address;
	}

	public String getResultCode() {
		return ResultCode;
	}

	public List<ResultAuthenticationInfo> getAuthentication_Info() {
		return Authentication_Info;
	}

}
