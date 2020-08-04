package its.traffic.flow.entity;

public class ChangeEntity {

    private int cId;
    private int userId;
    private int cType;
    private int cDataId;
    private String cDes;
    private String cDateTime;
    private String cContent;


    public String getcContent() {
        return cContent;
    }

    public void setcContent(String cContent) {
        this.cContent = cContent;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getcType() {
        return cType;
    }

    public void setcType(int cType) {
        this.cType = cType;
    }

    public int getcDataId() {
        return cDataId;
    }

    public void setcDataId(int cDataId) {
        this.cDataId = cDataId;
    }

    public String getcDes() {
        return cDes;
    }

    public void setcDes(String cDes) {
        this.cDes = cDes;
    }

    public String getcDateTime() {
        return cDateTime;
    }

    public void setcDateTime(String cDateTime) {
        this.cDateTime = cDateTime;
    }
}
