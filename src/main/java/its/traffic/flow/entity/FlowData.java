package its.traffic.flow.entity;

public class FlowData {

    private Integer addrId;
    private Integer carId;
    private String vsTime;

    public Integer getAddrId() {
        return addrId;
    }

    public void setAddrId(Integer addrId) {
        this.addrId = addrId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getVsTime() {
        return vsTime;
    }

    public void setVsTime(String vsTime) {
        this.vsTime = vsTime;
    }

    @Override
    public String toString() {
        return "FlowData{" +
                "addrId=" + addrId +
                ", carId=" + carId +
                ", vsTime='" + vsTime + '\'' +
                '}';
    }
}
