package its.traffic.flow.mapper;

import its.traffic.flow.entity.ChangeEntity;
import its.traffic.flow.entity.ODTypeEntity;
import its.traffic.flow.entity.PlotEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface TrafficBuiltMapper {

    /**
     * 获取最大交通小区id
     * @return
     */
    @Select("SELECT max(tzId) from t_keen_its_traffic_zone")
    int getMaxPlotTableId();

    /**
     * 获取最大OD类型id
     * @return
     */
    @Select("SELECT max(otId) from t_keen_its_od_type")
    int getMaxODTypeTableId();

    /**
     * od类型 新增
     * @param ODTypeEntity
     */
    @Insert("insert into t_keen_its_od_type(`otName`,`otFlag`,`otAudit`) values(#{otName},#{otFlag},#{otAudit})")
    void insertODType(ODTypeEntity ODTypeEntity);

    /**
     * 交通小区 新增
     * @param plotEntity
     */
    @Insert("insert into t_keen_its_traffic_zone(`otId`,`tzName`,`tzFlag`,`tzCenterCoordinates`,`tzScope`,`tzAudit`) values(#{otId},#{tzName},#{tzFlag},#{tzCenterCoordinates},#{tzScope},#{tzAudit})")
    void insertPlot(PlotEntity plotEntity);

    @Insert("insert into t_keen_its_change(`userId`,`cType`,`cDataId`,`cDes`,`cDateTime`,`cContent`) values(#{userId},#{cType},#{cDataId},#{cDes},#{cDateTime},#{cContent})")
    void insertChange(ChangeEntity changeEntity);

    /**
     * 查询变更表数据
     * @param map
     * @return
     */
    Integer selectChange(Map<String,Object> map);

    /**
     * 根据数据id 查询userId
     * @param map
     * @return
     */
    List<Integer> selectChangUserId(Map<String,Object> map);

    /**
     * 根据id查询交通小区
     * @param tzId
     * @return
     */
    Map<String,Object> idGetZoneDetails(Integer tzId);


    /**
     * 查询变更表数据 cContent
     * @param map
     * @return
     */
    String selectChangeData_cContent(Map<String,Object> map);
    /**
     * 查询该小区是否存在
     * @param tzName
     * @return
     */
    Integer selectPlotIsExist(String tzName);

    /**
     * 查询小区Id
     * @param tzName
     * @return
     */
    Integer selectPlotTzId(String tzName);
    /**
     * 查询OD类型是否存在
     * @param otName
     * @return
     */
    Integer selectODtypeIsExist(String otName);

    /**
     * 查询otId
     * @param otName
     * @return
     */
    Integer selectODtypeOtId(String otName);
    /**
     * 根据oD名称 查询otId
     * @param otName
     * @return
     */
    int queryOtId(String otName);

    /**
     * 根据oDId 查询otName
     * @param otId
     * @return
     */
    String queryOtName(Integer otId);

    /**
     * 修改交通小区内容
     * @param map
     */
//    @Update("UPDATE t_keen_its_traffic_zone set otId = #{otId},tzName = #{tzName}, tzFlag = #{tzFlag},tzCenterCoordinates = #{tzCenterCoordinates},tzScope = #{tzScope},tzAudit = #{tzAudit} where tzId = #{tzId1} and tzName = #{tzName1}")
    void updatePlot(Map<String,Object> map);

    /**
     * 修改OD类型
     * @param map
     */
    void updateODType(Map<String,Object> map);

    /**
     * 修改日志变更表
     * @param map
     */
    void updateChangeTable(Map<String,Object> map);


    /**
     * 查询 交通小区审核数据列表
     * @return
     */
    List<Map<String,Object>> selectAuditPlotData();

    /**
     * 查询 OD类型审核数据列表
     * @return
     */
    List<Map<String,Object>> selectODTypeData();

    /**
     * 获取交通小区数据
     * @return
     */
    List<Map<String,Object>> getPlotsDetails(Map<String,Object> map);




    /**
     * 获取OD类型数据
     * @return
     */
    List<Map<String,Object>> getODTypeDetails();
    /**
     * 获取OD类型数据 单条
     * @return
     */
    Map<String,Object> getOneODTypeDetails(Integer otId);

    /**
     * 删除变更表
     * @param changeEntity
     */
    @Delete("delete from t_keen_its_change where cType = #{cType} and cDataId = #{cDataId} and cDes = #{cDes}")
    void delteChangeTbale(ChangeEntity changeEntity);


    /**
     * 更改小区表 （删除，修改审核状态）
     * @param tzId
     */
    @Update("UPDATE t_keen_its_traffic_zone set tzAudit = 2 ,tzFlag = 1 where tzId = #{tzId}")
    void updatePlotTbale_delete(Integer tzId);


    /**
     * 更改OD表 （删除，修改审核状态）
     * @param otId
     */
    @Update("UPDATE t_keen_its_od_type set otAudit = 2 ,otFlag = 1 where otId = #{otId}")
    void delteODTypeTbale_delete(Integer otId);


    /**
     * 审核后 新增小区表
     * @param tzId
     */
    @Update("UPDATE t_keen_its_traffic_zone set tzAudit = 1 ,tzFlag = 1 where tzId = #{tzId}")
    void insertPlotTable_audit(Integer tzId);

    /**
     * 审核后 删除小区表
     * @param tzId
     */
    @Update("UPDATE t_keen_its_traffic_zone set tzAudit = 1 ,tzFlag = 2 where tzId = #{tzId}")
    void deletePlotTable_audit(Integer tzId);

    /**
     * 审核后 新增OD表
     * @param otId
     */
    @Update("UPDATE t_keen_its_od_type set otAudit = 1 ,otFlag = 1 where otId = #{otId}")
    void insertODTypeTable_audit(Integer otId);

    /**
     * 审核后 删除OD表
     * @param otId
     */
    @Update("UPDATE t_keen_its_od_type set otAudit = 1 ,otFlag = 2 where otId = #{otId}")
    void deleteODTypeTable_audit(Integer otId);

    /**
     * 修改交通小区状态
     * @param tzId
     */
    @Update("UPDATE t_keen_its_traffic_zone set tzAudit = 2 where tzId = #{tzId}")
    void updatePlotType(Integer tzId);

    /**
     * 根据otId 修改交通小区状态
     * @param otId
     */
    @Update("UPDATE t_keen_its_traffic_zone set tzFlag = 2 where otId = #{otId}")
    void updatePlotType_ot(Integer otId);

    /**
     * 修改OD表状态
     * @param otId
     */
    @Update("UPDATE t_keen_its_od_type set otAudit = 2 where otId = #{otId}")
    void updateODTableType(Integer otId);


    /**
     * 删除小区信息，审核未通过
     * @param tzId
     */
    @Delete("Delete from t_keen_its_traffic_zone where tzId = #{tzId}")
    void deletePlot_false(Integer tzId);

    /**
     * 删除od类型信息，审核未通过
     * @param otId
     */
    @Delete("Delete from t_keen_its_od_type where otId = #{otId}")
    void deleteODType_false(Integer otId);
}
