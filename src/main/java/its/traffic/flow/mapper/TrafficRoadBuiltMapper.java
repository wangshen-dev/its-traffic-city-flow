package its.traffic.flow.mapper;

import its.traffic.flow.entity.PlotEntity;
import its.traffic.flow.entity.RoadEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TrafficRoadBuiltMapper {

    /**
     * 新增道路信息
     * @param roadEntity
     */
    @Insert("insert into t_keen_its_road_install(`stzId`,`etzId`,`roadName`,`roadLocaltion`,`diffusionLocation`,`roadLength`,`roadDescribe`) values(#{stzId},#{etzId},#{roadName},#{roadLocaltion},#{diffusionLocation},#{roadLength},#{roadDescribe})")
    void insertRoadTable(RoadEntity roadEntity);

    /**
     * 删除道路信息
     * @param id
     */
    @Delete("delete from t_keen_its_road_install where roadId = #{id}")
    void deleteRoadTable(Integer id);

    /**
     * 修改道路配置
     * @param map
     */
    void  updateRoadDetails(Map<String,Object> map);
    /**
     * 查询道路配置
     * @param roadId
     * @return
     */
    RoadEntity seleteRoadDetails(Integer roadId);

    /**
     * 查询道路详情
     * @param roadEntity
     * @return
     */
    List<RoadEntity> seleteRoadDetails2(RoadEntity roadEntity);

    /**
     * 查询小区名称
     * @param tzId
     * @return
     */
    String seletePlotName(Integer tzId);

    /**
     * 查询小区id
     * @param tzName
     * @return
     */
    Integer seletePlotId(String tzName);


    List<RoadEntity> seleteRoadList();


    List<PlotEntity>  seleteZoneList();
}
