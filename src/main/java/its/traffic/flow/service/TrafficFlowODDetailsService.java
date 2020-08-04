package its.traffic.flow.service;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.ODFlowParam;

public interface TrafficFlowODDetailsService {


    /**
     * 获取交通小区列表
     * @return
     */
    RespMessage getPlotsList();


    /**
     * 获取OD类型列表
     * @return
     */
    RespMessage getODTypeList();

    /**
     * 获取起点-讫点道路信息
     * @return
     */
    RespMessage getRoadsList(ODFlowParam odFlowParam);

    /**
     * 出行量 起点、讫点变化趋势
     * @param odFlowParam
     * @return
     */
    RespMessage traveFlowODTrend(ODFlowParam odFlowParam);


    /**
     * 路径轨迹
     * @param odFlowParam
     * @return
     */
    RespMessage pointTrack(ODFlowParam odFlowParam);
}
