package its.traffic.flow.entity;

/**
 * ODFlowParam
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/28 17:00
 **/
public class ODFlowParam {

    private String zone;
    private String startZone;
    private String endZone;
    private String date;
    private String startDate;
    private String endDate;

    private int odType;
    private String otName;
    private String regionName;

    private String tzName;

    private int size = 0;

    public String getTzName() {
        return tzName;
    }

    public void setTzName(String tzName) {
        this.tzName = tzName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getStartZone() {
        return startZone;
    }

    public void setStartZone(String startZone) {
        this.startZone = startZone;
    }

    public String getEndZone() {
        return endZone;
    }

    public void setEndZone(String endZone) {
        this.endZone = endZone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getOdType() {
        return odType;
    }

    public void setOdType(int odType) {
        this.odType = odType;
    }

    public String getOtName() {
        return otName;
    }

    public void setOtName(String otName) {
        this.otName = otName;
    }

    @Override
    public String toString() {
        return "ODFlowParam{" +
                "zone='" + zone + '\'' +
                ", startZone='" + startZone + '\'' +
                ", endZone='" + endZone + '\'' +
                ", date='" + date + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", odType=" + odType +
                ", otName='" + otName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", tzName='" + tzName + '\'' +
                ", size=" + size +
                '}';
    }
}
