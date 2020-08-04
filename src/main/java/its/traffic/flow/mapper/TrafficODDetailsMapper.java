package its.traffic.flow.mapper;


import its.traffic.flow.entity.FlowData;
import its.traffic.flow.entity.ODTypeEntity;
import its.traffic.flow.entity.PlotEntity;
import its.traffic.flow.entity.RoadEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TrafficODDetailsMapper {

    /**
     * 获取交通小区列表详情
     * @return
     */
    List<PlotEntity> getPlotsDetails();

    /**
     * 获取OD类型数据
     * @return
     */
    List<ODTypeEntity> getODtypeList();

    /**
     * 获取道路信息
     * @return
     */
    List<RoadEntity> getRoadsList(Map<String,Object> map);
    /**
     * 获取流量数据
     * @param map
     * @return
     */
    List<FlowData> getFlowData(Map<String,Object> map);

}
