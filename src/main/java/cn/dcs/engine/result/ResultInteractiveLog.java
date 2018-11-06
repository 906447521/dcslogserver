package cn.dcs.engine.result;

import cn.dcs.util.QueryUtil;

public class ResultInteractiveLog {

    // String interactive;
    //
    // String user;

    public String type;

    public String date;

    public String macaddr;

    public String network;

    public String gateway;

    public String linktype;

    public String option82;

    public String stbtype;

    public String stbname;

    public String stbresult;

    public String AAAresult;

    public String AAAerror;

    public String dhcpsent;

    public String errorinfo;

    public String warninginfo;

    public String leasestatus;

    public String userip;

    public String username;

    // public String getInteractive() {
    // return interactive;
    // }
    //
    // public String getUser() {
    // return user;
    // }

    public String getType() {
        return type;
    }

    public String getDate() {
        return QueryUtil.stringtime14To19(date);
    }

    public String getMacaddr() {
        return macaddr;
    }

    public String getNetwork() {
        return network;
    }

    public String getGateway() {
        return gateway;
    }

    public String getLinktype() {
        return linktype;
    }

    public String getOption82() {
        return option82;
    }

    public String getStbtype() {
        return stbtype;
    }

    public String getStbname() {
        return stbname;
    }

    public String getStbresult() {
        return stbresult;
    }

    public String getAAAresult() {
        return AAAresult;
    }

    public String getAAAerror() {
        return AAAerror;
    }

    public String getDhcpsent() {
        return dhcpsent;
    }

    public String getErrorinfo() {
        return errorinfo;
    }

    public String getWarninginfo() {
        return warninginfo;
    }

    public String getLeasestatus() {
        return leasestatus;
    }

    public String getUserip() {
        return userip;
    }

    public String getUsername() {
        return username;
    }

}
