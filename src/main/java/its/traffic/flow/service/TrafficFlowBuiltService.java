package its.traffic.flow.service;


import its.traffic.flow.RespMessage;
import its.traffic.flow.ResponseException;
import its.traffic.flow.entity.ChangeEntity;
import its.traffic.flow.entity.ODTypeEntity;
import its.traffic.flow.entity.PlotEntity;

public interface TrafficFlowBuiltService {

    /**
     * 新增od类型
     * @param ODTypeEntity
     * @return
     */
    RespMessage insertODType(ODTypeEntity ODTypeEntity) throws ResponseException;

    /**
     * 新增交通小区
     * @param plotEntity
     * @return
     */
    RespMessage insertPlot(PlotEntity plotEntity) throws ResponseException;

    /**
     * 检验交通小区是否存在
     * @param plotEntity
     * @return
     */
    RespMessage isExitPlot(PlotEntity plotEntity);


    /**
     * 检验OD类型是否存在
     * @param ODTypeEntity
     * @return
     */
    RespMessage isExitODType(ODTypeEntity ODTypeEntity);

    /**
     * 修改交通小区
     * @param plotEntity
     * @return
     */
    RespMessage updatePlot(PlotEntity plotEntity,Integer id) throws ResponseException;


    /**
     * 修改OD类型
     * @param ODTypeEntity
     * @return
     */
    RespMessage updateODType(ODTypeEntity ODTypeEntity,Integer id) throws ResponseException;

    /**
     * 获取交通小区数据审核
     * @return
     */
    RespMessage auditPoltData() throws ResponseException;

    /**
     * 获取OD类型数据审核
     * @return
     */
    RespMessage auditODTypeData() throws ResponseException;

    /**
     * 获取交通小区列表
     * @return
     */
    RespMessage getPlotsList(PlotEntity plotEntity) throws ResponseException;

    /**
     * 获取OD类型列表
     * @return
     */
    RespMessage getODTypeList() throws ResponseException;

    /**
     * 删除交通小区
     * @param plotEntity
     * @return
     */
    RespMessage deletePlot(PlotEntity plotEntity,Integer id) throws ResponseException;

    /**
     * 删除OD类型
     * @param odTypeEntity
     * @return
     */
    RespMessage deletODType(ODTypeEntity odTypeEntity,Integer id) throws ResponseException;

    /**
     * 处理交通小区数据_审核过程
     * @param changeEntity
     * @return
     */
    RespMessage processPlotDataBefore(ChangeEntity changeEntity,Integer tzId) throws ResponseException;
    /**
     * 处理OD类型数据_审核过程
     * @param changeEntity
     * @return
     */
    RespMessage processODTyeDataBefore(ChangeEntity changeEntity,Integer otId) throws ResponseException;
    /**
     * 处理交通小区数据_审核后
     * @param changeEntity
     * @return
     */
    RespMessage processPlotDataLater(ChangeEntity changeEntity,Integer tzId) throws ResponseException;

    /**
     * 审核未通过
     * @param changeEntity
     * @param tzId
     * @return
     * @throws ResponseException
     */
    RespMessage processPlotDataLater_false(ChangeEntity changeEntity,Integer tzId) throws ResponseException;

    /**
     * 处理OD类型数据_审核后
     * @param changeEntity
     * @return
     */
    RespMessage processODTyeDataLater(ChangeEntity changeEntity,Integer otId) throws ResponseException;

    /**
     * 审核未通过
     * @param changeEntity
     * @param otId
     * @return
     * @throws ResponseException
     */
    RespMessage  processODTyeDataLater_false(ChangeEntity changeEntity,Integer otId) throws ResponseException;
}
