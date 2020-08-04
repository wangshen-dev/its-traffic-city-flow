package its.traffic.flow.controller;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.ODFlowParam;
import its.traffic.flow.service.TrafficFlowHostPotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;

/**
 * TrafficFlowHomePageController
 * 热点模块
 * @author wangshen
 * @version 1.0
 * 2020/7/7 11:45
 **/
@Controller
@RequestMapping("/traffic/flow/hotPlot")
public class TrafficFlowHostPotController {

    @Autowired
    TrafficFlowHostPotService trafficFlowHostPotService;


    /**
     * 获取交通小区数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPlots")
    public RespMessage getPlots(){
        return trafficFlowHostPotService.getPlots();
    }


    /**
     * 核心交通小区OD量分布+热点小区数+热点小区OD量+热点小区OD量分布+热点小区小时变换
     */
    @ResponseBody
    @RequestMapping("/plotCoreODFlowDistribution")
    @Cacheable("hotPlot-flowList")
    public RespMessage plotCoreODFlowDistribution(ODFlowParam odFlowParam) {
        return trafficFlowHostPotService.getPlotCoreODFlow(odFlowParam);
    }

    /**
     * 热点小区上周流量
     */
    @ResponseBody
    @RequestMapping("/plotCoreODFlowLastWeek")
    @Cacheable("hotPlot-lastWeek")
    public RespMessage plotCoreODFlowLastWeek(String date,String plots) throws ParseException {
        return trafficFlowHostPotService.getPlotCoreODFlowLstaWeek(date,plots);
    }


    /**
     * 热点小区轨迹分布
     */
    @ResponseBody
    @RequestMapping("/hotPlotDistribution")
    @Cacheable("hotPlot-distribution")
    public RespMessage hotPlotDistribution(String plots,ODFlowParam odFlowParam)  {
        return trafficFlowHostPotService.hotPlotDistribution(plots,odFlowParam);
    }






}
