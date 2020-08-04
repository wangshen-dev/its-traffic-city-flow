package its.traffic.flow.service;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.ODFlowParam;


/**
 * TrafficFlowHomePageService
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/28 16:21
 **/
public interface TrafficFlowHomePageService {

    /**
     * OD类型
     * @return
     */
    RespMessage queryODType();


    /**
     * 返回交通小区
     * @return
     */
    RespMessage getPlotList(String otName);

    /**
     * 出行OD量列表 + 出行产生变化趋势 + 总OD产生量
     * @return
     */
    RespMessage getODTraveFlow(ODFlowParam odFlowParam);

    /**
     * OD迁徙数据
     * @param odFlowParam
     * @return
     */
    RespMessage getODMigrateFlow(ODFlowParam odFlowParam);


    RespMessage getWeathData(String Date);

    /**
     * 车辆类型占比
     * @param odFlowParam
     * @return
     */
    RespMessage getCarTypeRatio(ODFlowParam odFlowParam);
}
