package its.traffic.flow.service;


import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.ODFlowParam;

import java.text.ParseException;

public interface TrafficFlowHostPotService {


    /**
     * 获取交通小区数
     * @return
     */
    RespMessage getPlots();

    /**
     * 核心交通小区OD量分布
     * @param odFlowParam
     * @return
     */
    RespMessage getPlotCoreODFlow(ODFlowParam odFlowParam);

    /**
     * 热点小区 上周od量分布
     * @param date
     * @param plots
     * @return
     */
    RespMessage getPlotCoreODFlowLstaWeek(String date,String plots) throws ParseException;

    /**
     * 小区分布
     * @param plots
     * @param odFlowParam
     * @return
     */
    RespMessage hotPlotDistribution(String plots,ODFlowParam odFlowParam);
}
