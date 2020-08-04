package its.traffic.flow.entity;

public class ODTypeEntity {

    private int otId;
    private String otName;
    private int otFlag = -1;
    private int otAudit = 1;
    private String otDescribe;


    public int getOtId() {
        return otId;
    }

    public void setOtId(int otId) {
        this.otId = otId;
    }

    public String getOtName() {
        return otName;
    }

    public void setOtName(String otName) {
        this.otName = otName;
    }

    public int getOtFlag() {
        return otFlag;
    }

    public void setOtFlag(int otFlag) {
        this.otFlag = otFlag;
    }

    public int getOtAudit() {
        return otAudit;
    }

    public void setOtAudit(int otAudit) {
        this.otAudit = otAudit;
    }

    public String getOtDescribe() {
        return otDescribe;
    }

    public void setOtDescribe(String otDescribe) {
        this.otDescribe = otDescribe;
    }
}
