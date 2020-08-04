package its.traffic.flow.entity;

public class RoadEntity {

    private int roadId;
    private int stzId;
    private int etzId;
    private String stzName;
    private String etzName;

    private String roadName;
    private String roadLocaltion;
    private String diffusionLocation;
    private int roadDescribe;


    private String oldStzName;
    private String oldEtzName;
    private String oldRoadName;

    private String type;
    private double length;
    private double roadLength;



    public String getOldStzName() {
        return oldStzName;
    }

    public void setOldStzName(String oldStzName) {
        this.oldStzName = oldStzName;
    }

    public String getOldEtzName() {
        return oldEtzName;
    }

    public void setOldEtzName(String oldEtzName) {
        this.oldEtzName = oldEtzName;
    }

    public String getOldRoadName() {
        return oldRoadName;
    }

    public void setOldRoadName(String oldRoadName) {
        this.oldRoadName = oldRoadName;
    }

    public String getStzName() {
        return stzName;
    }

    public void setStzName(String stzName) {
        this.stzName = stzName;
    }

    public String getEtzName() {
        return etzName;
    }

    public void setEtzName(String etzName) {
        this.etzName = etzName;
    }

    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }

    public int getStzId() {
        return stzId;
    }

    public void setStzId(int stzId) {
        this.stzId = stzId;
    }

    public int getEtzId() {
        return etzId;
    }

    public void setEtzId(int etzId) {
        this.etzId = etzId;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getRoadLocaltion() {
        return roadLocaltion;
    }

    public void setRoadLocaltion(String roadLocaltion) {
        this.roadLocaltion = roadLocaltion;
    }

    public String getDiffusionLocation() {
        return diffusionLocation;
    }

    public void setDiffusionLocation(String diffusionLocation) {
        this.diffusionLocation = diffusionLocation;
    }

    public int getRoadDescribe() {
        return roadDescribe;
    }

    public void setRoadDescribe(int roadDescribe) {
        this.roadDescribe = roadDescribe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getRoadLength() {
        return roadLength;
    }

    public void setRoadLength(double roadLength) {
        this.roadLength = roadLength;
    }

    @Override
    public String toString() {
        return "RoadEntity{" +
                "roadId=" + roadId +
                ", stzId=" + stzId +
                ", etzId=" + etzId +
                ", stzName='" + stzName + '\'' +
                ", etzName='" + etzName + '\'' +
                ", roadName='" + roadName + '\'' +
                ", roadLocaltion='" + roadLocaltion + '\'' +
                ", diffusionLocation='" + diffusionLocation + '\'' +
                ", roadDescribe='" + roadDescribe + '\'' +
                ", oldStzName='" + oldStzName + '\'' +
                ", oldEtzName='" + oldEtzName + '\'' +
                ", oldRoadName='" + oldRoadName + '\'' +
                '}';
    }
}
