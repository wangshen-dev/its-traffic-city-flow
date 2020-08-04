package its.traffic.flow.service.impl;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.FlowData;
import its.traffic.flow.entity.ODFlowParam;
import its.traffic.flow.mapper.TrafficHomePageMapper;
import its.traffic.flow.mapper.TrafficRuleMapper;
import its.traffic.flow.service.TrafficFlowHomePageService;
import its.traffic.flow.utils.SortUtils;
import its.traffic.flow.utils.TrafficFlowODUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * TrafficFlowHomePageServiceImpl
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/28 16:22
 **/
@Service
public class TrafficFlowHomePageServiceImpl implements TrafficFlowHomePageService {

    @Autowired
    private TrafficHomePageMapper trafficHomePageMapper;

    @Autowired
    private TrafficRuleMapper trafficRuleMapper;



    /**
     * 返回OD类型
     * @return
     */
    @Override
    public RespMessage queryODType() {
        List<Map<String,Object>> list = trafficHomePageMapper.queryODType();
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Boolean> otNameMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        int id = 0;
        String otNames = "行政区域,交通枢纽,旅游景点,经济商圈,住宅小区,OD量聚类";
        for(String otName : otNames.split(",")){
            resultMap.put("otName",otName);
            resultMap.put("id",++id);
            resultList.add(resultMap);
            resultMap = new HashMap<>();
            otNameMap.put(otName,true);
        }

        for(Map<String,Object> map : list){
            if(otNameMap.get(map.get("otName")) == null){
                resultMap.put("otName",map.get("otName"));
                resultMap.put("id",++id);
                resultList.add(resultMap);
                resultMap = new HashMap<>();
            }
        }
        return RespMessage.getSuccessMsg(resultList);
    }

    /**
     * 返回交通小区
     * @param otName
     * @return
     */
    @Override
    public RespMessage getPlotList(String otName) {
        List<Map<String, Object>> otNameGetPlots = trafficHomePageMapper.isOtNameGetPlots(otName);

        return RespMessage.getSuccessMsg(otNameGetPlots);
    }


    /**
     * 获取天气数据
     * @param date
     * @return
     */
    @Override
    public RespMessage getWeathData(String date) {
        Map<String, Object> weatherData = trafficRuleMapper.getWeatherData(date);
        if(weatherData == null){
            return RespMessage.getSuccessMsg("无数据");
        }

        return RespMessage.getSuccessMsg(weatherData);
    }

