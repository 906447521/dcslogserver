package cn.dcs.engine;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.dcs.util.QueryUtil;

/**
 * 根据Rs封装成EtlLog对象 如果字段为 null 返回 空字符串
 * @author Administrator
 */
public class SessionRow {

    public EtlLog row(ResultSet rs) throws SQLException {
        EtlLog log = new EtlLog();
        log.setId(QueryUtil.nullToStr(rs.getInt("id")));
        log.setTime(QueryUtil.nullToStr(rs.getLong("time")));
        log.setTid(QueryUtil.nullToStr(rs.getInt("tid")));
        log.setMegType(QueryUtil.nullToStr(rs.getString("meg_type")));
        log.setRequestType(QueryUtil.nullToStr(rs.getString("request_type")));
        log.setMacAddr(QueryUtil.nullToStr(rs.getString("mac_addr")));
        log.setAccessNet(QueryUtil.nullToStr(rs.getString("access_net")));
        log.setLinkType(QueryUtil.nullToStr(rs.getString("link_type")));
        log.setNasInfo(QueryUtil.nullToStr(rs.getString("nas_info")));
        log.setStbType(QueryUtil.nullToStr(rs.getString("stb_type")));
        log.setStbOption60(QueryUtil.nullToStr(rs.getString("stb_option60")));
        log.setStbRule(QueryUtil.nullToStr(rs.getString("stb_rule")));
        log.setUserinfo(QueryUtil.nullToStr(rs.getString("userinfo")));
        log.setAaaResult(QueryUtil.nullToStr(rs.getString("aaa_result")));
        log.setAaaDenied(QueryUtil.nullToStr(rs.getString("aaa_denied")));
        log.setDhcpSent(QueryUtil.nullToStr(rs.getString("dhcp_sent")));
        log.setYip(QueryUtil.nullToStr(rs.getString("yip")));
        log.setRaip(QueryUtil.nullToStr(rs.getString("raip")));
        log.setDhcpDenied(QueryUtil.nullToStr(rs.getString("dhcp_denied")));
        log.setLeaseStatus(QueryUtil.nullToStr(rs.getString("lease_status")));
        log.setNextAction(QueryUtil.nullToStr(rs.getString("next_action")));
        log.setNextTid(QueryUtil.nullToStr(rs.getInt("next_tid")));
        log.setDhcpWarning(QueryUtil.nullToStr(rs.getString("dhcp_warning")));
        log.setUserid(QueryUtil.nullToStr(rs.getString("userid")));
        return log;
    }

}
