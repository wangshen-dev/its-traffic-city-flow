package its.traffic.flow.service.impl;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.FlowData;
import its.traffic.flow.entity.ODFlowParam;
import its.traffic.flow.mapper.TrafficGetFlowDataMapper;
import its.traffic.flow.mapper.TrafficHomePageMapper;
import its.traffic.flow.mapper.TrafficRuleMapper;
import its.traffic.flow.service.TrafficFlowHostPotService;
import its.traffic.flow.utils.DateTimeUtils;
import its.traffic.flow.utils.SortUtils;
import its.traffic.flow.utils.TrafficFlowODUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@Service
public class TrafficFlowHostPotServiceImpl implements TrafficFlowHostPotService {

    @Autowired
    private TrafficRuleMapper trafficRuleMapper;

    @Autowired
    private  TrafficHomePageMapper trafficHomePageMapper;

    @Autowired
    private TrafficGetFlowDataMapper trafficGetFlowDataMapper;

    /**
     * 获取交通小区数
     * @return
     */
    @Override
    public RespMessage getPlots() {
        List<Map<String, Object>> tzId_addrIdList = trafficRuleMapper.getZoneDetails();

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("plots",tzId_addrIdList.size());
        return RespMessage.getSuccessMsg(resultMap);
    }

    /**
     * 核心小区OD量
     * @param odFlowParam
     * @return
     */
    @Override
    public RespMessage getPlotCoreODFlow(ODFlowParam odFlowParam) {

        //获取所有的小区、地点ID
        List<Map<String, Object>> tzId_addrIdRuleList = trafficRuleMapper.getaddrIDAsTzNameAndTzId();
        //地点id - 小区名称
        Map<Integer,String> addrId_tZNameMap = new HashMap<>();
        //小区名称 - 地点id
        Map<String,List<Integer>> parMap = new HashMap<>();
        //存取所有的地点id
        List<Integer> addrIdList = new ArrayList();
        for(Map<String, Object> map : tzId_addrIdRuleList){
            addrId_tZNameMap.put(Integer.valueOf(map.get("addrId")+""),map.get("tzName").toString());
            addrIdList.add(Integer.valueOf(map.get("addrId")+""));
        }

        for (Map<String, Object> map : tzId_addrIdRuleList){
            if(parMap.get(map.get("tzName").toString()) == null){
                List<Integer> list = new ArrayList<>();
                list.add(Integer.valueOf(map.get("addrId")+""));
                parMap.put(map.get("tzName").toString(),list);
            }else{
                List<Integer> list = parMap.get(map.get("tzName").toString());
                list.add(Integer.valueOf(map.get("addrId")+""));
                parMap.put(map.get("tzName").toString(),list);
            }
        }

        //抽取数据
        List<FlowData> flowDataList = TrafficFlowHomePageServiceImpl.getFlowData(odFlowParam, addrIdList, trafficHomePageMapper);
//        System.out.println("抽取数据"+flowDataList.size());
        //数据转换 把地点id转换为小区名称
        //key:小区名称,Map<车牌号，时间集>
        Map<String, Map<Integer, Set<Integer>>> transFlowDataMap = transFlowData_plot(flowDataList, addrId_tZNameMap);

        double allFlow = 0;
        //遍历所有小区
        Map<String,Integer> produceMap = new HashMap<>();
        Map<String,Integer> attactMap = new HashMap<>();
        Map<String,Integer> hourMap = new HashMap<>();
        for(String tzName : parMap.keySet()){
            int[] flows = TrafficFlowODUtils.calcZoneODFlow(transFlowDataMap, tzName);
            allFlow += flows[0];
            allFlow += flows[1];
            if( produceMap.get(tzName) == null){
                produceMap.put(tzName,flows[0]);
            }else{
                produceMap.put(tzName,flows[0]+produceMap.get(tzName));
            }
            if( attactMap.get(tzName) == null){
                attactMap.put(tzName,flows[1]);
            }else{
                attactMap.put(tzName,flows[1]+attactMap.get(tzName));
            }
            //以小时为key
            Map<String, List<FlowData>> map = calcTraveFlow_Hour(flowDataList);
            //计算小时OD量
            calcHourSUM(map,addrId_tZNameMap,tzName, hourMap);
        }


        int hotPlot = 0;
        //存取热点小区数据
        Map<String,Object> hotPlotMap = new HashMap<>();
        //热点小区OD量
        List<Map<String,Object>> hotPlotList = new ArrayList<>();
        //存取热点小区名
        Map<String,Boolean> hotPlotsMap = new HashMap<>();
        //出行OD量列表
        List<Map<String,Object>> resultList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String,Object> map = new HashMap<>();
        List<String> hotPlotList2 = new ArrayList<>();
        for(String tzName : parMap.keySet()){
            Integer sum = produceMap.get(tzName) + attactMap.get(tzName);
            //存取出行OD量列表数据 小区生产量、出行量、占比
            map.put("ratio",df.format((sum*100.0)/(allFlow*1.0))+"%");
            map.put("placeName",tzName);
            map.put("produce",produceMap.get(tzName));
            map.put("attract",attactMap.get(tzName));
            resultList.add(map);
            map = new HashMap<>();
            if ( ((1.5*allFlow/parMap.size())) >= sum) {
                //热点小区
                hotPlot ++;
                hotPlotMap.put("placeName",tzName);
                hotPlotMap.put("travelFlow",sum);
                hotPlotList.add(hotPlotMap);
                hotPlotMap = new HashMap<>();

                hotPlotsMap.put(tzName,true);
                hotPlotList2.add(tzName);
            }
        }


        Map<String,Object> resultMap = new HashMap<>();
        //OD量变化趋势
//        resultMap.put("hotPlotTraveFlowTrend",resultList2);
        //热点小区OD量
        resultMap.put("hotPlotTravelFlowList",hotPlotList);
        //出行OD量列表
        resultMap.put("travelFlowList",resultList);
        //热点小区数
        resultMap.put("hotPlots",hotPlot);


        //热点小区平行出行产生小时变换
        Map<String,Map<Integer,Integer>> placeMap2 = new HashMap<>();
        Map<Integer,Integer> timeMap = new HashMap<>();
        for(String key : hourMap.keySet()){
            String[] split = key.split("_");
            if( hotPlotsMap.get(split[1]) != null){
                if(placeMap2.get(split[1]) == null){
                    timeMap.put(Integer.valueOf(split[0]),hourMap.get(key));
                    placeMap2.put(split[1],timeMap);
                    timeMap = new HashMap<>();
                }else{
                    Map<Integer, Integer> timeMap2 = placeMap2.get(split[1]);
                    timeMap2.put(Integer.valueOf(split[0]),hourMap.get(key));
                    placeMap2.put(split[1],timeMap2);
                }
            }
        }
        List<Map<String,Object>> resultList3 = new ArrayList<>();
        Map<String,Object> map3 = new HashMap<>();
        for(String placeName : placeMap2.keySet()){
            Map<Integer, Integer> sortMap = SortUtils.sortMapByKeys(placeMap2.get(placeName));
            Integer[] flowArr = new Integer[24];
            Integer[] hourArr = new Integer[24];
            //初始化数组
            initArrData(flowArr,hourArr);
            for(Integer hour : sortMap.keySet()){
                flowArr[hour] = sortMap.get(hour);
                hourArr[hour] = hour;
            }
            map3.put("placeName",placeName);
            map3.put("travelFlow",flowArr);
            map3.put("hour",hourArr);
            resultList3.add(map3);
            map3 = new HashMap<>();
        }
        resultMap.put("hotPlotHourTrend",resultList3);
        resultMap.put("hotPlotNameList",hotPlotList2);

        return RespMessage.getSuccessMsg(resultMap);

    }


