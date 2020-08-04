package its.traffic.flow.controller;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.ODFlowParam;
import its.traffic.flow.service.TrafficFlowODDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TrafficFlowHomePageController
 * 热点模块 起讫点详情
 * @author wangshen
 * @version 1.0
 * 2020/7/10 09:41
 **/
@Controller
@RequestMapping("/traffic/flow/hotPlot/od/")
public class TrafficFlowODDetailsController {

    @Autowired
    TrafficFlowODDetailsService trafficFlowODDetailsService;

    /**
     * 获取交通小区列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPlotsList")
    public RespMessage getPlots(){
        return trafficFlowODDetailsService.getPlotsList();
    }

    /**
     * 获取OD类型列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/getODTypeList")
    public RespMessage getODTypeList(){
        return trafficFlowODDetailsService.getODTypeList();
    }

    /**
     * 获取起点-讫点 道路信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRoadsList")
    public RespMessage getRoadsList(ODFlowParam odFlowParam){
        return trafficFlowODDetailsService.getRoadsList(odFlowParam);
    }


//    /**
//     * 通勤比
//     * @param odFlowParam
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping("/getCommutRate")
//    public RespMessage getCommutRate(ODFlowParam odFlowParam){
//
//        return null;
//    }


//    /**
//     * 各类型车辆占比
//     * @param odFlowParam
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping("/getCarTypeRate")
//    public RespMessage getCarTypeRate(ODFlowParam odFlowParam){
//
//        return null;
//    }

//    /**
//     * 出行量、速度、时间
//     * @param odFlowParam
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping("/getTraveFlowTime")
//    public RespMessage getTraveFlowTime(ODFlowParam odFlowParam){
//
//        return null;
//    }

    /**
     * 出行量每小时变化趋势 + 出行吸引基础数据列表
     * @param odFlowParam
     * @return
     */
    @ResponseBody
    @RequestMapping("/traveFlowTrend")
    @Cacheable("hotPlot-ODflowTrend")
    public RespMessage traveFlowTrend(ODFlowParam odFlowParam){

        return   trafficFlowODDetailsService.traveFlowODTrend(odFlowParam);
    }


    /**
     * 路径轨迹
     * @param odFlowParam
     * @return
     */
    @ResponseBody
    @RequestMapping("/pointTrack")
    @Cacheable("hotPlot-ODPointTrack")
    public RespMessage pointTrack(ODFlowParam odFlowParam){

        return  trafficFlowODDetailsService.pointTrack(odFlowParam);
    }

}
