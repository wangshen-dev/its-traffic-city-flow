package its.traffic.flow.mapper;

import its.traffic.flow.entity.FlowData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * TrafficGetFlowDataMapper
 *
 * @author Lenovo
 * @version 1.0
 * 2020/7/31 9:16
 **/
@Mapper
public interface TrafficGetFlowDataMapper {


    List<FlowData> getFlowData(Map<String,Object> map);
}