    @Override
    public RespMessage hotPlotDistribution(String plots,ODFlowParam odFlowParam) {
        //道路详情，匹配道路经纬度、道路名称
        List<Map<String, Object>> addrDetails = trafficRuleMapper.getAddrDetails();
        Map<Integer,Object> addrId_addrLatMap = new HashMap<>();
        Map<Integer,Object> addrId_addrLonMap = new HashMap<>();
        Map<Integer,Object> addrId_addrNameMap = new HashMap<>();
        for (Map<String, Object> addrMap : addrDetails){
            addrId_addrLatMap.put(Integer.valueOf(addrMap.get("addrId")+""),addrMap.get("addrLat"));
            addrId_addrLonMap.put(Integer.valueOf(addrMap.get("addrId")+""),addrMap.get("addrLon"));
            addrId_addrNameMap.put(Integer.valueOf(addrMap.get("addrId")+""),addrMap.get("addrName"));
        }
        //小区详情，匹配小区经纬度
        List<Map<String, Object>> zoneDetails = trafficRuleMapper.getZoneDetails();
        Map<String,Object> tzName_zoneLon_LatMap = new HashMap<>();
        for (Map<String, Object> zoneMap : zoneDetails){
            tzName_zoneLon_LatMap.put(zoneMap.get("tzName").toString(),zoneMap.get("tzCenterCoordinates"));
        }

        //获取所有的小区、地点ID
        List<Map<String, Object>> tzId_addrIdRuleList = trafficRuleMapper.getaddrIDAsTzNameAndTzId();
        //地点id - 小区名称
        Map<Integer,String> addrId_tZNameMap = new HashMap<>();
        //存取所有的地点id
        List<Integer> addrIdList = new ArrayList();
        for(Map<String, Object> map : tzId_addrIdRuleList){
            addrIdList.add(Integer.valueOf(map.get("addrId")+""));
            addrId_tZNameMap.put(Integer.valueOf(map.get("addrId")+""),map.get("tzName").toString());
        }
        //抽取数据
        List<FlowData> flowDataList = TrafficFlowHomePageServiceImpl.getFlowData(odFlowParam, addrIdList, trafficHomePageMapper);


        List<Map<String,Object>> resultList5 = new ArrayList<>();
        Map<String,Object> map5 = new HashMap<>();
        for(String tzName : plots.split(",") ){
            List<Map<String,Object>> resultList = new ArrayList<>();
            //转换数据
            Map<Integer, Map<Integer, Set<Integer>>> addrIdMap = transHotPlotFlowData(flowDataList, addrId_tZNameMap, tzName);
            //计算
            Map<String, Set<Integer>> setMap = TrafficFlowODUtils.calcAddrId(addrIdMap, 0);
            for(Integer addrId : setMap.get("produce")){
                Map<String,Object> map = new HashMap<>();
                map.put("placeName",addrId_addrNameMap.get(addrId));
                map.put("location","["+addrId_addrLonMap.get(addrId)+","+addrId_addrLatMap.get(addrId)+"]");
                resultList.add(map);
            }

            map5.put("produce",resultList);
            resultList = new ArrayList<>();
            for(Integer addrId : setMap.get("attract")){
                Map<String,Object> map = new HashMap<>();
                map.put("placeName",addrId_addrNameMap.get(addrId));
                map.put("location","["+addrId_addrLonMap.get(addrId)+","+addrId_addrLatMap.get(addrId)+"]");
                resultList.add(map);
            }
            map5.put("attract",resultList);
            Map<String,Object> map2 = new HashMap<>();
            map2.put("location",tzName_zoneLon_LatMap.get(tzName));
            map2.put("placeName",tzName);
            map5.put("hotPlot",map2);

            resultList5.add(map5);
            map5 = new HashMap<>();

        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("hotPlotDistribution",resultList5);
        return RespMessage.getSuccessMsg(resultMap);
    }

    /**
     * 前一周热点小区OD量
     * @param sdate
     * @param plots
     * @return
     * @throws ParseException
     */
    @Override
    public RespMessage getPlotCoreODFlowLstaWeek(String sdate, String plots) throws ParseException {

        //获取所有的小区、地点ID
        List<Map<String, Object>> tzId_addrIdRuleList = trafficRuleMapper.getaddrIDAsTzNameAndTzId();
        //地点id - 小区名称
        Map<Integer,String> addrId_tZNameMap = new HashMap<>();
        //小区名称 - 地点id
        Map<String,List<Integer>> parMap = new HashMap<>();
        //存取所有的地点id
        List<Integer> addrIdList = new ArrayList();
        for(Map<String, Object> map : tzId_addrIdRuleList){
            addrId_tZNameMap.put(Integer.valueOf(map.get("addrId")+""),map.get("tzName").toString());
            addrIdList.add(Integer.valueOf(map.get("addrId")+""));
        }

        for (Map<String, Object> map : tzId_addrIdRuleList){
            if(parMap.get(map.get("tzName").toString()) == null){
                List<Integer> list = new ArrayList<>();
                list.add(Integer.valueOf(map.get("addrId")+""));
                parMap.put(map.get("tzName").toString(),list);
            }else{
                List<Integer> list = parMap.get(map.get("tzName").toString());
                list.add(Integer.valueOf(map.get("addrId")+""));
                parMap.put(map.get("tzName").toString(),list);
            }
        }

        List<String> days = DateTimeUtils.getNextNDay(sdate, 7, -1);
        //求前一周热点小区的OD量
        Map<String,Integer> flowMap = new HashMap<>();
        for(String day : days){
            for(String tzName : plots.split(",") ){
                ODFlowParam odFlowParam2 = new ODFlowParam();
                odFlowParam2.setStartDate(day+" 00:00:00");
                odFlowParam2.setEndDate(day+" 23:59:59");
                //抽取数据
                List<FlowData> flowList = TrafficFlowHomePageServiceImpl.getFlowData(odFlowParam2, parMap.get(tzName), trafficHomePageMapper);
//                System.out.println("抽取数据 1周");
                //计算小区OD量
                Map<String, Map<Integer, Set<Integer>>> flowDataMap = transFlowData_plot(flowList, addrId_tZNameMap);
                int[] flows = TrafficFlowODUtils.calcZoneODFlow(flowDataMap, tzName);
                flowMap.put(tzName+"_"+day,flows[0]+flows[1]);
            }
        }

        Map<String,Map<Integer,Integer>> placeMap = new HashMap<>();
        Map<Integer,Integer> dateMap = new HashMap<>();
        for(String key : flowMap.keySet()){
            String[] split = key.split("_",-1);
            if(placeMap.get(split[0]) == null){
                dateMap.put(Integer.valueOf(split[1].replaceAll("-","")),flowMap.get(key));
                placeMap.put(split[0],dateMap);
                dateMap = new HashMap<>();
            }else{
                Map<Integer, Integer> dateMap2 = placeMap.get(split[0]);
                dateMap2.put(Integer.valueOf(split[1].replaceAll("-","")),flowMap.get(key));
                placeMap.put(split[0],dateMap2);
            }
        }
        List<Map<String,Object>> resultList2 = new ArrayList<>();
        Map<String,Object> map2 = new HashMap<>();
        for(String placeName : placeMap.keySet()){
            Map<Integer, Integer> sortMap = SortUtils.sortMapByKeys(placeMap.get(placeName));
            Integer[] flowArr = new Integer[7];
            String[] dateArr = new String[7];
            int i = 0;
            for(Integer date : sortMap.keySet()){
                flowArr[i] = sortMap.get(date);
                dateArr[i] = DateTimeUtils.replaceDate(date);
                i++;
            }
            map2.put("placeName",placeName);
            map2.put("travelFlow",flowArr);
            map2.put("date",dateArr);
            resultList2.add(map2);
            map2 = new HashMap<>();
        }
        return RespMessage.getSuccessMsg(resultList2);
    }

    /**
     * 初始化数组数据
     * @param flowArr
     * @param hourArr
     */
    public static void initArrData(Integer[] flowArr,Integer[] hourArr){
        for(int i = 0;i<=23;i++){
            flowArr[i] = 0;
            hourArr[i] = i;
        }
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
     * 计算总和
     * @param map
     * @param addrId_tZNameMap
     * @param tzName
     * @param hourMap
     */
    public static void calcHourSUM(Map<String,List<FlowData>> map,Map<Integer,String> addrId_tZNameMap,String tzName, Map<String,Integer> hourMap){
        //按小时计算
        for(String hour : map.keySet()){
            //计算小区OD量
            Map<String, Map<Integer, Set<Integer>>> flowDataMap = transFlowData_plot(map.get(hour), addrId_tZNameMap);

            int[] flows = TrafficFlowODUtils.calcZoneODFlow(flowDataMap, tzName);

            //存储每个小区每小时的OD量
            if(hourMap.get(hour+"_"+tzName) == null){
                hourMap.put(hour+"_"+tzName, flows[0]+flows[1]);
            }else {
                hourMap.put(hour+"_"+tzName, flows[0]+flows[1]+hourMap.get(hour+"_"+tzName));
            }
        }

    }

    /**
     * 小区转换数据 用map存取
     * @param flowList
     */
    public static  Map<String,Map<Integer, Set<Integer>>> transFlowData_plot(List<FlowData> flowList, Map<Integer,String> ruleMap){

        //key:区域, map<key:车牌，value:时间集合>
        Map<String,Map<Integer, Set<Integer>>> plotMap = new HashMap<>();
        Map<Integer, Set<Integer>> carIdMap  = new HashMap<>();
        Set<Integer> set = new TreeSet<>();
        for(FlowData flowData : flowList){
            String name =  ruleMap.get(flowData.getAddrId()) == null ?  "未知" : ruleMap.get(flowData.getAddrId());
            if( plotMap.get(name) == null){
                //用set存取时间
                set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                //key:车 value:该区域下的车辆时间轨迹
                carIdMap.put(flowData.getCarId(),set);
                plotMap.put(name,carIdMap);
                carIdMap = new HashMap<>();
                set = new TreeSet<>();
            }else{
                Map<Integer, Set<Integer>> map2 = plotMap.get(name);
                if(map2.get(flowData.getCarId()) == null){
                    set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                    map2.put(flowData.getCarId(),set);
                    set = new TreeSet<>();
                }else{
                    Set<Integer> set2 = map2.get(flowData.getCarId());
                    set2.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                    map2.put(flowData.getCarId(),set2);
                }
                plotMap.put(name,map2);
            }
        }


        return plotMap;
    }


    /**
     * 热点小区分布
     * 转换数据 用map存取
     * @param flowList
     */
    public static  Map<Integer,Map<Integer, Set<Integer>>> transHotPlotFlowData(List<FlowData> flowList, Map<Integer,String> ruleMap,String tzName){
        //key:地点, map<key:车牌，value:时间集合>
        Map<Integer,Map<Integer, Set<Integer>>> addrIdMap = new HashMap<>();
        Map<Integer, Set<Integer>> carIdMap  = new HashMap<>();
        Set<Integer> set = new TreeSet<>();
        for(FlowData flowData : flowList){
            Integer addrId = flowData.getAddrId();
            if(ruleMap.get(addrId) != null){
                if( ruleMap.get(addrId).equals(tzName)){
                    //当该地点id为该小区时
                    addrId = 0;
                }
            }
            if( addrIdMap.get(addrId) == null ){
                //用set存取时间
                set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                //key:车 value:该区域下的车辆时间轨迹
                carIdMap.put(flowData.getCarId(),set);
                addrIdMap.put(addrId,carIdMap);
                carIdMap = new HashMap<>();
                set = new TreeSet<>();
            }else{
                Map<Integer, Set<Integer>> map2 = addrIdMap.get(addrId);
                if(map2.get(flowData.getCarId()) == null){
                    set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                    map2.put(flowData.getCarId(),set);
                    set = new TreeSet<>();
                }else{
                    Set<Integer> set2 = map2.get(flowData.getCarId());
                    set2.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                    map2.put(flowData.getCarId(),set2);
                }
                addrIdMap.put(addrId,map2);
            }
        }
        return addrIdMap;
    }


//    /**
//     * 获取流量数据
//     * @param odFlowParam
//     * @param AddrIDList
//     * @param trafficGetFlowDataMapper
//     * @return
//     */
//    public static  List<FlowData> getFlowData(ODFlowParam odFlowParam,List<Integer> AddrIDList,TrafficGetFlowDataMapper trafficGetFlowDataMapper){
//        //根据地点id 时间 抽取车流数据
//        Map<String,Object> map = new HashMap<>();
//        String day = TrafficFlowODUtils.getFlowTableDay(odFlowParam.getStartDate());
//        map.put("table","t_keen_its_vehicle_flow"+day);
//        map.put("startDate",odFlowParam.getStartDate());
//        map.put("endDate",odFlowParam.getEndDate());
//        map.put("listAddrs",AddrIDList);
//        map.put("vsDate",odFlowParam.getStartDate().split(" ",-1)[0].replaceAll("-",""));
//
//
//        //获取数据
//        List<FlowData> flowList = trafficHomePageMapper.getFlowData(map);
//
//        return flowList;
//    }
}
