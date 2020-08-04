package its.traffic.flow.controller;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.RoadEntity;
import its.traffic.flow.service.TrafficFlowRoadBuiltService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TrafficFlowBuiltController
 * 路网构建模块
 * @author wangshen
 * @version 1.0
 * 2020/7/17 09:58
 **/
@Controller
@RequestMapping("/traffic/flow/built/road")
public class TrafficFlowRoadBuiltController {

    @Autowired
    TrafficFlowRoadBuiltService trafficFlowRoadBuiltService;

    /**
     * 新增道路配置
     */
    @ResponseBody
    @RequestMapping("/createRoadDetails")
    public RespMessage createRoadDetails(RoadEntity roadEntity){
        return trafficFlowRoadBuiltService.createRoadDetails(roadEntity);
    }

    /**
     * 删除道路配置
     */
    @ResponseBody
    @RequestMapping("/deleteRoadDetails")
    public RespMessage deleteRoadDetails(Integer id){
        return trafficFlowRoadBuiltService.deleteRoadDetails(id);
    }

    /**
     * 修改道路配置
     */
    @ResponseBody
    @RequestMapping("/updateRoadDetails")
    public RespMessage updateRoadDetails(RoadEntity roadEntity,Integer id){
        return trafficFlowRoadBuiltService.updateRoadDetails(roadEntity,id);
    }

    /**
     * 查看单条道路配置信息
     */
    @ResponseBody
    @RequestMapping("/seleteOneRoadDetails")
    public RespMessage seleteOneRoadDetails(RoadEntity roadEntity,Integer id){
        return trafficFlowRoadBuiltService.seleteOneRoadDetails(roadEntity,id);
    }

    /**
     * 道路信息列表
     */
    @ResponseBody
    @RequestMapping("/getRoadList")
    public RespMessage getRoadList(){
        return trafficFlowRoadBuiltService.getRoadList();
    }


    /**
     * 查看道路详细信息
     */
    @ResponseBody
    @RequestMapping("/selectRoadDetails")
    public RespMessage selectRoadDetails(RoadEntity roadEntity){
        return trafficFlowRoadBuiltService.seleteRoadDetails(roadEntity);
    }

}