    /**
     * 出行OD量列表 + 出行产生变化趋势 + 总OD产生量
     * @param odFlowParam
     * @return
     */
    @Override
    public RespMessage getODTraveFlow(ODFlowParam odFlowParam){
        if(odFlowParam.getOtName() == null){
            return RespMessage.getFailureMsg("OD类型名称(otName)不能为空");
        }
        List<Map<String,Object>> odList = trafficHomePageMapper.queryODType();
        boolean flag = true;
        for(Map<String,Object> map : odList){
            if(map.get("otName").toString().equals(odFlowParam.getOtName())){
                flag = false;
            }
        }
        if(flag){
            return RespMessage.getFailureMsg("暂未开发该OD类型");
        }
        //获取ot类型下的小区名称
        List<Map<String, Object>> otNameGetPlots = trafficHomePageMapper.isOtNameGetPlots(odFlowParam.getOtName());
        List<String> regionList = new ArrayList<>();
        for(Map<String,Object> map : otNameGetPlots){
            regionList.add(map.get("tzName").toString());
        }
//        String regions = "观山湖区,白云区,乌当区,云岩区,花溪区,南明区";
        //获取行政区域下的地点id
        List<Integer> addrIdlists = new ArrayList<>();
        for(String region : regionList){
            //获取小区下的所有地点id
            List<Integer> AddrIDList = trafficRuleMapper.getTzNameAsAddress(region);
            addrIdlists.addAll(AddrIDList);
        }

        //获取地点id-小区名称
        List<Map<String,Object>> addrId_tzNameList = trafficRuleMapper.getaddrIDAsTzNameAndTzId();
        Map<Integer,String> addrId_tzNameMap = new HashMap<>();
        Map<String,Integer> tzName_tzIdMap = new HashMap<>();
        for(Map<String, Object> map : addrId_tzNameList){
            addrId_tzNameMap.put(Integer.valueOf(map.get("addrId")+""),map.get("tzName").toString());
            tzName_tzIdMap.put(map.get("tzName").toString(),Integer.valueOf(map.get("tzId")+""));
        }

        //获取道路长度
        Map<String,Double> roadLengthMap = new HashMap<>();
        for(String key : tzName_tzIdMap.keySet()){
            for(String key2 : tzName_tzIdMap.keySet()){
                if(!key.equals(key2)){
                    Map<String,Object> roadParMap = new HashMap<>();
                    roadParMap.put("stzId",tzName_tzIdMap.get(key));
                    roadParMap.put("etzId",tzName_tzIdMap.get(key2));
                    Double roadLength = trafficRuleMapper.getRoadLengths(roadParMap);
                    roadLengthMap.put(key+"_"+key2,roadLength);
                }
            }
        }

//        //获取小区下的所有地点id
//        List<Integer> AddrIDList = trafficRuleMapper.getTzNameAsAddress(odFlowParam.getTzName());
        //根据地点id 时间 抽取车流数据
        List<FlowData> flowList = getFlowData_plot(odFlowParam, addrIdlists, trafficHomePageMapper);

        Set<Integer> proSet = new HashSet<>();
        Set<Integer> attSet = new HashSet<>();

        //计算行政区域OD量
        Map<String, Map<Integer, Set<Integer>>> flowDataMap = transFlowData(flowList, addrId_tzNameMap);
        Map<String, Object> resultMap = calcRegionODFlow(flowDataMap, odFlowParam.getTzName(),odFlowParam.getSize(),proSet,attSet,roadLengthMap);

        //计算变化趋势
        Map<String,List<FlowData>> flowMap = new HashMap<>();
        List<FlowData> list = new ArrayList<>();
        for(FlowData flowData : flowList){
            String hour = flowData.getVsTime().split(":")[0];
            if( flowMap.get(hour) == null){
                list.add(flowData);
                flowMap.put(hour,list);
                list = new ArrayList<>();
            }else{
                List<FlowData> list2 = flowMap.get(hour);
                list2.add(flowData);
                flowMap.put(hour,list2);
            }
        }
        Map<Integer,Integer> produceMap = new HashMap<>();
        Map<Integer,Integer> attractMap = new HashMap<>();

        Set<Integer> proSet2 = new HashSet<>();
        Set<Integer> attSet2 = new HashSet<>();
        for(String hour : flowMap.keySet()){
            Map<String, Map<Integer, Set<Integer>>> flowDataMap2 = transFlowData(flowMap.get(hour), addrId_tzNameMap);
            Map<String, Object> resultMap2 = calcRegionODFlow(flowDataMap2,odFlowParam.getTzName(),0,proSet2,attSet2,roadLengthMap);
            Map<String,Object>  obj2Map = (Map<String,Object>)resultMap2.get("allTravelFlow");

            produceMap.put(Integer.valueOf(hour),Integer.valueOf(obj2Map.get("allProduce")+""));
            attractMap.put(Integer.valueOf(hour),Integer.valueOf(obj2Map.get("allAttract")+""));
        }

        //排序
        Map<Integer, Integer> sortMap = SortUtils.sortMapByKeys(produceMap);
        Integer[] flowArr = new Integer[24];
        Integer[] hourArr = new Integer[24];
        //初始化数组
        TrafficFlowHostPotServiceImpl.initArrData(flowArr,hourArr);
        for(Integer hour : sortMap.keySet()){
            flowArr[hour] = sortMap.get(hour);
            hourArr[hour] = hour;
        }
        Map<String,Object> proHourMap = new HashMap<>();
        proHourMap.put("hour",hourArr);
        proHourMap.put("travelFlow",flowArr);

        //排序
        Map<Integer, Integer> sortMap2 = SortUtils.sortMapByKeys(attractMap);
        //初始化数组
        flowArr = new Integer[24];
        hourArr = new Integer[24];
        TrafficFlowHostPotServiceImpl.initArrData(flowArr,hourArr);
        for(Integer hour : sortMap2.keySet()){
            flowArr[hour] = sortMap2.get(hour);
            hourArr[hour] = hour;
        }
        Map<String,Object> attHourMap = new HashMap<>();
        attHourMap.put("hour",hourArr);
        attHourMap.put("travelFlow",flowArr);

        Map<String,Object> map2 = new HashMap<>();
        map2.put("produceTrend",proHourMap);
        map2.put("attractTrend",attHourMap);
        resultMap.put("travelFlowTrend",map2);


        List<Map<String, Object>> carTypeList = trafficRuleMapper.getCarTypeList();
        Map<Integer,Integer> carTypeMap = new HashMap<>();
        for(Map<String, Object> cMap : carTypeList){
            carTypeMap.put(Integer.valueOf(cMap.get("carId")+""),Integer.valueOf(cMap.get("type")+""));
        }

        //车辆生成类型
        Map<Integer,Double> proCarMap = new HashMap<>();
        proCarMap.put(0,0.0); proCarMap.put(1,0.0); proCarMap.put(2,0.0); proCarMap.put(3,0.0);
        double proCarSum = 0;
        for(Integer carId : proSet){
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
        for(Integer carId : attSet){
            int type = carTypeMap.get(carId) == null ? 0:carTypeMap.get(carId);
            if(attCarMap.get(type) == null){
                attCarMap.put(type,1.0);
            }else{
                attCarMap.put(type,1.0+attCarMap.get(type));
            }
            attCarSum ++;
        }
        DecimalFormat df = new DecimalFormat("0.00");
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
        resultMap.put("carType",carAllMap);

        return RespMessage.getSuccessMsg(resultMap);
    }



    /**
     * OD迁徙数据
     * @param odFlowParam
     * @return
     */
    @Override
    public RespMessage getODMigrateFlow(ODFlowParam odFlowParam) {
        return null;
    }


    /**
     * 车辆类型占比
     * @param odFlowParam
     * @return
     */
    @Override
    public RespMessage getCarTypeRatio(ODFlowParam odFlowParam) {

        return RespMessage.getSuccessMsg();
    }


    /**
     * 计算区域OD量
     * @param regionMap
     * @param kerRegion
     * @return
     */
    public static Map<String,Object>  calcRegionODFlow(Map<String,Map<Integer, Set<Integer>>> regionMap,String kerRegion,int size,Set<Integer> proSet,Set<Integer> attSet,Map<String,Double> roadLengthMap){

        //计算OD量
        Map<String,Integer> proMap = new HashMap<>();
        Map<String,Integer> attMap = new HashMap<>();
        Map<String,Double> proTimeMap = new HashMap<>();
        Map<String,Double> attTimeMap = new HashMap<>();
        int allProduce = 0;
        int allAttract = 0;
        for (String tzName : regionMap.keySet()){
            int produce = 0;
            int attract = 0;

            int proTime = 0;
//            int proCount = 0;
            double attTime = 0;
//            double attCount = 0;
            if(!tzName.equals(kerRegion)){
                Map<Integer, Set<Integer>> secMap = regionMap.get(tzName);
                for(Integer secCarId : secMap.keySet()){
                    if(regionMap.get(kerRegion).get(secCarId) != null){
                        int[] booleans = TrafficFlowODUtils.calcMapODFlow(regionMap.get(kerRegion).get(secCarId), secMap.get(secCarId));
                        produce += booleans[0];
                        attract += booleans[1];

                        if(booleans[2] > 0){
                            proTime += booleans[2];
//                            proCount ++;
                        }
                        if(booleans[3] > 0){
                            attTime += booleans[3];
//                            attCount ++;
                        }

                        if(booleans[0] > 0){
                            proSet.add(secCarId);
                        }
                        if(booleans[1] > 0){
                            attSet.add(secCarId);
                        }

                    }
                }
                proMap.put(kerRegion+"_"+tzName,produce);
                attMap.put(tzName+"_"+kerRegion,attract);
//                proTimeMap.put(kerRegion+"_"+tzName,proTime*1.0/proCount);
//                attTimeMap.put(tzName+"_"+kerRegion,attTime*1.0/attCount);
                proTimeMap.put(kerRegion+"_"+tzName,proTime*1.0/(produce*1.0));
                attTimeMap.put(tzName+"_"+kerRegion,attTime*1.0/(attract*1.0));
                allProduce +=produce;
                allAttract += attract;
            }
        }

        //取出最大值
        int proMax = TrafficFlowODUtils.getMaxValueMap(proMap);
        int attMax = TrafficFlowODUtils.getMaxValueMap(attMap);
        int maxFlow = (proMax > attMax ? proMax : attMax);
        List<Map<String,Object>> produceList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.00");
        double roadLength = 15.6;
        for (String name : proMap.keySet()){
//            map.put("placeName",name);
            //当比例为0时，显示所有数据。 不为0时，选择本身值大于最大值的比例
            if(size == 0 || (proMap.get(name) >= maxFlow*(size*1.0/100.0)) ){
                map.put("startPlaceName",name.split("_")[0]);
                map.put("endPlaceName",name.split("_")[1]);
                map.put("travelFlow",proMap.get(name));
                map.put("ratio",df.format((proMap.get(name)*100.0)/(allProduce*1.0))+"%" );
                map.put("times",df.format(proTimeMap.get(name)));
//                map.put("speed",df.format(roadLengthMap.get(name)/proTimeMap.get(name)));
                map.put("speed",df.format(roadLength/(proTimeMap.get(name)/60.0)));
                produceList.add(map);
                map = new HashMap<>();
            }
        }
        List<Map<String,Object>> attractList = new ArrayList<>();
        for (String name : attMap.keySet()){
//            map.put("placeName",name);
            if(size == 0 || (attMap.get(name) >= maxFlow*(size*1.0/100.0)) ) {
                map.put("startPlaceName", name.split("_")[0]);
                map.put("endPlaceName", name.split("_")[1]);
                map.put("travelFlow", attMap.get(name));
                map.put("ratio", df.format((attMap.get(name) * 100.0) / (allAttract * 1.0)) + "%");
                map.put("times",df.format(attTimeMap.get(name)));
                attractList.add(map);
                map = new HashMap<>();
            }
        }

        Map<String,Object> map2 = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>();
        //OD量列表
        map2.put("produce",produceList);
        map2.put("attract",attractList);
        resultMap.put("TravelFlowList",map2);
        map2 = new HashMap<>();
        //总量
        map2.put("allProduce",allProduce);
        map2.put("allAttract",allAttract);
        resultMap.put("allTravelFlow",map2);
        //最大值
        resultMap.put("maxTravelFlow",maxFlow);
        return resultMap;
    }




    /**
     * 转换数据 用map存取
     * @param flowList
     */
    public static  Map<String,Map<Integer, Set<Integer>>> transFlowData(List<FlowData> flowList, Map<Integer,String> ruleMap){

        //key:区域, map<key:车牌，value:时间集合>
        Map<String,Map<Integer, Set<Integer>>> regionMap = new HashMap<>();
        Map<Integer, Set<Integer>> carIdMap  = new HashMap<>();
        Set<Integer> set = new TreeSet<>();
        for(FlowData flowData : flowList){
            if( ruleMap.get(flowData.getAddrId()) != null ){
                if( regionMap.get(ruleMap.get(flowData.getAddrId())) == null){
                    //用set存取时间
                    set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                    //key:车 value:该区域下的车辆时间轨迹
                    carIdMap.put(flowData.getCarId(),set);
                    regionMap.put(ruleMap.get(flowData.getAddrId()),carIdMap);
                    carIdMap = new HashMap<>();
                    set = new TreeSet<>();
                }else{
                    Map<Integer, Set<Integer>> map2 = regionMap.get(ruleMap.get(flowData.getAddrId()));
                    if(map2.get(flowData.getCarId()) == null){
                        set.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                        map2.put(flowData.getCarId(),set);
                        set = new TreeSet<>();
                    }else{
                        Set<Integer> set2 = map2.get(flowData.getCarId());
                        set2.add(Integer.valueOf(flowData.getVsTime().replaceAll(":","")));
                        map2.put(flowData.getCarId(),set2);
                    }
                    regionMap.put(ruleMap.get(flowData.getAddrId()),map2);
                }
            }
        }

        return regionMap;
    }


    /**
     * 获取流量数据
     * @param odFlowParam
     * @param AddrIDList
     * @param trafficHomePageMapper
     * @return
     */
    public static  List<FlowData> getFlowData_plot(ODFlowParam odFlowParam,List<Integer> AddrIDList,TrafficHomePageMapper trafficHomePageMapper){
        //根据地点id 时间 抽取车流数据
        Map<String,Object> map = new HashMap<>();
        String day = TrafficFlowODUtils.getFlowTableDay(odFlowParam.getStartDate());
        map.put("table","t_keen_its_vehicle_flow"+day);
        map.put("startDate",odFlowParam.getStartDate());
        map.put("endDate",odFlowParam.getEndDate());
        map.put("listAddrs",AddrIDList);
        map.put("vsDate",odFlowParam.getStartDate().split(" ",-1)[0].replaceAll("-",""));

        //获取数据
        List<FlowData> flowList = trafficHomePageMapper.getFlowData_plot(map);

        return flowList;
    }


    /**
     * 获取流量数据
     * @param odFlowParam
     * @param AddrIDList
     * @param trafficHomePageMapper
     * @return
     */
    public static  List<FlowData> getFlowData(ODFlowParam odFlowParam,List<Integer> AddrIDList,TrafficHomePageMapper trafficHomePageMapper){
        //根据地点id 时间 抽取车流数据
        Map<String,Object> map = new HashMap<>();
        String day = TrafficFlowODUtils.getFlowTableDay(odFlowParam.getStartDate());
        map.put("table","t_keen_its_vehicle_flow"+day);
        map.put("startDate",odFlowParam.getStartDate());
        map.put("endDate",odFlowParam.getEndDate());
        map.put("listAddrs",AddrIDList);
        map.put("vsDate",odFlowParam.getStartDate().split(" ",-1)[0].replaceAll("-",""));

        //获取车牌id
        List<String> carIdList = trafficHomePageMapper.getCarId(map);
        if(carIdList.size() > 0){
            map.put("listCarIds",carIdList);
        }
//        System.out.println(" SELECT addrId,carId,substring(vsTime,12,5) as vsTime FROM "+map.get("table")+" WHERE vsTime BETWEEN "+map.get("startDate")+" AND "+map.get("endDate")+" AND addrId is not null");
        //获取数据
        List<FlowData> flowList = trafficHomePageMapper.getFlowData(map);

        return flowList;
    }
}
