package its.traffic.flow.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * TrafficRuleMapper
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/3 15:40
 **/
@Mapper
public interface TrafficRuleMapper {


    /**
     * 获取道路详情
     * @return
     */
   List<Map<String,Object>> getAddrDetails();

    /**
     * 获取小区详情
     * @return
     */
   List<Map<String,Object>> getZoneDetails();

    /**
     * 根据小区名 获取小区下所有的地点Id
     * @param tzName
     * @return
     */
    List<Integer> getTzNameAsAddress(String tzName);


    /**
     * 获取地点id、小区名称、小区Id
     * @return
     */
    List<Map<String,Object>> getaddrIDAsTzNameAndTzId();

    /**
     * 获取道路Id 、道路名称
     * @return
     */
    List<Map<String,Object>> getAddrIDAsAddrName();

    /**
     * 获取地点Id 、经纬度
     * @return
     */
    List<Map<String,Object>>  getAddrIDAsAddrLatLon();

    /**
     * 根据小区 OD类型 获取地点id
     * @param map
     * @return
     */
    List<Integer> isZoneASODTypeGetAddrID(Map<String,Object> map);


    /**
     * 车辆类型id
     * @return
     */
    List<Map<String,Object>> getCarTypeList();

    /**
     * 通勤车id
     * @return
     */
    List<Integer> getCommCarList(Map<String,Object> map);

   /**
    * 全市通勤车数量
    * @return
    */
    Integer getAllCommCarList();

   /**
    * 查找车牌号
    * @return
    */
   List<String> getCarNumber(Map<String,Object> map);

     /**
      * 获取小区之间道路长度
      * @param map
      * @return
      */
     Double getRoadLengths(Map<String,Object> map);

    Map<String,Object> getWeatherData(String Date);
}
