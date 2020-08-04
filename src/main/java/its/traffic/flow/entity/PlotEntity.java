package its.traffic.flow.entity;


public class PlotEntity {

    private int otId;
    private int tzId;
    private String otName;
    private String tzName;
    private int tzFlag = -1;
    private String tzCenterCoordinates;
    private String tzScope;
    private int tzAudit = 1;

    private String old_tzName;
    private String old_otName;

    public String getOld_tzName() {
        return old_tzName;
    }

    public void setOld_tzName(String old_tzName) {
        this.old_tzName = old_tzName;
    }

    public int getTzId() {
        return tzId;
    }

    public void setTzId(int tzId) {
        this.tzId = tzId;
    }

    public String getOtName() {
        return otName;
    }

    public void setOtName(String otName) {
        this.otName = otName;
    }

    public int getOtId() {
        return otId;
    }

    public void setOtId(int otId) {
        this.otId = otId;
    }

    public String getTzName() {
        return tzName;
    }

    public void setTzName(String tzName) {
        this.tzName = tzName;
    }

    public int getTzFlag() {
        return tzFlag;
    }

    public void setTzFlag(int tzFlag) {
        this.tzFlag = tzFlag;
    }

    public String getTzCenterCoordinates() {
        return tzCenterCoordinates;
    }

    public void setTzCenterCoordinates(String tzCenterCoordinates) {
        this.tzCenterCoordinates = tzCenterCoordinates;
    }

    public String getTzScope() {
        return tzScope;
    }

    public void setTzScope(String tzScope) {
        this.tzScope = tzScope;
    }

    public int getTzAudit() {
        return tzAudit;
    }

    public void setTzAudit(int tzAudit) {
        this.tzAudit = tzAudit;
    }
}
