package its.traffic.flow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RoadTraffic
 *
 * @author Lenovo
 * @version 1.0
 * 2020/7/30 15:49
 **/
@Component
public class RoadTraffic {

    @Value("${road.traffic.1}")
    private int artery_1;

    @Value("${road.traffic.2}")
    private int artery_2;

    @Value("${road.traffic.3}")
    private int artery_3;



    public int getArtery(int type){
        if(type == 1){
            return artery_1;
        }else if (type == 2){
            return artery_2;
        }else if (type == 3){
            return artery_3;
        }
        return 0;
    }

    public int getArtery_1() {
        return artery_1;
    }

    public void setArtery_1(int artery_1) {
        this.artery_1 = artery_1;
    }

    public int getArtery_2() {
        return artery_2;
    }

    public void setArtery_2(int artery_2) {
        this.artery_2 = artery_2;
    }

    public int getArtery_3() {
        return artery_3;
    }

    public void setArtery_3(int artery_3) {
        this.artery_3 = artery_3;
    }
}
