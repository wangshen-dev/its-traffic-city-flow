package its.traffic.flow.utils;

import java.util.*;

/**
 * GPSUtils
 * gps工具类
 * @author wangshen
 * @version 1.0
 * 2020/7/24 15:16
 **/
public class GPSUtils {

//    private final static double EARTH_RADIUS = 6378137.0;
    /**
     * 计算两点距离
     *
     * @param lat_a
     * @param lng_a
     *
     * @param lat_b
     * @param lng_b
     * @return
     */
    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        double EARTH_RADIUS = 6378137.0;
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }


    /**
     * 计算点到直线的距离 点为 A
     * @param lat_a
     * @param lon_a
     * @param lat_b
     * @param lon_b
     * @param lat_c
     * @param lon_c
     * @return
     */
    public static double getAddrLength(double lat_a, double lon_a, double lat_b, double lon_b,double lat_c, double lon_c){

        double c = getDistance(lat_a,lon_a,lat_b,lon_b);
        double b = getDistance(lat_a,lon_a,lat_c,lon_c);
        double a = getDistance(lat_c,lon_c,lat_b,lon_b);
        //求外接圆半周长p
        double p = (a + b + c) / 2;
        //根据海伦-秦九昭算法求面积S
        double S = Math.sqrt(Math.abs(p * (p - a) * (p - b) * (p - 	c)));
        double distance = S == 0 ? 0d : (2 * S / a);
        return distance;
    }


    /**
     * 计算地点 到 路的最短距离
     * @param gpsMap
     * @param addrId_LonLatMap
     * @param countMap
     * @return
     */
    public static double calcAddrId(Map<Integer,String> gpsMap,Map<Integer,Double[]> addrId_LonLatMap,Map<Integer,Integer> countMap,Map<Integer,Double> roadMap){
        //存取经纬度
        List<String> list = new ArrayList<>();
        Map<Integer, Map<Integer,Double>> addrMap = new HashMap<>();
        //存取距离长度
        List<Double> lengthList = new ArrayList<>();
        for(Integer roadId : gpsMap.keySet()){
            String[] split = gpsMap.get(roadId).split("],\\[", -1);
            //把该路上的经纬度 两两组合
            for(int index1 = 0; index1 <= split.length;index1 ++){
                for(int index2 = index1; index2 < split.length ;  index2++) {
                    if(!split[index1].equals(split[index2])){
                        list.add(split[index1].replaceAll("\\[", "").replaceAll("]", "")+"&&"+split[index2].replaceAll("\\[", "").replaceAll("]", ""));
                    }
                }
            }
            for(Integer addrId : countMap.keySet()){
                //0:Lon 1:Lat
                Double[] doubles = addrId_LonLatMap.get(addrId);

                for(String line : list){
                    String[] split1 = line.split("&&", -1);
                    double lon_b = Double.valueOf(split1[0].split(",")[0]);
                    double lat_b = Double.valueOf(split1[0].split(",")[1]);
                    double lon_c = Double.valueOf(split1[1].split(",")[0]);
                    double lat_c = Double.valueOf(split1[1].split(",")[1]);
                    double addrLength = getAddrLength(doubles[1], doubles[0], lat_b, lon_b, lat_c, lon_c);
                    //存取 地点到这条路的所有距离
                    lengthList.add(addrLength);
                }
                //取出点到直线的最短距离
                double minValue = getMinValue(lengthList);
                lengthList = new ArrayList<>();
                if(addrMap.get(addrId) == null){
                    Map<Integer,Double> map = new HashMap<>();
                    map.put(roadId,minValue);
                    addrMap.put(addrId,map);
                }else {
                    Map<Integer, Double> map2 = addrMap.get(addrId);
                    map2.put(roadId,minValue);
                    addrMap.put(addrId,map2);
                }
            }
            list = new ArrayList<>();
        }

        //取距离最小的道路
        double sum = 0;
        for(Integer addrId : addrMap.keySet()){
            Integer road = 0;
            double min = 99999999999.999;
            for(Integer roadId : addrMap.get(addrId).keySet()){
                if(addrMap.get(addrId).get(roadId) < min){
                    min = addrMap.get(addrId).get(roadId);
                    road = roadId;
                }
            }
            sum += countMap.get(addrId);
            if(roadMap.get(road) == null){
                roadMap.put(road,Double.valueOf(countMap.get(addrId)));
            }else{
                roadMap.put(road,countMap.get(addrId)+roadMap.get(road));
            }
        }

        return sum;

    }


    /**
     * 计算地点 到 路的最短距离
     * @param gpsMap
     * @param addrId_LonLatMap
     * @return
     */
    public static  List<Integer> calcAddrId_minLength(Map<Integer,String> gpsMap,Map<Integer,Double[]> addrId_LonLatMap,List<Integer> addridList,Integer kerRoadId){
        //存取经纬度
        List<String> list = new ArrayList<>();
        Map<Integer, Map<Integer,Double>> addrMap = new HashMap<>();
        //存取距离长度
        List<Double> lengthList = new ArrayList<>();
        for(Integer roadId : gpsMap.keySet()){
            String[] split = gpsMap.get(roadId).split("],\\[", -1);
            //把该路上的经纬度 两两组合
            for(int index1 = 0; index1 <= split.length;index1 ++){
                for(int index2 = index1; index2 < split.length ;  index2++) {
                    if(!split[index1].equals(split[index2])){
                        list.add(split[index1].replaceAll("\\[", "").replaceAll("]", "")+"&&"+split[index2].replaceAll("\\[", "").replaceAll("]", ""));
                    }
                }
            }

            for(Integer addrId : addridList){
                //0:Lon 1:Lat
                if( addrId_LonLatMap.get(addrId) != null){
                    Double[] doubles = addrId_LonLatMap.get(addrId);
                    for(String line : list){
                        String[] split1 = line.split("&&", -1);
                        double lon_b = Double.valueOf(split1[0].split(",")[0]);
                        double lat_b = Double.valueOf(split1[0].split(",")[1]);
                        double lon_c = Double.valueOf(split1[1].split(",")[0]);
                        double lat_c = Double.valueOf(split1[1].split(",")[1]);
                        double addrLength = getAddrLength(doubles[1], doubles[0], lat_b, lon_b, lat_c, lon_c);
                        //存取 地点到这条路的所有距离
                        lengthList.add(addrLength);
                    }
                    //取出点到直线的最短距离
                    double minValue = getMinValue(lengthList);
                    lengthList = new ArrayList<>();
                    if(addrMap.get(addrId) == null){
                        Map<Integer,Double> map = new HashMap<>();
                        map.put(roadId,minValue);
                        addrMap.put(addrId,map);
                    }else {
                        Map<Integer, Double> map2 = addrMap.get(addrId);
                        map2.put(roadId,minValue);
                        addrMap.put(addrId,map2);
                    }
                }
            }

            list = new ArrayList<>();
        }

        List<Integer> addrIdList = new ArrayList<>();
        //取距离最小的道路
        for(Integer addrId : addrMap.keySet()){
            Integer road = 0;
            double min = 99999999999.999;
            for(Integer roadId : addrMap.get(addrId).keySet()){
                if(addrMap.get(addrId).get(roadId) < min){
                    min = addrMap.get(addrId).get(roadId);
                    road = roadId;
                }
            }

            if(road == kerRoadId){
                addrIdList.add(addrId);
            }
        }


        return addrIdList;

    }



    /**
     * 计算地点 到 路的最短距离 道路流量
     * @param gpsMap
     * @param addrId_LonLatMap
     * @param countMap
     * @return
     */
    public static void calcAddrId_roadFlow(Map<Integer,String> gpsMap,Map<Integer,Double[]> addrId_LonLatMap,Map<Integer,Set<Integer>> countMap,Map<Integer, Double> roadMap){
        //存取经纬度
        List<String> list = new ArrayList<>();
        Map<Integer, Map<Integer,Double>> addrMap = new HashMap<>();
        //存取距离长度
        List<Double> lengthList = new ArrayList<>();
        for(Integer roadId : gpsMap.keySet()){
            String[] split = gpsMap.get(roadId).split("],\\[", -1);
            //把该路上的经纬度 两两组合
            for(int index1 = 0; index1 <= split.length;index1 ++){
                for(int index2 = index1; index2 < split.length ;  index2++) {
                    if(!split[index1].equals(split[index2])){
                        list.add(split[index1].replaceAll("\\[", "").replaceAll("]", "")+"&&"+split[index2].replaceAll("\\[", "").replaceAll("]", ""));
                    }
                }
            }
            for(Integer addrId : countMap.keySet()){
                //0:Lon 1:Lat
                Double[] doubles = addrId_LonLatMap.get(addrId);

                for(String line : list){
                    String[] split1 = line.split("&&", -1);
                    double lon_b = Double.valueOf(split1[0].split(",")[0]);
                    double lat_b = Double.valueOf(split1[0].split(",")[1]);
                    double lon_c = Double.valueOf(split1[1].split(",")[0]);
                    double lat_c = Double.valueOf(split1[1].split(",")[1]);
                    double addrLength = getAddrLength(doubles[1], doubles[0], lat_b, lon_b, lat_c, lon_c);
                    //存取 地点到这条路的所有距离
                    lengthList.add(addrLength);
                }
                //取出点到直线的最短距离
                double minValue = getMinValue(lengthList);
                lengthList = new ArrayList<>();
                if(addrMap.get(addrId) == null){
                    Map<Integer,Double> map = new HashMap<>();
                    map.put(roadId,minValue);
                    addrMap.put(addrId,map);
                }else {
                    Map<Integer, Double> map2 = addrMap.get(addrId);
                    map2.put(roadId,minValue);
                    addrMap.put(addrId,map2);
                }
            }
            list = new ArrayList<>();
        }

        //取距离最小的道路
        for(Integer addrId : addrMap.keySet()){
            Integer road = 0;
            double min = 99999999999.999;
            for(Integer roadId : addrMap.get(addrId).keySet()){
                if(addrMap.get(addrId).get(roadId) < min){
                    min = addrMap.get(addrId).get(roadId);
                    road = roadId;
                }
            }
            if(roadMap.get(road) == null){
                roadMap.put(road,Double.valueOf(countMap.get(addrId).size()));
            }else{
                roadMap.put(road,countMap.get(addrId).size()+roadMap.get(road));
            }
        }

    }


    /**
     * 取最小值
     * @param list
     * @return
     */
    public static double getMinValue(List<Double> list){
        double min  = list.get(0);
        for(Double key : list) {
            if( min > key ) {
                min = key;
            }
        }
        return min;
    }

}
