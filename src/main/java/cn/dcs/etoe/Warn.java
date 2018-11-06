package cn.dcs.etoe;

/**
 * @author wanghaiinfo
 * @version 1.0, Mar 30, 2012
 */
public class Warn {

    private String AlarmId;

    private String FaultStartTime;

    private Integer FaultObjectType;

    private String FaultObject;

    private String FaultObjectName;

    private String FaultValue;

    private String FaultThreshold;

    private Integer FaultType;

    private Integer FaultLevel;

    private String FaultDescribe;

    private String FaultClearTime;

    public String getAlarmId() {
        return AlarmId;
    }

    public void setAlarmId(String alarmId) {
        AlarmId = alarmId;
    }

    public String getFaultStartTime() {
        return FaultStartTime;
    }

    public void setFaultStartTime(String faultStartTime) {
        FaultStartTime = faultStartTime;
    }

    public Integer getFaultObjectType() {
        return FaultObjectType;
    }

    public void setFaultObjectType(Integer faultObjectType) {
        FaultObjectType = faultObjectType;
    }

    public String getFaultObject() {
        return FaultObject;
    }

    public void setFaultObject(String faultObject) {
        FaultObject = faultObject;
    }

    public String getFaultObjectName() {
        return FaultObjectName;
    }

    public void setFaultObjectName(String faultObjectName) {
        FaultObjectName = faultObjectName;
    }

    public String getFaultValue() {
        return FaultValue;
    }

    public void setFaultValue(String faultValue) {
        FaultValue = faultValue;
    }

    public String getFaultThreshold() {
        return FaultThreshold;
    }

    public void setFaultThreshold(String faultThreshold) {
        FaultThreshold = faultThreshold;
    }

    public Integer getFaultType() {
        return FaultType;
    }

    public void setFaultType(Integer faultType) {
        FaultType = faultType;
    }

    public Integer getFaultLevel() {
        return FaultLevel;
    }

    public void setFaultLevel(Integer faultLevel) {
        FaultLevel = faultLevel;
    }

    public String getFaultDescribe() {
        return FaultDescribe;
    }

    public void setFaultDescribe(String faultDescribe) {
        FaultDescribe = faultDescribe;
    }

    public String getFaultClearTime() {
        return FaultClearTime;
    }

    public void setFaultClearTime(String faultClearTime) {
        FaultClearTime = faultClearTime;
    }

}
