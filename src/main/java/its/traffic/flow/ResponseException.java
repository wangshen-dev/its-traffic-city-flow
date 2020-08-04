package its.traffic.flow;

/**
 * @ClassName NotLegitimateInfoException
 * @Description TODO
 * @Author yangchongshun
 * @Date 2019/9/18 11:35
 * @Version v1.0
 */
public class ResponseException extends Exception {
    public ResponseException() {
        super();
    }
    public ResponseException(String message) {
        super(message);
    }
    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }
    public ResponseException(Throwable cause) {
        super(cause);
    }
}
