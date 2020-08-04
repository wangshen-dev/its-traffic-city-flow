package its.traffic.flow.utils;

import its.traffic.flow.ResponseException;
import its.traffic.flow.entity.FlowData;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * TrafficFlowODUtils
 * 计算OD量方法
 * @author Lenovo
 * @version 1.0
 * 2020/6/28 16:45
 **/
public class TrafficFlowODUtils {


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
     * 取出最大的值
     * @param map
     * @return
     */
    public static int getMaxValueMap(Map<String, Integer> map) {
        int max = 0;
        int temp = 0;
        for(String key : map.keySet()) {
            temp = map.get(key);
            if( max < temp ) {
                max = temp;
            }
        }

        return max;
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
        for (String tzName : flowMap.keySet()){
            if(!tzName.equals(kerZone)){
                Map<Integer, Set<Integer>> secMap = flowMap.get(tzName);
                for(Integer secCarId : secMap.keySet()){
                    if(flowMap.get(kerZone) != null){
                        if(flowMap.get(kerZone).get(secCarId) != null){
                            int[] booleans = calcMapODFlow(flowMap.get(kerZone).get(secCarId), secMap.get(secCarId));
                            allProduce += booleans[0];
                            allAttract += booleans[1];
                        }
                    }
                }
            }
        }

        return new int[]{allProduce,allAttract};
    }


    /**
     *
     * 计算该地点是否有出去其它地点
     * @param flowMap
     * @param kerAddrId
     * @return
     */
    public static Map<String,Set<Integer>> calcAddrId(Map<Integer,Map<Integer, Set<Integer>>> flowMap,Integer kerAddrId){
        //计算OD量
        int produce = 0;
        int attract = 0;
        Set<Integer> produceSet = new HashSet<>();
        Set<Integer> attractSet = new HashSet<>();
        for (Integer addrId : flowMap.keySet()){
            if(addrId!=kerAddrId){
                Map<Integer, Set<Integer>> secMap = flowMap.get(addrId);
                for(Integer carId : secMap.keySet()){
                    if(flowMap.get(kerAddrId) != null){
                        if(flowMap.get(kerAddrId).get(carId) != null){
                            //转换成数组
                            Integer[] karArr = new Integer[flowMap.get(kerAddrId).get(carId).size()];
                            Integer[] secArr = new Integer[secMap.get(carId).size()];
                            flowMap.get(kerAddrId).get(carId).toArray(karArr);
                            secMap.get(carId).toArray(secArr);
                            //计算产生量
                            produce += calcMapODFlow(karArr, secArr)[0];
                            attract += calcMapODFlow(secArr,karArr)[1];
                            
                        }
                    }
                }
                if(produce > 0){
                    produceSet.add(addrId);
                }
                if(attract > 0){
                    attractSet.add(addrId);
                }
                produce = 0;
                attract = 0;
            }
        }
        Map<String,Set<Integer>> map = new HashMap<>();
        map.put("produce",produceSet);
        map.put("attract",attractSet);
        return map;
    }

    /**
     * 计算
     * @param karSet
     * @param secSet
     * @return
     */
    public static int[] calcMapODFlow(Set<Integer> karSet,Set<Integer> secSet){

        //转换成数组
        Integer[] karArr = new Integer[karSet.size()];
        Integer[] secArr = new Integer[secSet.size()];
        karSet.toArray(karArr);
        secSet.toArray(secArr);
        //计算产生量
        int[] produce = calcMapODFlow(karArr, secArr);
        //计算吸引量
        int[] attract = calcMapODFlow(secArr,karArr);
        return new int[]{produce[0],attract[0],produce[1],attract[1]};
    }

    /**
     * 根据两个数组计算od量
     * @param karArr
     * @param secArr
     * @return
     */
    public static int[] calcMapODFlow(Integer[] karArr, Integer[] secArr){
        int odFlow = 0;
        int kar = 0;
        int sec = 0;
        int temp = 0;

        int times = 0;
        for( ; kar < karArr.length; kar++) {
            for ( ; sec < secArr.length; sec++) {
                if(odFlow > 0) {
                    if(karArr[kar] < temp ) {
                        break;
                    }
                }
                //当起始小区时间 小于 终点小区时，出行量+1
                if (karArr[kar] < secArr[sec]) {
                    //计算时间差
                    int min = DateTimeUtils.calcTime_hour_min(karArr[kar], secArr[sec]);
                    times += min;

                    //计算od量
                    odFlow++;
                    kar++;
                    temp = secArr[sec];

                }
                if(kar >= karArr.length) {
                    break;
                }
            }
        }
        //0:od量 1：时间
        return new int[]{odFlow,times};
    }





    /**
     * 计算吸引量
     * @param map
     * @param addrId_tZNameMap
     * @param tzName
     * @param hourMap
     */
    public static void calcHourAttract(Map<String,List<FlowData>> map,Map<Integer,String> addrId_tZNameMap,String tzName, Map<Integer,Integer> hourMap){
        //按小时计算
        for(String hour : map.keySet()){
            //计算小区OD量
            Map<String, Map<Integer, Set<Integer>>> flowDataMap = TrafficFlowODUtils.transFlowData(map.get(hour), addrId_tZNameMap);
            int[] flows = TrafficFlowODUtils.calcZoneODFlow(flowDataMap, tzName);
            //存储每个小区每小时的OD量
            if(hourMap.get(Integer.valueOf(hour)) == null){
                hourMap.put(Integer.valueOf(hour),flows[1]);
            }else {
                hourMap.put(Integer.valueOf(hour),flows[1]+hourMap.get(Integer.valueOf(hour)));
            }
        }
    }


    /**
     * 替换车辆类型
     * @param type
     */
    public static String  replaceCarType(int type){
        String str = "";
        if(type == 1){
            str = "通勤车";
        }else if(type == 2){
            str = "出租车";
        }else if(type == 3){
            str = "公交车";
        }else{
            str = "其它类型";
        }
        return str;
    }

    /**
     * 截取日期 获取天
     * @param date
     * @return
     */
    public static String getFlowTableDay(String date){
        String day = date.split(" ")[0].split("-")[2];

        return day;
    }

    /**
     * 判断是区域 还是 地区
     * @param place
     * @return
     */
    public static boolean isTrueDistinctAndZone(String place){
        boolean flag = false;
        if(place.equals("观山湖区") || place.equals("云岩区") || place.equals("南明区") || place.equals("花溪区") || place.equals("乌当区") || place.equals("白云区")){
                flag = true;
        }

        return flag;
    }

    /**
     *生成UUid
     */
    public static String createUUId() {
        //注意replaceAll前面的是正则表达式
        String uuid = UUID.randomUUID().toString().replaceAll("-","");

        return uuid;
    }

    /**
     * 获取token信息
     * @param request
     * @param stringRedisTemplate
     * @return
     * @throws ResponseException
     *
     */
    public static String getToken(HttpServletRequest request, StringRedisTemplate stringRedisTemplate) throws ResponseException {
        String token = request.getHeader("token");
//        System.out.println(token);
        String value = stringRedisTemplate.opsForValue().get(token);
//        System.out.println(value);
        if(value == null){
            throw new ResponseException("请先登录");
        }
        return value;
    }
}
