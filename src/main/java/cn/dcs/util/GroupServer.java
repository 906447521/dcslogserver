package cn.dcs.util;

public class GroupServer {

    public static String getPRI() {
        return GroupServerHashtable.instence().get("pri");
    }

    public static String getSEC() {
        return GroupServerHashtable.instence().get("sec");
    }

}
