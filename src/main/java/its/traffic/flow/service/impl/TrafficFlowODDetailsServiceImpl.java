package its.traffic.flow.service.impl;

import its.traffic.flow.RespMessage;
import its.traffic.flow.config.RoadTraffic;
import its.traffic.flow.entity.*;
import its.traffic.flow.mapper.*;
import its.traffic.flow.service.TrafficFlowODDetailsService;
import its.traffic.flow.utils.DateTimeUtils;
import its.traffic.flow.utils.GPSUtils;
import its.traffic.flow.utils.SortUtils;
import its.traffic.flow.utils.TrafficFlowODUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class TrafficFlowODDetailsServiceImpl implements TrafficFlowODDetailsService {

    @Autowired
    private TrafficODDetailsMapper trafficODDetailsMapper;

    @Autowired
    private TrafficRuleMapper trafficRuleMapper;

    @Autowired
    private TrafficRoadBuiltMapper trafficRoadBuiltMapper;

    @Autowired
    private TrafficBuiltMapper trafficBuiltMapper;

    @Autowired
    private RoadTraffic roadTraffic;

    /**
     * 获取交通小区列表详情
     * @return
     */
    @Override
    public RespMessage getPlotsList() {
        List<PlotEntity> plotsDetailsList = trafficODDetailsMapper.getPlotsDetails();
        Map<String,Object>  resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        for(PlotEntity plotEntity : plotsDetailsList){
            resultMap.put("id",plotEntity.getTzId());
            resultMap.put("otName",plotEntity.getOtName());
            resultMap.put("tzName",plotEntity.getTzName());
            resultMap.put("tzCenterCoordinates",plotEntity.getTzCenterCoordinates());
            resultMap.put("tzScope",plotEntity.getTzScope());
            resultMap.put("tzAudit",plotEntity.getTzAudit());
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }
        return RespMessage.getSuccessMsg(resultList);
    }

    /**
     * 获取OD类型列表详情
     * @return
     */
    @Override
    public RespMessage getODTypeList() {
        List<ODTypeEntity> odDetailsList = trafficODDetailsMapper.getODtypeList();
        Map<String,Object>  resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        for(ODTypeEntity odTypeEntity : odDetailsList){
            resultMap.put("id",odTypeEntity.getOtId());
            resultMap.put("otName",odTypeEntity.getOtName());
            resultMap.put("tzAudit",odTypeEntity.getOtAudit());
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }
        return RespMessage.getSuccessMsg(resultList);
    }

    /**
     * 起点-讫点 道路
     * @param odFlowParam
     * @return
     */
    @Override
    public RespMessage getRoadsList(ODFlowParam odFlowParam) {

        //查询交通小区id
        Integer stzId = trafficBuiltMapper.selectPlotTzId(odFlowParam.getStartZone());
        Integer etzId = trafficBuiltMapper.selectPlotTzId(odFlowParam.getEndZone());

        Map<String,Object> parMap = new HashMap<>();
        parMap.put("stzId",stzId);
        parMap.put("etzId",etzId);
        List<RoadEntity> roadsList = trafficODDetailsMapper.getRoadsList(parMap);
        Map<String,Object> resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        for(RoadEntity roadEntity : roadsList){
            resultMap.put("id",roadEntity.getRoadId());
            resultMap.put("placeName",roadEntity.getRoadName());
            resultMap.put("roadLocation",roadEntity.getRoadLocaltion());
            resultMap.put("length",roadEntity.getRoadLength());
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }

        return RespMessage.getSuccessMsg(resultList);
    }

    /**
     * 出行吸引变化趋势 + 起点、讫点OD量分布
     * @param odFlowParam
     * @return
     */
    @Override
    public RespMessage traveFlowODTrend(ODFlowParam odFlowParam) {

        Map<String,Object> parmMap = new HashMap<>();
        //获取起点地点id
        List<Integer> startAddreddList = new ArrayList<>();
        //获取讫点地点id
        List<Integer> endAddreddList = new ArrayList<>();
        //当起点、讫点都不为null时
        if(odFlowParam.getStartZone()!= null && odFlowParam.getEndZone()!= null){
            parmMap.put("tzName",odFlowParam.getStartZone());
            startAddreddList = getAddrId(trafficRuleMapper,parmMap);
            parmMap = new HashMap<>();
            parmMap.put("tzName",odFlowParam.getEndZone());
            endAddreddList = getAddrId(trafficRuleMapper,parmMap);
        }else if(odFlowParam.getStartZone()!= null && odFlowParam.getEndZone() == null){
            //当起点不为null ，讫点为null时
            parmMap.put("tzName",odFlowParam.getStartZone());
            startAddreddList = getAddrId(trafficRuleMapper,parmMap);
            parmMap = new HashMap<>();
            parmMap.put("tzName2",odFlowParam.getStartZone());
            parmMap.put("otName",odFlowParam.getOtName());
            endAddreddList = getAddrId(trafficRuleMapper,parmMap);
        }else if(odFlowParam.getStartZone()== null && odFlowParam.getEndZone() != null){
            //当起点为null ，讫点不为null时
            parmMap.put("tzName2",odFlowParam.getEndZone());
            parmMap.put("otName",odFlowParam.getOtName());
            startAddreddList = getAddrId(trafficRuleMapper,parmMap);
            parmMap = new HashMap<>();
            parmMap.put("tzName",odFlowParam.getEndZone());
            endAddreddList = getAddrId(trafficRuleMapper,parmMap);
        }
        //获取所有的小区、地点ID
        List<Map<String, Object>> tzId_addrIdRuleList = trafficRuleMapper.getaddrIDAsTzNameAndTzId();
        //小区名称 - 小区id
        Map<String,Integer> tzName_tzIdMap = new HashMap<>();
        //地点id - 小区名称
        Map<Integer,String> addrId_tZNameMap = new HashMap<>();
        for(Map<String, Object> map : tzId_addrIdRuleList){
            addrId_tZNameMap.put(Integer.valueOf(map.get("addrId")+""),map.get("tzName").toString());
            tzName_tzIdMap.put(map.get("tzName").toString(),Integer.valueOf(map.get("tzId").toString()));
        }
        //小区详情，匹配小区经纬度
        List<Map<String, Object>> zoneDetails = trafficRuleMapper.getZoneDetails();
        Map<String,Object> tzName_zoneLon_LatMap = new HashMap<>();
        for (Map<String, Object> zoneMap : zoneDetails){
            tzName_zoneLon_LatMap.put(zoneMap.get("tzName").toString(),zoneMap.get("tzCenterCoordinates"));
        }


        List<Integer> allAddrIDList = new ArrayList<>();
        allAddrIDList.addAll(startAddreddList);  allAddrIDList.addAll(endAddreddList);
        //获取所有数据
        List<FlowData> flowList = getFlowData(odFlowParam, allAddrIDList, trafficODDetailsMapper);
//        System.out.println("抽取数据");
        //小时趋势Map
        Map<Integer,Integer> produce_hourMap = new HashMap<>();
        Map<Integer,Integer> attract_hourMap = new HashMap<>();
        //最终返回map
        Map<String,Object> resultMap = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.00");
        if(odFlowParam.getStartZone()!= null && odFlowParam.getEndZone()!= null){
            //计算出行量变化趋势 小时
            Map<String, List<FlowData>> map = calcTraveFlow_Hour(flowList);
            //计算产生量——小时
            calcHourProduce(map,addrId_tZNameMap,odFlowParam.getStartZone(), produce_hourMap);
            //计算吸引量——小时
            TrafficFlowODUtils.calcHourAttract(map,addrId_tZNameMap,odFlowParam.getStartZone(), attract_hourMap);
            //排序
            Map<Integer, Integer> sortMap = SortUtils.sortMapByKeys(produce_hourMap);
            Integer[] flowArr = new Integer[24];
            Integer[] hourArr = new Integer[24];
            //初始化数组
            TrafficFlowHostPotServiceImpl.initArrData(flowArr,hourArr);
            int produce = 0;
            for(Integer hour : sortMap.keySet()){
                flowArr[hour] = sortMap.get(hour);
                hourArr[hour] = hour;
                produce += sortMap.get(hour);
            }
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("hour",hourArr);
            dataMap.put("produce",flowArr);
            Map<Integer, Integer> sortMap2 = SortUtils.sortMapByKeys(attract_hourMap);
            //初始化数组
            TrafficFlowHostPotServiceImpl.initArrData(flowArr,hourArr);
            int attract = 0;
            for(Integer hour : sortMap2.keySet()){
                flowArr[hour] = sortMap.get(hour);
                attract += sortMap.get(hour);
            }
            dataMap.put("attract",flowArr);
            //出行小时变化趋势
            resultMap.put("traveFlowTrend",dataMap);

            Map<String,Object> map2 = new HashMap<>();
            map2.put("startPlace",odFlowParam.getStartZone());
            map2.put("endPlace",odFlowParam.getEndZone());
            map2.put("startLocation",tzName_zoneLon_LatMap.get(odFlowParam.getStartZone()));
            map2.put("endLocation",tzName_zoneLon_LatMap.get(odFlowParam.getEndZone()));
            map2.put("produce",produce);
            map2.put("attract",attract);
            //地点分布
            resultMap.put("distribution",map2);

            Set<Integer> proCarIdSet = new HashSet<>();
            Set<Integer> attCarIdSet = new HashSet<>();
            //计算起点-讫点OD量
            int[] flows = calcPlaceODTravelFlow(flowList, addrId_tZNameMap, odFlowParam.getStartZone(), proCarIdSet, attCarIdSet);
            //通勤比
            Map<String, String> carCommMap = calcCommCar(proCarIdSet, attCarIdSet, tzName_tzIdMap, odFlowParam.getStartZone(), odFlowParam.getEndZone(), trafficRuleMapper);
            //车辆类型
            Map<String, Object> carTypeMap = calcCarType(proCarIdSet, attCarIdSet, trafficRuleMapper);
            List<Map<String, Object>> pro_carTypeList = calcCityCarType(proCarIdSet, trafficRuleMapper);
            List<Map<String, Object>> att_carTypeList = calcCityCarType(attCarIdSet, trafficRuleMapper);
            Map<String,Object> trMap = new HashMap<>();
            trMap.put("produce",pro_carTypeList);
            trMap.put("attract",att_carTypeList);
            resultMap.put("carType",carTypeMap);
            resultMap.put("cityCarType",trMap);
            resultMap.put("commRartio",carCommMap);

            Map<String,Object> map3 = new HashMap<>();
            map3.put("travelFlow",flows[0]);
            map3.put("times",df.format(flows[2]*1.0/(flows[0]*1.0)));
//            map3.put("times",flows[2]*1.0/(flows[4]*1.0));
            map3.put("speed",df.format(105.0/(flows[2]*1.0/(flows[0]*1.0))));
            Map<String,Object> map4 = new HashMap<>();
            map4.put("produce",map3);
            map3 = new HashMap<>();
            map3.put("travelFlow",flows[1]);
//            map3.put("times",flows[3]*1.0/(flows[5]*1.0));
            map3.put("times",df.format(flows[3]*1.0/(flows[1]*1.0)));
            map3.put("speed",df.format(105.0/(flows[3]*1.0/(flows[1]*1.0))));
            map4.put("attract",map3);
            //出行量、时间
            resultMap.put("travelFlowAndTimes",map4);

        }else if(odFlowParam.getStartZone() == null || odFlowParam.getEndZone() == null){
            String kerTzName = "";
            if(odFlowParam.getStartZone() != null){
                kerTzName = odFlowParam.getStartZone();
            }else {
                kerTzName = odFlowParam.getEndZone();
            }
            //计算出行量变化趋势 小时
            Map<String, List<FlowData>> map = calcTraveFlow_Hour(flowList);
            //计算产生量——小时
            //  如果是计算吸引量   //因为现在以 讫点小区做为起点小区， 计算讫点小区到起点小区的产生量 就是起点小区到讫点小区的吸引量
            calcHourProduce(map,addrId_tZNameMap,kerTzName, produce_hourMap);
            //排序
            Map<Integer, Integer> sortMap = SortUtils.sortMapByKeys(produce_hourMap);
            Integer[] flowArr = new Integer[24];
            Integer[] hourArr = new Integer[24];
            //初始化数组
            TrafficFlowHostPotServiceImpl.initArrData(flowArr,hourArr);
            for(Integer hour : sortMap.keySet()){
                flowArr[hour] = sortMap.get(hour);
                hourArr[hour] = hour;
            }
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("hour",hourArr);
            dataMap.put("travelFlow",flowArr);
            //出行吸引变化趋势
            resultMap.put("traveFlowTrend",dataMap);

            Set<Integer> carIdSet = new HashSet<>();
            //小区名，Map<车牌ID,Set<时间>>
            Map<String, Map<Integer, Set<Integer>>> transFlowMap = TrafficFlowODUtils.transFlowData(flowList, addrId_tZNameMap);
            //计算OD产生量
            Map<String, Integer> flowMap = new HashMap<>();
            Map<String, Double> timesMap = new HashMap<>();
            Map<String, Set<Integer>> commMap = new HashMap<>();

            //flowMap:OD量 timesMap:时间 commMap:小区-车牌号（用于计算通勤车）,carIdSet:所有车牌号
            calcPlotTravelFlow(transFlowMap, kerTzName, 0,flowMap,timesMap,commMap,carIdSet);
            //车辆类型
            List<Map<String, Object>> cityCarTypeList = calcCarType2(carIdSet, trafficRuleMapper);
            List<Map<String, Object>> carTypeList = calcCityCarType(carIdSet, trafficRuleMapper);
            resultMap.put("carType",carTypeList);
            resultMap.put("cityCarType",cityCarTypeList);
            //全市通勤车
            Integer commAll = trafficRuleMapper.getAllCommCarList();
            List<Map<String,Object>> commRationList = new ArrayList<>();

            int sum = 0;
            Map<String,Object> map2 = new HashMap<>();
            Map<String,Object> map3 = new HashMap<>();
            List<Map<String,Object>> resultList = new ArrayList<>();
            List<Map<String,Object>> resultList2 = new ArrayList<>();
            if(odFlowParam.getStartZone() != null){
                for(String tzName : flowMap.keySet()){
                    //小区分布
                    sum += flowMap.get(tzName);
                    map2.put("placeName",tzName);
                    map2.put("travelFlow",flowMap.get(tzName));
                    map2.put("times",df.format(timesMap.get(tzName)));
                    map3.put("speed",df.format(105.0/timesMap.get(tzName)));
                    resultList.add(map2);
                    map2 = new HashMap<>();

                    map3.put("startPlace",kerTzName);
                    map3.put("startLocation",tzName_zoneLon_LatMap.get(kerTzName));

                    map3.put("endPlace",tzName);
                    map3.put("endLocation",tzName_zoneLon_LatMap.get(tzName));
                    map3.put("travelFlow",flowMap.get(tzName));
                    resultList2.add(map3);
                    map3 = new HashMap<>();
                }

                for(String tzName : commMap.keySet()){
                    //通勤比
                    String[] comm  = calcCommCar2(commMap.get(tzName), tzName_tzIdMap, odFlowParam.getStartZone(), tzName, commAll,trafficRuleMapper);
                    Map<String,Object> tmap = new HashMap<>();
                    tmap.put("placeName",tzName);
                    tmap.put("commRatio",comm[0]);
                    tmap.put("cityRatio",comm[1]);
                    commRationList.add(tmap);
                }
            }else {
                for(String tzName : flowMap.keySet()){
                    sum += flowMap.get(tzName);
                    map2.put("placeName",tzName);
                    map2.put("travelFlow",flowMap.get(tzName));
                    map2.put("times",df.format(timesMap.get(tzName)));
                    map3.put("speed",df.format(105.0/timesMap.get(tzName)));
                    resultList.add(map2);
                    map2 = new HashMap<>();

                    map3.put("startPlace",tzName);
                    map3.put("startLocation",tzName_zoneLon_LatMap.get(tzName));
                    map3.put("endPlace",kerTzName);
                    map3.put("endLocation",tzName_zoneLon_LatMap.get(kerTzName));
                    map3.put("travelFlow",flowMap.get(tzName));
                    resultList2.add(map3);
                    map3 = new HashMap<>();
                }
                for(String tzName : commMap.keySet()){
                    //通勤比
                    String[] comm  = calcCommCar2(commMap.get(tzName), tzName_tzIdMap, tzName,odFlowParam.getEndZone(), commAll,trafficRuleMapper);
                    Map<String,Object> tmap = new HashMap<>();
                    tmap.put("placeName",tzName);
                    tmap.put("commRatio",comm[0]);
                    tmap.put("cityRatio",comm[1]);
                    commRationList.add(tmap);
                }
            }

            resultMap.put("commRation",commRationList);
            //基础数据列表
            resultMap.put("baseDataList",resultList);
            //起点-讫点小区流量分布
            resultMap.put("plotDistribution",resultList2);
            //出行量合计
            resultMap.put("travelFlowSum",sum);

        }

        return RespMessage.getSuccessMsg(resultMap);
    }


    /**
     * 计算起点-讫点 时间 OD量 车牌id
     * @param flowList
     * @param addrId_tZNameMap
     * @param tzName
     * @param proCarIdSet
     * @param attCarIdSet
     * @return
     */
    public static int[] calcPlaceODTravelFlow(List<FlowData> flowList,Map<Integer,String> addrId_tZNameMap,String tzName,Set<Integer> proCarIdSet,Set<Integer> attCarIdSet){

        Map<String, Map<Integer, Set<Integer>>> flowDataMap = TrafficFlowHostPotServiceImpl.transFlowData_plot(flowList, addrId_tZNameMap);
        int[] flows = calcODFlow(flowDataMap, tzName,proCarIdSet,attCarIdSet);

        return flows;

    }

    /**
     * 计算 通勤比
     * @param proCarIdSet
     * @param attCarIdSet
     * @param tzName_tzIdMap
     * @param startName
     * @param endName
     * @param trafficRuleMapper
     * @return
     */
    public static  Map<String,String> calcCommCar(Set<Integer> proCarIdSet,Set<Integer> attCarIdSet,Map<String,Integer> tzName_tzIdMap,String startName,String endName,TrafficRuleMapper trafficRuleMapper){

        Map<String,Object> parMap = new HashMap<>();
        parMap.put("tzsId", tzName_tzIdMap.get(startName));
        parMap.put("tzeId", tzName_tzIdMap.get(endName));

        List<Integer> commCarList = trafficRuleMapper.getCommCarList(parMap);
        Map<Integer,Boolean> carIdMap = new HashMap<>();
        for(Integer carId : commCarList){
            carIdMap.put(carId,true);
        }
        double pro = 0;
        for(Integer carId : proCarIdSet){
            if(carIdMap.get(carId) != null){
                pro ++;
            }
        }
        double att = 0;
        for(Integer carId : attCarIdSet){
            if(carIdMap.get(carId) != null){
                att ++;
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        //通勤比
        Map<String,String> commMap = new HashMap<>();
        commMap.put("produce",df.format(pro*100.0/(carIdMap.size()*1.0)) +"%" );
        commMap.put("attract",df.format(att*100.0/(carIdMap.size()*1.0)) +"%" );


        return commMap;

    }

    /**
     * 计算 通勤比
     * @param carIdSet
     * @param tzName_tzIdMap
     * @param startName
     * @param endName
     * @param trafficRuleMapper
     * @return
     */
    public static  String[] calcCommCar2(Set<Integer> carIdSet,Map<String,Integer> tzName_tzIdMap,String startName,String endName, Integer commAll,TrafficRuleMapper trafficRuleMapper){

        Map<String,Object> parMap = new HashMap<>();
        parMap.put("tzsId", tzName_tzIdMap.get(startName));
        parMap.put("tzeId", tzName_tzIdMap.get(endName));

        List<Integer> commCarList = trafficRuleMapper.getCommCarList(parMap);
        Map<Integer,Boolean> carIdMap = new HashMap<>();
        for(Integer carId : commCarList){
            carIdMap.put(carId,true);
        }
        double sum = 0;
        for(Integer carId : carIdSet){
            if(carIdMap.get(carId) != null){
                sum ++;
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        //通勤比
       String commRation = df.format(sum*100.0/(carIdMap.size()*1.0)) +"%";
        String commAllRation = df.format(sum*100.0/(commAll*1.0)) +"%";

        return new String[]{commRation,commAllRation};
    }


    /**
     * 车辆类型
     * @param proCarIdSet
     * @param attCarIdSet
     * @param trafficRuleMapper
     * @return
     */
    public static Map<String,Object> calcCarType(Set<Integer> proCarIdSet,Set<Integer> attCarIdSet,TrafficRuleMapper trafficRuleMapper){
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String, Object>> carTypeList = trafficRuleMapper.getCarTypeList();
        Map<Integer,Integer> carTypeMap = new HashMap<>();
        for(Map<String, Object> cMap : carTypeList){
            carTypeMap.put(Integer.valueOf(cMap.get("carId")+""),Integer.valueOf(cMap.get("type")+""));
        }
        //车辆生成类型
        Map<Integer,Double> proCarMap = new HashMap<>();
        proCarMap.put(0,0.0); proCarMap.put(1,0.0); proCarMap.put(2,0.0); proCarMap.put(3,0.0);
        double proCarSum = 0;
        for(Integer carId : proCarIdSet){
            int type = carTypeMap.get(carId) == null ? 0:carTypeMap.get(carId);
            if(proCarMap.get(type) == null){
                proCarMap.put(type,1.0);
            }else{
                proCarMap.put(type,1.0+proCarMap.get(type));
            }
            proCarSum ++;
        }
        //车辆吸引类型
        Map<Integer,Double> attCarMap = new HashMap<>();
        attCarMap.put(0,0.0); attCarMap.put(1,0.0); attCarMap.put(2,0.0); attCarMap.put(3,0.0);
        double attCarSum = 0;
        for(Integer carId : attCarIdSet){
            int type = carTypeMap.get(carId) == null ? 0:carTypeMap.get(carId);
            if(attCarMap.get(type) == null){
                attCarMap.put(type,1.0);
            }else{
                attCarMap.put(type,1.0+attCarMap.get(type));
            }
            attCarSum ++;
        }

        List<Map<String,Object>> proCarList = new ArrayList<>();
        for(Integer type : proCarMap.keySet()){
            Map<String,Object> carMap = new HashMap<>();
            carMap.put("name",TrafficFlowODUtils.replaceCarType(type));
            carMap.put("value",proCarMap.get(type));
            carMap.put("ratio",df.format(proCarMap.get(type)*100.0/proCarSum) + "%");
            proCarList.add(carMap);
        }
        List<Map<String,Object>> attCarList = new ArrayList<>();
        for(Integer type : attCarMap.keySet()){
            Map<String,Object> carMap = new HashMap<>();
            carMap.put("name",TrafficFlowODUtils.replaceCarType(type));
            carMap.put("value",attCarMap.get(type));
            carMap.put("ratio",df.format(attCarMap.get(type)*100.0/attCarSum) + "%");
            attCarList.add(carMap);
        }
        Map<String,Object> carAllMap = new HashMap<>();
        carAllMap.put("produce",proCarList);
        carAllMap.put("attract",attCarList);

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("carType",carAllMap);
//        List<Map<String,Object>> proCarList = new ArrayList<>();
//        List<Map<String,Object>> attCarList = new ArrayList<>();
//
//        double tqc = attCarIdSet.size() * 2.0 / 10.0;
//        double ccc = attCarIdSet.size() * 3.0 / 10.0;
//        double gjc = attCarIdSet.size() * 1.0 / 10.0;
//        double qtc = attCarIdSet.size() * 4.0 / 10.0;
//        DecimalFormat df = new DecimalFormat("0.00");
//        Map<String,Double> dataMap = new HashMap<>();
//        dataMap.put("0",tqc);dataMap.put("1",ccc);dataMap.put("2",gjc);dataMap.put("3",qtc);
//        String types = "0,1,2,3";
//        for(String type : types.split(",")){
//            Map<String,Object> carMap = new HashMap<>();
//            carMap.put("type",TrafficFlowODUtils.replaceCarType(Integer.valueOf(type)));
//            carMap.put("ratio",df.format(dataMap.get(type)*100.0/attCarIdSet.size()) + "%");
//            attCarList.add(carMap);
//            Map<String,Object> carMap2 = new HashMap<>();
//            carMap2.put("type",TrafficFlowODUtils.replaceCarType(Integer.valueOf(type)));
//            carMap2.put("ratio",df.format(dataMap.get(type)*100.0/proCarIdSet.size()) + "%");
//            proCarList.add(carMap);
//        }
//
//        Map<String,Object> carAllMap = new HashMap<>();
//        carAllMap.put("produce",proCarList);
//        carAllMap.put("attract",attCarList);

        return carAllMap;
    }


    /**
     * 车辆类型
     * @param trafficRuleMapper
     * @return
     */
    public static  List<Map<String,Object>> calcCarType2(Set<Integer> carIdSet,TrafficRuleMapper trafficRuleMapper){

        List<Map<String, Object>> carTypeList2 = trafficRuleMapper.getCarTypeList();
        Map<Integer,Integer> carTypeMap = new HashMap<>();
        for(Map<String, Object> cMap : carTypeList2){
            carTypeMap.put(Integer.valueOf(cMap.get("carId")+""),Integer.valueOf(cMap.get("type")+""));
        }
        //车辆生成类型
        Map<Integer,Double> proCarMap = new HashMap<>();
        proCarMap.put(0,0.0); proCarMap.put(1,0.0); proCarMap.put(2,0.0); proCarMap.put(3,0.0);
        double proCarSum = 0;
        for(Integer carId : carIdSet){
            int type = carTypeMap.get(carId) == null ? 0:carTypeMap.get(carId);
            if(proCarMap.get(type) == null){
                proCarMap.put(type,1.0);
            }else{
                proCarMap.put(type,1.0+proCarMap.get(type));
            }
            proCarSum ++;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String,Object>> carTypeList = new ArrayList<>();
        for(Integer type : proCarMap.keySet()){
            Map<String,Object> carMap = new HashMap<>();
            carMap.put("name",TrafficFlowODUtils.replaceCarType(type));
            carMap.put("ratio",df.format(proCarMap.get(type)*100.0/proCarSum) + "%");
            carTypeList.add(carMap);
        }


        return carTypeList;
    }

    /**
     * 计算市内外车辆类型
     * @param carIdSet
     * @param trafficRuleMapper
     * @return
     */
    public static List<Map<String, Object>> calcCityCarType(Set<Integer> carIdSet,TrafficRuleMapper trafficRuleMapper){
        String table = "t_keen_its_car_number_";
        List<String> carNumberList = new ArrayList<>();
        for(int i = 0; i<=5;i++){
            Map<String,Object> parMap = new HashMap<>();
            parMap.put("carIds",carIdSet);
            parMap.put("table",table+i);
            List<String> list = trafficRuleMapper.getCarNumber(parMap);
            if (list.size() > 0){
                carNumberList.addAll(list);
            }

            if(carNumberList.size() >= carIdSet.size()){
                //当已查询出全部车牌id，跳出循环
                break;
            }
        }

        //省外
        int outside_pro = 0;
        //市外
        int outside_city = 0;
        //市内
        int city = 0;
        //市内市外比例
        for(String carNumber : carNumberList){
            if(!carNumber.substring(0, 1).equals("贵")){
                outside_pro ++;
            }else{
                if(carNumber.substring(1, 2).equals("A")){
                    city ++;
                }else{
                    outside_city++;
                }
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String, Object>> cityCarTypeList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("name","省外");
        map.put("value",outside_pro);
        map.put("ratio",df.format( (outside_pro*100.0)/(carNumberList.size()*1.0) ) + "%");
        cityCarTypeList.add(map);
        map = new HashMap<>();
        map.put("name","市内");
        map.put("value",city);
        map.put("ratio",df.format( (city*100.0)/(carNumberList.size()*1.0) ) + "%");
        cityCarTypeList.add(map);
        map = new HashMap<>();
        map.put("name","市外");
        map.put("value",outside_city);
        map.put("ratio",df.format( (outside_city*100.0)/(carNumberList.size()*1.0) ) + "%");
        cityCarTypeList.add(map);
        return cityCarTypeList;
    }


    /**
     * 计算小区OD量
     * @param flowMap
     * @param kerZone
     * @return
     */
    public static int[] calcODFlow(Map<String,Map<Integer, Set<Integer>>> flowMap,String kerZone,Set<Integer> proCarIdSet,Set<Integer> attCarIdSet){
        //计算OD量
        int allProduce = 0;
        int allAttract = 0;
        int allProTime = 0;
        int allAttTime = 0;
        int proCount = 0;
        int attCount = 0;
        for (String tzName : flowMap.keySet()){
            if(!tzName.equals(kerZone)){
                Map<Integer, Set<Integer>> secMap = flowMap.get(tzName);
                for(Integer secCarId : secMap.keySet()){
                    if(flowMap.get(kerZone) != null){
                        if(flowMap.get(kerZone).get(secCarId) != null){
                            int[] booleans = TrafficFlowODUtils.calcMapODFlow(flowMap.get(kerZone).get(secCarId), secMap.get(secCarId));
                            allProduce += booleans[0];
                            allAttract += booleans[1];

                            if(booleans[0] > 0){
                                proCarIdSet.add(secCarId);
                                allProTime += booleans[2];
                                proCount ++;
                            }
                            if(booleans[1] > 0){
                                attCarIdSet.add(secCarId);
                                allAttTime += booleans[3];
                                attCount ++;
                            }
                        }
                    }
                }
            }
        }

        return new int[]{allProduce,allAttract,allProTime,allAttTime,proCount,attCount};
    }


    /**
     *
     * 计算起点-讫点OD量 产生 吸引
     * @param flowDataMap
     * @param kerTzName
     * @param index
     * @return
     */
    public static void  calcPlotTravelFlow(Map<String, Map<Integer, Set<Integer>>> flowDataMap,String kerTzName,int index, Map<String, Integer> flowMap , Map<String, Double> timesMap, Map<String, Set<Integer>> commMap ,Set<Integer> carIdSet){

        for (String tzName : flowDataMap.keySet()){
            int sum = 0;
            double times = 0;
//            double count = 0;
            Set<Integer> tzNameCarSet = new HashSet<>();
            if(!tzName.equals(kerTzName)){
                Map<Integer, Set<Integer>> secMap = flowDataMap.get(tzName);
                for(Integer secCarId : secMap.keySet()){
                    if(flowDataMap.get(kerTzName) != null){
                        if(flowDataMap.get(kerTzName).get(secCarId) != null){
                            int[] flows = TrafficFlowODUtils.calcMapODFlow(flowDataMap.get(kerTzName).get(secCarId), secMap.get(secCarId));
                            sum += flows[index];

                            if(flows[index] >0){
                                times += flows[index+2];
//                                count ++;
                                carIdSet.add(secCarId);
                                tzNameCarSet.add(secCarId);
                            }
                        }
                    }
                }
                commMap.put(tzName,tzNameCarSet);
//                timesMap.put(tzName,times/count);
                timesMap.put(tzName,times/(sum*1.0));
                flowMap.put(tzName,sum);
            }
        }

    }



    /**
     * 路径轨迹
     * @param odFlowParam
     * @return
     */
    @Override
    public RespMessage pointTrack(ODFlowParam odFlowParam) {

        //查询交通小区id
        Integer stzId = trafficBuiltMapper.selectPlotTzId(odFlowParam.getStartZone());
        Integer etzId = trafficBuiltMapper.selectPlotTzId(odFlowParam.getEndZone());
        RoadEntity roadEntity = new RoadEntity();
        roadEntity.setStzId(stzId);
        roadEntity.setEtzId(etzId);
        List<RoadEntity> roadEntities = trafficRoadBuiltMapper.seleteRoadDetails2(roadEntity);

        if(roadEntities.size() == 0){
            return RespMessage.getFailureMsg("没有相关联道路");
        }

        Map<String,Object> parmMap = new HashMap<>();
        parmMap.put("tzName",odFlowParam.getStartZone());
        //获取起点地点id
        List<Integer> startAddreddList = getAddrId(trafficRuleMapper,parmMap);
        //获取讫点地点id
        parmMap = new HashMap<>();
        parmMap.put("tzName",odFlowParam.getEndZone());
        List<Integer> endAddreddList = getAddrId(trafficRuleMapper,parmMap);

//        //根据道路id 获取道路详情
//        RoadEntity roadEntity = trafficRoadBuiltMapper.seleteRoadDetails(id);
//        //获取道路-经纬度
//        Map<String,String> gpsMap = new HashMap<>();
//        gpsMap.put(roadEntity.getRoadName(),roadEntity.getRoadLocaltion());


        //获取道路-经纬度
        Map<Integer,String> gpsMap = new HashMap<>();
        //获取道路-道路等级
        Map<Integer,Integer> road_describeMap = new HashMap<>();
        //获取道路-道路等级
        Map<Integer,String> roadId_NameMap = new HashMap<>();
        for(RoadEntity roadEntity3 : roadEntities){
            gpsMap.put(roadEntity3.getRoadId(),roadEntity3.getRoadLocaltion());
            road_describeMap.put(roadEntity3.getRoadId(),roadEntity3.getRoadDescribe());
            roadId_NameMap.put(roadEntity3.getRoadId(),roadEntity3.getRoadName());
        }
        //获取地点id-经纬度
        List<Map<String, Object>> addrId_LonLatList = trafficRuleMapper.getAddrIDAsAddrLatLon();
        Map<Integer,Double[]> addrId_LonLatMap = new HashMap<>();
        for(Map<String, Object> map : addrId_LonLatList){
            addrId_LonLatMap.put( Integer.valueOf(map.get("addrId")+""),new Double[]{ Double.valueOf(map.get("addrLon")+""),Double.valueOf(map.get("addrLat")+"") } );
        }

//        List<Integer> addrIdList = GPSUtils.calcAddrId_minLength(gpsMap, addrId_LonLatMap, startAddreddList, id);
//        //获取起点的所有数据
//        List<FlowData> startFlowData = getFlowData(odFlowParam, addrIdList, trafficODDetailsMapper);
//        //获取讫点的所有数据
//        List<FlowData> endFlowData = getFlowData(odFlowParam, endAddreddList, trafficODDetailsMapper);
//
//        //存储OD量数据
//        Map<Integer,Integer> countMap = new HashMap<>();
//        //计算起点道路的OD量
//        calcDetailsODFlow(startFlowData, endFlowData, countMap);

        //存储OD量数据
        Map<Integer,Integer> countMap = new HashMap<>();
//        int allflow = 0;
        //获取起点的所有数据
        List<FlowData> startFlowData = getFlowData(odFlowParam, startAddreddList, trafficODDetailsMapper);
        //获取讫点的所有数据
        List<FlowData> endFlowData = getFlowData(odFlowParam, endAddreddList, trafficODDetailsMapper);

        //计算起点道路的OD量
        calcDetailsODFlow(startFlowData, endFlowData, countMap);
        //计算道路流量
        Map<Integer,Double> roadMap = new HashMap<>();
        double sum = GPSUtils.calcAddrId(gpsMap, addrId_LonLatMap, countMap, roadMap);
        DecimalFormat df = new DecimalFormat("0.00");

        Map<Integer,String> saturaMap = new HashMap<>();
        //计算道路饱和度
        Map<Integer, Double> roadFlowMap = calcRoadFlow(startFlowData, endFlowData, gpsMap, addrId_LonLatMap);

        int hours = DateTimeUtils.getHours(odFlowParam.getStartDate(), odFlowParam.getEndDate());
        for(Integer roadId : roadFlowMap.keySet()){
            int artery = roadTraffic.getArtery(road_describeMap.get(roadId));
            saturaMap.put(roadId,df.format(roadFlowMap.get(roadId)*100.0/(artery*1.0)/(hours*1.0) )+"%");
        }

        Map<String,Object> resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        for(Integer roadId : roadMap.keySet()){
            resultMap.put("saturability",saturaMap.get(roadId));
            resultMap.put("placeName",roadId_NameMap.get(roadId));
            resultMap.put("traveFlow",roadMap.get(roadId));
            resultMap.put("traveFlowRatio",df.format((roadMap.get(roadId)*100.0)/sum)+"%");
            resultMap.put("roadLocation",gpsMap.get(roadId));
            resultMap.put("id",roadId);
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }


        return RespMessage.getSuccessMsg(resultList);
    }


//    /**
//     * 路径轨迹
//     * @param odFlowParam
//     * @return
//     */
//    @Override
//    public RespMessage pointTrack(ODFlowParam odFlowParam,Integer id) {
//        Map<String,Object> parmMap = new HashMap<>();
//        parmMap.put("tzName",odFlowParam.getStartZone());
//        //获取起点地点id
//        List<Integer> startAddreddList = getAddrId(trafficRuleMapper,parmMap);
//        //获取讫点地点id
//        parmMap = new HashMap<>();
//        parmMap.put("tzName",odFlowParam.getEndZone());
//        List<Integer> endAddreddList = getAddrId(trafficRuleMapper,parmMap);
//
//        //存储OD量数据
//        Map<Integer,Integer> countMap = new HashMap<>();
////        int allflow = 0;
//        //获取起点的所有数据
//        List<FlowData> startFlowData = getFlowData(odFlowParam, startAddreddList, trafficODDetailsMapper);
//        //获取讫点的所有数据
//        List<FlowData> endFlowData = getFlowData(odFlowParam, endAddreddList, trafficODDetailsMapper);
//
//        //计算起点道路的OD量
//        calcDetailsODFlow(startFlowData, endFlowData, countMap);
//
//        //查询交通小区id
//        Integer stzId = trafficBuiltMapper.selectPlotTzId(odFlowParam.getStartZone());
//        Integer etzId = trafficBuiltMapper.selectPlotTzId(odFlowParam.getEndZone());
//
//        RoadEntity roadEntity = new RoadEntity();
//        roadEntity.setStzId(stzId);
//        roadEntity.setEtzId(etzId);
//        List<RoadEntity> roadEntities = trafficRoadBuiltMapper.seleteRoadDetails2(roadEntity);
//        //获取道路-经纬度
//        Map<String,String> gpsMap = new HashMap<>();
//        //获取道路-道路等级
//        Map<String,Integer> road_describeMap = new HashMap<>();
//        for(RoadEntity roadEntity3 : roadEntities){
//            gpsMap.put(roadEntity3.getRoadName(),roadEntity3.getRoadLocaltion());
//            road_describeMap.put(roadEntity3.getRoadName(),roadEntity3.getRoadDescribe());
//        }
//        //获取地点id-经纬度
//        List<Map<String, Object>> addrId_LonLatList = trafficRuleMapper.getAddrIDAsAddrLatLon();
//        Map<Integer,Double[]> addrId_LonLatMap = new HashMap<>();
//        for(Map<String, Object> map : addrId_LonLatList){
//            addrId_LonLatMap.put( Integer.valueOf(map.get("addrId")+""),new Double[]{ Double.valueOf(map.get("addrLon")+""),Double.valueOf(map.get("addrLat")+"") } );
//        }
//
//        //计算道路流量
//        Map<String,Double> roadMap = new HashMap<>();
//        double sum = GPSUtils.calcAddrId(gpsMap, addrId_LonLatMap, countMap, roadMap);
//
//        DecimalFormat df = new DecimalFormat("0.00");
//
//        Map<String,String> saturaMap = new HashMap<>();
//        //计算道路饱和度
//        Map<String, Double> roadFlowMap = calcRoadFlow(startFlowData, endFlowData, gpsMap, addrId_LonLatMap);
//        int hours = DateTimeUtils.getHours(odFlowParam.getStartDate(), odFlowParam.getEndDate());
//        for(String roadName : roadFlowMap.keySet()){
//            int artery = roadTraffic.getArtery(road_describeMap.get(roadName));
//            saturaMap.put(roadName,df.format(roadFlowMap.get(roadName)*100.0/(artery*1.0)/(hours*1.0) )+"%");
//        }
//
//
//        Map<String,Object> resultMap = new HashMap<>();
//        List<Map<String,Object>> resultList = new ArrayList<>();
//        for(String roadName : roadMap.keySet()){
//            resultMap.put("saturability",saturaMap.get(roadName));
//            resultMap.put("placeName",roadName);
//            resultMap.put("traveFlow",roadMap.get(roadName));
//            resultMap.put("traveFlowRatio",df.format((roadMap.get(roadName)*100.0)/sum)+"%");
//            resultMap.put("roadLocation",gpsMap.get(roadName));
//            resultList.add(resultMap);
//            resultMap = new HashMap<>();
//        }
//
//
//
//        return RespMessage.getSuccessMsg(resultList);
//    }

    /**
     * 计算道路流量
     * @param startFlowData
     * @param endFlowData
     * @param gpsMap
     * @param addrId_LonLatMap
     * @return
     */
    public static Map<Integer, Double>  calcRoadFlow(List<FlowData> startFlowData,List<FlowData> endFlowData,Map<Integer,String> gpsMap,Map<Integer,Double[]> addrId_LonLatMap){
        List<FlowData> list = new ArrayList<>();
        list.addAll(startFlowData);
        list.addAll(endFlowData);
        //以小时分组
        //key:小时 Map<地点id,车牌集>
        Map<String, Map<Integer, Set<Integer>>> hourMap = addrId_carIdHour(list);
        //释放
        list = new ArrayList<>();
        //计算道路流量
        Map<Integer, Double> roadMap = new HashMap<>();
        for (String hour: hourMap.keySet()){
             GPSUtils.calcAddrId_roadFlow(gpsMap, addrId_LonLatMap, hourMap.get(hour),roadMap);
        }

        return roadMap;

    }




    /**
     * 计算OD
     * @param startFlowData
     * @param endFlowData
     * @param countMap
     * @return
     */
    public static int calcDetailsODFlow(List<FlowData> startFlowData,List<FlowData> endFlowData,Map<Integer,Integer> countMap){
        //根据地点分组
        Map<Integer, Map<Integer, Set<Integer>>> startMap = transStartFlowData(startFlowData);
        Map<Integer, Set<Integer>> endMap = transEndFlowData(endFlowData);
        int sum = 0;
        int allFlow = 0;
        for(Integer addrId : startMap.keySet()){
            Map<Integer, Set<Integer>> kerMap = startMap.get(addrId);
            for(Integer carId : kerMap.keySet()){
                if( endMap.get(carId) != null ){
                    //计算OD量
                    int[] flows = TrafficFlowODUtils.calcMapODFlow(kerMap.get(carId), endMap.get(carId));
                    allFlow += flows[0];
//                    allFlow += flows[1];
                }
            }
            //用map存取数据
//            if(countMap.get(addrId)==null){
                countMap.put(addrId,allFlow);
//            }else {
//                countMap.put(addrId,allFlow+countMap.get(addrId));
//            }
            sum += allFlow;
            allFlow = 0;
        }

        return sum;
    }


    /**
     * 转换起点数据
     * @param startFlowData
     * @return
     */
    public static Map<Integer,Map<Integer, Set<Integer>>> transStartFlowData(List<FlowData> startFlowData){
        //根据地点分组
        Map<Integer, Set<Integer>> carIdMap  = new HashMap<>();
        Set<Integer> set = new TreeSet<>();
        Map<Integer,Map<Integer, Set<Integer>>> startMap = new HashMap<>();
        for (FlowData flowData : startFlowData){
            if( startMap.get(flowData.getAddrId()) == null){
                set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                //key:车 value:该区域下的车辆时间轨迹
                carIdMap.put(flowData.getCarId(),set);
                startMap.put(flowData.getAddrId(),carIdMap);
                set = new TreeSet<>();
                carIdMap = new HashMap<>();
            }else{
                Map<Integer, Set<Integer>> carIdMap2 = startMap.get(flowData.getAddrId());
                if( carIdMap2.get(flowData.getCarId()) == null){
                    set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                    carIdMap2.put(flowData.getCarId(),set);
                    set = new TreeSet<>();
                }else{
                    Set<Integer> set2 = carIdMap2.get(flowData.getCarId());
                    set2.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                    carIdMap2.put(flowData.getCarId(),set2);
                }
                startMap.put(flowData.getAddrId(),carIdMap2);
            }
        }

        return startMap;
    }

    /**
     * 转换讫点数据
     * @param endFlowData
     * @return
     */
    public static  Map<Integer, Set<Integer>> transEndFlowData(List<FlowData> endFlowData){
        Map<Integer, Set<Integer>> endMap = new HashMap<>();
        Set<Integer> set = new TreeSet<>();
        for(FlowData flowData : endFlowData){
            if( endMap.get(flowData.getCarId()) == null){
                set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                endMap.put(flowData.getCarId(),set);
                set = new TreeSet<>();
            }else{
                Set<Integer> set2 = endMap.get(flowData.getCarId());
                set2.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                endMap.put(flowData.getCarId(),set2);
            }
        }

        return endMap;
    }



    /**
     * 计算小时OD量
     * @param flowList
     * @return
     */
    public static Map<String,List<FlowData>>  calcTraveFlow_Hour(List<FlowData> flowList){
        Map<String,List<FlowData>> map = new HashMap<>();
        //按小时分组
        for(FlowData flowData : flowList){
            String hour = flowData.getVsTime().split(":")[0];
            if( map.get(hour) == null ){
                List<FlowData> list = new ArrayList<>();
                list.add(flowData);
                map.put(hour,list);
            }else{
                List<FlowData> list2 = map.get(hour);
                list2.add(flowData);
                map.put(hour,list2);
            }
        }
        return map;
    }


    /**
     * 存取每个小时下的 地点id，流量
     * @param flowList
     * @return
     */
    public static Map<String,Map<Integer,Set<Integer>>>   addrId_carIdHour(List<FlowData> flowList){
        Map<String,Map<Integer,Set<Integer>>> hourMap = new HashMap<>();
        //按小时分组
        for(FlowData flowData : flowList){
            String hour = flowData.getVsTime().split(":")[0];
            if( hourMap.get(hour) == null ){
                Map<Integer,Set<Integer>> map = new HashMap<>();
                Set<Integer> set = new HashSet<>();
                set.add(flowData.getCarId());
                map.put(flowData.getAddrId(),set);
                hourMap.put(hour,map);
            }else{
                Map<Integer, Set<Integer>> map2 = hourMap.get(hour);
                if(map2.get(flowData.getAddrId()) == null){
                    Set<Integer> set = new HashSet<>();
                    set.add(flowData.getCarId());
                    map2.put(flowData.getAddrId(),set);
                }else{
                    Set<Integer> set = map2.get(flowData.getAddrId());
                    set.add(flowData.getCarId());
                    map2.put(flowData.getAddrId(),set);
                }
                hourMap.put(hour,map2);
            }
        }
        return hourMap;
    }

    /**
     * 计算产生量
     * @param map
     * @param addrId_tZNameMap
     * @param tzName
     * @param hourMap
     */
    public static void calcHourProduce(Map<String,List<FlowData>> map,Map<Integer,String> addrId_tZNameMap,String tzName, Map<Integer,Integer> hourMap){

        //按小时计算
        for(String hour : map.keySet()){
            //计算小区OD量
            Map<String, Map<Integer, Set<Integer>>> flowDataMap = TrafficFlowHostPotServiceImpl.transFlowData_plot(map.get(hour), addrId_tZNameMap);
            int[] flows = calcZoneODFlow(flowDataMap, tzName);
            //存储每个小区每小时的OD量
            if(hourMap.get(Integer.valueOf(hour)) == null){
                hourMap.put(Integer.valueOf(hour),flows[0]);
            }else {
                hourMap.put(Integer.valueOf(hour),flows[0]+hourMap.get(Integer.valueOf(hour)));
            }
        }
    }


    /**
     * 计算小区OD量
     * @param flowMap
     * @param kerZone
     * @return
     */
    public static int[] calcZoneODFlow(Map<String,Map<Integer, Set<Integer>>> flowMap,String kerZone){
        //计算OD量
        int allProduce = 0;
        int allAttract = 0;
        int allProTime = 0;
        int allAttTime = 0;
        for (String tzName : flowMap.keySet()){
            if(!tzName.equals(kerZone)){
                Map<Integer, Set<Integer>> secMap = flowMap.get(tzName);
                for(Integer secCarId : secMap.keySet()){
                    if(flowMap.get(kerZone) != null){
                        if(flowMap.get(kerZone).get(secCarId) != null){
                            int[] booleans = TrafficFlowODUtils.calcMapODFlow(flowMap.get(kerZone).get(secCarId), secMap.get(secCarId));
                            allProduce += booleans[0];
                            allAttract += booleans[1];
                            allProTime += booleans[2];
                            allAttTime += booleans[3];
                        }
                    }
                }
            }
        }

        return new int[]{allProduce,allAttract,allProTime,allAttTime};
    }


    /**
     * 获取地点id
     * @param trafficRuleMapper
     * @return
     */
    public static List<Integer> getAddrId(TrafficRuleMapper trafficRuleMapper,Map<String,Object> parmMap){
        List<Integer> list = trafficRuleMapper.isZoneASODTypeGetAddrID(parmMap);
        return list;
    }


    /**
     * 获取流量数据
     * @param odFlowParam
     * @param AddrIDList
     * @param trafficODDetailsMapper
     * @return
     */
    public static  List<FlowData> getFlowData(ODFlowParam odFlowParam,List<Integer> AddrIDList,TrafficODDetailsMapper trafficODDetailsMapper){
        //根据地点id 时间 抽取车流数据
        Map<String,Object> map = new HashMap<>();
        String day = TrafficFlowODUtils.getFlowTableDay(odFlowParam.getStartDate());
        map.put("table","t_keen_its_vehicle_flow"+day);
        map.put("startDate",odFlowParam.getStartDate());
        map.put("endDate",odFlowParam.getEndDate());
        map.put("listAddrIds",AddrIDList);
        map.put("vsDate",odFlowParam.getStartDate().split(" ",-1)[0].replaceAll("-",""));

        //根据地点id
         //获取数据
        List<FlowData> flowList = trafficODDetailsMapper.getFlowData(map);

        return flowList;
    }



}
