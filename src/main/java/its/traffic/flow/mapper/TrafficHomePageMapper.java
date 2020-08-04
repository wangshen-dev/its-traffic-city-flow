package its.traffic.flow.mapper;

import its.traffic.flow.entity.FlowData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * TrafficHomePageMapper
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/28 16:17
 **/
@Mapper
public interface TrafficHomePageMapper {
    /**
     * OD类型
     * @return
     */
    List<Map<String,Object>> queryODType();

    /**
     * 返回交通小区
     * @return
     */
    List<Map<String,Object>> isOtNameGetPlots(String otName);
    /**
     * 获取小区下的车牌id
     * @param map
     * @return
     */
    List<String> getCarId(Map<String,Object> map);

    /**
     * 根据车牌id 获取数据
     * @param map
     * @return
     */
    List<FlowData> getFlowData(Map<String,Object> map);

    /**
     * 根据地点id 获取数据
     * @param map
     * @return
     */
    List<FlowData> getFlowData_plot(Map<String,Object> map);
}
