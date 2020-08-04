package its.traffic.flow.entity;

/**
 * Filter
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/4 10:17
 **/
public class Filter {

    private String sessionId;
    private String sessionTime;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionTime='" + sessionTime + '\'' +
                '}';
    }
}
