package its.traffic.flow.controller;

import its.traffic.flow.RespMessage;
import its.traffic.flow.ResponseException;
import its.traffic.flow.entity.ChangeEntity;
import its.traffic.flow.entity.ODTypeEntity;
import its.traffic.flow.entity.PlotEntity;
import its.traffic.flow.service.TrafficFlowBuiltService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TrafficFlowBuiltController
 * 基础构建模块
 * @author wangshen
 * @version 1.0
 * 2020/7/13 11:21
 **/
@Controller
@RequestMapping("/traffic/flow/built")
public class TrafficFlowBuiltController {

    @Autowired
    TrafficFlowBuiltService trafficFlowBuiltService;

    /**
     * 新增OD类型
     */
    @ResponseBody
    @RequestMapping("/createODType")
    public RespMessage createODType(ODTypeEntity ODTypeEntity) throws ResponseException {
        return trafficFlowBuiltService.insertODType(ODTypeEntity);
    }

    /**
     * 新增交通小区
     */
    @ResponseBody
    @RequestMapping("/createPlot")
    public RespMessage createPolt(PlotEntity plotEntity)throws ResponseException {
        return trafficFlowBuiltService.insertPlot(plotEntity);
    }


    /**
     * 检验交通小区是否存在
     */
    @ResponseBody
    @RequestMapping("/isExitPlot")
    public RespMessage isExitPlot(PlotEntity plotEntity){
        return trafficFlowBuiltService.isExitPlot(plotEntity);
    }

    /**
     * 检验交通小区类型是否存在
     */
    @ResponseBody
    @RequestMapping("/isExitODType")
    public RespMessage isExitODType(ODTypeEntity odTypeEntity){
        return trafficFlowBuiltService.isExitODType(odTypeEntity);
    }

    /**
     * 修改交通小区
     */
    @ResponseBody
    @RequestMapping("/updatePlot")
    public RespMessage updatePolt(PlotEntity plotEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.updatePlot(plotEntity,id);
    }

    /**
     * 修改OD类型
     */
    @ResponseBody
    @RequestMapping("/updateODType")
    public RespMessage updateODType(ODTypeEntity ODTypeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.updateODType(ODTypeEntity,id);
    }


    /**
     * 显示交通小区数据审核列表
     */
    @ResponseBody
    @RequestMapping("/auditPlotData")
    public RespMessage auditPoltData()throws ResponseException {
        return trafficFlowBuiltService.auditPoltData();
    }

    /**
     * 显示OD类型数据审核列表
     */
    @ResponseBody
    @RequestMapping("/auditODTypeData")
    public RespMessage auditODTypeData()throws ResponseException {
        return trafficFlowBuiltService.auditODTypeData();
    }


    /**
     * 获取交通小区列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPlotsList")
    public RespMessage getPlots(PlotEntity plotEntity)throws ResponseException {
        return trafficFlowBuiltService.getPlotsList(plotEntity);
    }

    /**
     * 获取OD类型列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/getODTypeList")
    public RespMessage getODtypeList()throws ResponseException {
        return trafficFlowBuiltService.getODTypeList();
    }

    /**
     * 删除交通小区
     */
    @ResponseBody
    @RequestMapping("/deletePlot")
    public RespMessage deletePolt(PlotEntity plotEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.deletePlot(plotEntity,id);
    }


    /**
     * 删除OD类型
     */
    @ResponseBody
    @RequestMapping("/deleteODType")
    public RespMessage deleteODType(ODTypeEntity odTypeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.deletODType(odTypeEntity,id);
    }

    /**
     * 处理交通小区数据_审核过程
     */
    @ResponseBody
    @RequestMapping("/processPlotDataBefore")
    public RespMessage processPlotDataBefore(ChangeEntity changeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.processPlotDataBefore(changeEntity,id);
    }

    /**
     * 处理OD数据_审核过程
     */
    @ResponseBody
    @RequestMapping("/processODTypeDataBefore")
    public RespMessage processODTypeDataBefore(ChangeEntity changeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.processODTyeDataBefore(changeEntity,id);
    }


    /**
     * 处理交通小区数据_审核后
     */
    @ResponseBody
    @RequestMapping("/processPlotDataLater")
    public RespMessage processPlotDataLater(ChangeEntity changeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.processPlotDataLater(changeEntity,id);
    }

    /**
     * 处理OD类型数据_审核后
     */
    @ResponseBody
    @RequestMapping("/processODTypeDataLater")
    public RespMessage processODTyeDataLater(ChangeEntity changeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.processODTyeDataLater(changeEntity,id);
    }


    /**
     * 处理交通小区数据_审核未通过
     */
    @ResponseBody
    @RequestMapping("/processPlotDataLater_false")
    public RespMessage processPlotDataLater_false(ChangeEntity changeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.processPlotDataLater_false(changeEntity,id);
    }

    /**
     * 处理OD类型数据_审核未通过
     */
    @ResponseBody
    @RequestMapping("/processODTypeDataLater_false")
    public RespMessage processODTyeDataLater_false(ChangeEntity changeEntity,Integer id)throws ResponseException {
        return trafficFlowBuiltService.processODTyeDataLater_false(changeEntity,id);
    }
}
