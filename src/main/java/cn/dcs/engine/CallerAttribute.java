package cn.dcs.engine;

/**
 * 参数属性
 * @author Administrator
 */
public class CallerAttribute {

    private String nativaQueryNumber;

    public int queryNumber;

    public String queryBeginTime;
    
    public String queryEndTime;

    public int queryFlag;

    public String queryValue;

    CallerAttribute(String queryNumber, String queryBeginTime, String queryEndTime, int queryFlag) {
        this.nativaQueryNumber = queryNumber;
        this.queryBeginTime = queryBeginTime;
        this.queryEndTime = queryEndTime;
        this.queryFlag = queryFlag;
    }

    public void validation() {
        String str = nativaQueryNumber;
        String[] strs = str.split("]");
        if (strs.length != 2) throw new RuntimeException("100");
        int length = strs[0].length();
        try {
            this.queryNumber = Integer.parseInt(strs[0].substring(length - 1, length));
        } catch (Exception e) {
            throw new RuntimeException("102");
        }
        this.queryValue = strs[1];
        if (!(this.queryFlag == 0 || this.queryFlag == 1)) throw new RuntimeException("101");
        if (!(this.queryNumber == 0 || this.queryNumber == 1)) throw new RuntimeException("102");
    }

}
