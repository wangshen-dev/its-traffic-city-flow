package its.traffic.flow.controller;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.ODFlowParam;
import its.traffic.flow.service.TrafficFlowHomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TrafficFlowHomePageController
 * 首页模块
 * @author wangshen
 * @version 1.0
 * 2020/6/28 15:39
 **/
@Controller
@RequestMapping("/traffic/flow/home")
public class TrafficFlowHomePageController {

    @Autowired
    TrafficFlowHomePageService trafficFlowHomePageService;


    /**
     * OD类型查询
     */
    @ResponseBody
    @RequestMapping("/queryODType")
    public RespMessage odTypeQuery(){
        return trafficFlowHomePageService.queryODType();
    }

    /**
     * 根据OD类型 返回小区
     */
    @ResponseBody
    @RequestMapping("/getPlotList")
    public RespMessage getPlotList(String otName){
        return trafficFlowHomePageService.getPlotList(otName);
    }


    /**
     * 出行OD量列表 + 出行产生变化趋势 + 总OD产生量
     */
    @ResponseBody
    @RequestMapping("/traveODFlow")
    @Cacheable("homePage-traveODFlow")
    public RespMessage traveODFlow(ODFlowParam odFlowParam){
       return trafficFlowHomePageService.getODTraveFlow(odFlowParam);
    }

    /**
     * OD迁徙数据查询
     */
    @ResponseBody
    @RequestMapping("/getWeathData")
    public RespMessage getWeathData(String date){
        return trafficFlowHomePageService.getWeathData(date);
    }

//    /**
//     * OD迁徙数据查询
//     */
//    @ResponseBody
//    @RequestMapping("/migrateODFlow")
//    public RespMessage migrateODFlow(ODFlowParam odFlowParam){
//        return trafficFlowHomePageService.getODMigrateFlow(odFlowParam);
//    }
//
//
//
//    /**
//     * 车辆类型占比
//     */
//    @ResponseBody
//    @RequestMapping("/carTypeRatio")
//    public RespMessage carTypeRatio(ODFlowParam odFlowParam){
//        return trafficFlowHomePageService.getCarTypeRatio(odFlowParam);
//    }

}
