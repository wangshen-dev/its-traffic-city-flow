package its.traffic.flow.service.impl;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.PlotEntity;
import its.traffic.flow.entity.RoadEntity;
import its.traffic.flow.mapper.TrafficBuiltMapper;
import its.traffic.flow.mapper.TrafficRoadBuiltMapper;
import its.traffic.flow.service.TrafficFlowRoadBuiltService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrafficFlowRoadBuiltServiceImpl implements TrafficFlowRoadBuiltService {

    @Autowired
    private TrafficBuiltMapper trafficBuiltMapper;

    @Autowired
    private TrafficRoadBuiltMapper trafficRoadBuiltMapper;

    /**
     * 新增道路配置
     * @param roadEntity
     * @return
     */
    @Override
    public RespMessage createRoadDetails(RoadEntity roadEntity) {

        //查询交通小区id
        Integer stzId = trafficBuiltMapper.selectPlotTzId(roadEntity.getStzName());
        Integer etzId = trafficBuiltMapper.selectPlotTzId(roadEntity.getEtzName());

        if(stzId == null){
            return RespMessage.getFailureMsg("没有当前起点小区");
        }

        if(etzId == null){
            return RespMessage.getFailureMsg("没有当前讫点小区");
        }

        RoadEntity roadEntity2 = new RoadEntity();
        roadEntity2.setStzId(stzId);
        roadEntity2.setEtzId(etzId);
        roadEntity2.setRoadName(roadEntity.getRoadName());
        roadEntity2.setRoadLocaltion(roadEntity.getRoadLocaltion());
        roadEntity2.setDiffusionLocation(roadEntity.getDiffusionLocation());

        roadEntity2.setRoadLength(roadEntity.getLength());
        int desc = 0;
        String type = roadEntity.getType();
        if(type.equals("主干道")){
            desc = 1;
        }else if(type.equals("次干道")){
            desc = 2;
        }else{
            desc = 3;
        }
        roadEntity2.setRoadDescribe(desc);
        trafficRoadBuiltMapper.insertRoadTable(roadEntity2);

        return RespMessage.getSuccessMsg();
    }

    /**
     * 删除小区信息
     * @param id
     * @return
     */
    @Override
    public RespMessage deleteRoadDetails(Integer id) {
        //查询交通小区id
//        Integer stzId = trafficBuiltMapper.selectPlotTzId(roadEntity.getStzName());
//        Integer etzId = trafficBuiltMapper.selectPlotTzId(roadEntity.getEtzName());
//
//        RoadEntity roadEntity2 = new RoadEntity();
//        roadEntity2.setStzId(stzId);
//        roadEntity2.setEtzId(etzId);
//        roadEntity2.setRoadName(roadEntity.getRoadName());
        trafficRoadBuiltMapper.deleteRoadTable(id);
        return RespMessage.getSuccessMsg();
    }

    /**
     * 修改小区信息
     * @param roadEntity
     * @return
     */
    @Override
    public RespMessage updateRoadDetails(RoadEntity roadEntity,Integer id) {
        Map<String,Object> paraMap = new HashMap<>();
        if(roadEntity.getStzName() != null){
            paraMap.put("stzId",trafficBuiltMapper.selectPlotTzId(roadEntity.getStzName()));
        }
        if(roadEntity.getEtzName() != null){
            paraMap.put("etzId",trafficBuiltMapper.selectPlotTzId(roadEntity.getEtzName()));
        }
        if(roadEntity.getRoadName() != null){
            paraMap.put("roadName",roadEntity.getRoadName());
        }
        if(roadEntity.getRoadLocaltion() != null){
            paraMap.put("roadLocaltion",roadEntity.getRoadLocaltion());
        }
        if(roadEntity.getDiffusionLocation() != null){
            paraMap.put("diffusionLocation",roadEntity.getDiffusionLocation());
        }
//        paraMap.put("oldStzId",trafficBuiltMapper.selectPlotTzId(roadEntity.getOldStzName()));
//        paraMap.put("oldEtzId",trafficBuiltMapper.selectPlotTzId(roadEntity.getOldEtzName()));
//        paraMap.put("oldRoadName",trafficBuiltMapper.selectPlotTzId(roadEntity.getOldRoadName()));
        paraMap.put("roadId",id);
        trafficRoadBuiltMapper.updateRoadDetails(paraMap);
        return RespMessage.getSuccessMsg();
    }

    /**
     * 查看单条道路配置
     * @param roadEntity
     * @return
     */
    @Override
    public RespMessage seleteOneRoadDetails(RoadEntity roadEntity,Integer id) {

//        Map<String,Object> paraMap = new HashMap<>();
//        paraMap.put("stzId",trafficRoadBuiltMapper.seletePlotId(roadEntity.getStzName()));
//        paraMap.put("etzId",trafficRoadBuiltMapper.seletePlotId(roadEntity.getEtzName()));
//        paraMap.put("roadName",roadEntity.getRoadName());

//        Map<String,Object> paraMap = new HashMap<>();
//        paraMap.put("stzId",roadEntity.getStzId());
//        paraMap.put("etzId",roadEntity.getEtzId());
//        paraMap.put("roadName",roadEntity.getRoadName());

//        RoadEntity roadEntity1 = trafficRoadBuiltMapper.seleteRoadDetails(paraMap);

        RoadEntity roadEntity1 = trafficRoadBuiltMapper.seleteRoadDetails(id);
        Map<String,Object> map = new HashMap<>();
        map.put("id",roadEntity1.getRoadId());
//        map.put("stzId",roadEntity1.getStzId());
//        map.put("etzId",roadEntity1.getEtzId());
        map.put("stzName",trafficRoadBuiltMapper.seletePlotName(roadEntity1.getStzId()));
        map.put("etzName",trafficRoadBuiltMapper.seletePlotName(roadEntity1.getEtzId()));
        map.put("roadName",roadEntity1.getRoadName());
        map.put("roadLocaltion",roadEntity1.getRoadLocaltion());
        map.put("diffusionLocation",roadEntity1.getDiffusionLocation());
        map.put("type",replaceType(roadEntity1.getRoadDescribe()));
        map.put("length",roadEntity1.getRoadLength());
        return RespMessage.getSuccessMsg(map);
    }

    /**
     * 获取道路列表信息
     * @return
     */
    @Override
    public RespMessage getRoadList() {
        //起点小区-讫点小区 有几条道路
        List<RoadEntity> roadEntities = trafficRoadBuiltMapper.seleteRoadList();
        Map<String,Integer> roadMap = new HashMap<>();
        for (RoadEntity roadEntity : roadEntities){
            if( roadMap.get(roadEntity.getStzId()+"&&"+roadEntity.getEtzId()) == null){
                roadMap.put(roadEntity.getStzId()+"&&"+roadEntity.getEtzId(),1);
            }else{
                roadMap.put(roadEntity.getStzId()+"&&"+roadEntity.getEtzId(),1+roadMap.get(roadEntity.getStzId()+"&&"+roadEntity.getEtzId()));
            }
        }

        //查询交通小区信息
        List<PlotEntity> plotEntities = trafficRoadBuiltMapper.seleteZoneList();
        Map<String,Integer> poltMap = new HashMap<>();
        for(PlotEntity plotEntity1 : plotEntities){
            for(PlotEntity plotEntity2 : plotEntities){
                if(plotEntity1.getTzId() == plotEntity2.getTzId()){
                    continue;
                }
                poltMap.put(plotEntity1.getTzId()+"&&"+plotEntity2.getTzId(),roadMap.get(plotEntity1.getTzId()+"&&"+plotEntity2.getTzId()) == null ? 0 : roadMap.get(plotEntity1.getTzId()+"&&"+plotEntity2.getTzId()) );
            }
        }


        Map<String,Object> resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        for(String key : poltMap.keySet()){
            String[] split = key.split("&&", -1);
            String stzName = trafficRoadBuiltMapper.seletePlotName(Integer.valueOf(split[0]));
            String etzName = trafficRoadBuiltMapper.seletePlotName(Integer.valueOf(split[1]));
//            resultMap.put("stzId",Integer.valueOf(split[0]));
//            resultMap.put("etzId",Integer.valueOf(split[1]));
            resultMap.put("stzName",stzName);
            resultMap.put("etzName",etzName);
            resultMap.put("roads",poltMap.get(key));
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }
        return RespMessage.getSuccessMsg(resultList);
    }

    /**
     * 查看道路详情
     * @param roadEntity
     * @return
     */
    @Override
    public RespMessage seleteRoadDetails(RoadEntity roadEntity) {

        //查询交通小区id
        Integer stzId = trafficBuiltMapper.selectPlotTzId(roadEntity.getStzName());
        Integer etzId = trafficBuiltMapper.selectPlotTzId(roadEntity.getEtzName());
        RoadEntity roadEntity2 = new RoadEntity();
        roadEntity2.setStzId(stzId);
        roadEntity2.setEtzId(etzId);
        List<RoadEntity> roadEntities = trafficRoadBuiltMapper.seleteRoadDetails2(roadEntity2);
        Map<String,Object> resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        for(RoadEntity roadEntity3 : roadEntities){
            resultMap.put("id",roadEntity3.getRoadId());
            resultMap.put("roadName",roadEntity3.getRoadName());
            resultMap.put("roadLocaltion",roadEntity3.getRoadLocaltion());
//            resultMap.put("stzId",roadEntity3.getStzId());
//            resultMap.put("etzId",roadEntity3.getEtzId());
            String stzName = trafficRoadBuiltMapper.seletePlotName(roadEntity3.getStzId());
            String etzName = trafficRoadBuiltMapper.seletePlotName(roadEntity3.getEtzId());
            resultMap.put("stzName",stzName);
            resultMap.put("etzName",etzName);
            resultMap.put("type",replaceType(roadEntity3.getRoadDescribe()));
            resultMap.put("diffusionLocation",roadEntity3.getDiffusionLocation());
            resultMap.put("length",roadEntity3.getRoadLength());
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }
        return RespMessage.getSuccessMsg(resultList);
    }


    public static String replaceType(int type){
        String str = "";
        if(type == 1){
            str = "主干道";
        }else if(type == 2){
            str = "次干道";
        }else {
            str = "低速路";
        }
        return str;
    }
}
