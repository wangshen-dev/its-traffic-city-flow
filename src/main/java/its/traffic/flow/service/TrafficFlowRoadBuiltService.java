package its.traffic.flow.service;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.RoadEntity;

public interface TrafficFlowRoadBuiltService {

    /**
     * 新增道路配置
     * @param roadEntity
     * @return
     */
    RespMessage createRoadDetails(RoadEntity roadEntity);

    /**
     * 删除道路配置
     * @param id
     * @return
     */
    RespMessage deleteRoadDetails(Integer id);

    /**
     * 修改道路配置
     * @param roadEntity
     * @return
     */
    RespMessage updateRoadDetails(RoadEntity roadEntity,Integer id);

    /**
     * 查看道路配置
     * @param roadEntity
     * @return
     */
    RespMessage seleteOneRoadDetails(RoadEntity roadEntity,Integer id);

    /**
     * 道路信息列表
     * @return
     */
    RespMessage getRoadList();

    /**
     * 查看道路详情
     * @param roadEntity
     * @return
     */
    RespMessage seleteRoadDetails(RoadEntity roadEntity);

}
