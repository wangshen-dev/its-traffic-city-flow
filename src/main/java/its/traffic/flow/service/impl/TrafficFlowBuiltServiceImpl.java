package its.traffic.flow.service.impl;

import its.traffic.flow.RespMessage;
import its.traffic.flow.ResponseException;
import its.traffic.flow.entity.ChangeEntity;
import its.traffic.flow.entity.ODTypeEntity;
import its.traffic.flow.entity.PlotEntity;
import its.traffic.flow.mapper.TrafficBuiltMapper;
import its.traffic.flow.service.TrafficFlowBuiltService;
import its.traffic.flow.utils.DateTimeUtils;
import its.traffic.flow.utils.TrafficFlowODUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrafficFlowBuiltServiceImpl implements TrafficFlowBuiltService {

    @Autowired
    private TrafficBuiltMapper trafficBuiltMapper;
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 检验交通小区是否存在
     * @param plotEntity
     * @return
     */
    @Override
    public RespMessage isExitPlot(PlotEntity plotEntity) {
        Integer num = trafficBuiltMapper.selectPlotIsExist(plotEntity.getTzName());
        if(num > 0){
            return RespMessage.getFailureMsg("该小区已存在");
        }

        return RespMessage.getSuccessMsg("新交通小区");
    }

    /**
     * 检验OD类型是否存在
     * @param odTypeEntity
     * @return
     */
    @Override
    public RespMessage isExitODType(ODTypeEntity odTypeEntity) {
        Integer num = trafficBuiltMapper.selectODtypeIsExist(odTypeEntity.getOtName());
        if(num > 0){
            return RespMessage.getFailureMsg("该OD类型已存在");
        }
        return RespMessage.getSuccessMsg("新OD类型");
    }

    /**
     * 新增OD类型_变更表
     * @param ODTypeEntity
     * @return
     */
    @Override
    public RespMessage insertODType(ODTypeEntity ODTypeEntity) throws ResponseException {
        //获取token信息
        String userId = TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        Integer num = trafficBuiltMapper.selectODtypeIsExist(ODTypeEntity.getOtName());
        if(num > 0){
            return RespMessage.getFailureMsg("该OD类型已存在");
        }

        ChangeEntity changeEntity = new ChangeEntity();
        //user id
        changeEntity.setUserId(Integer.valueOf(userId));
        //数据id
        int otId = trafficBuiltMapper.getMaxODTypeTableId() + 1;
        changeEntity.setcDataId(otId);
        //数据类型
        changeEntity.setcType(2);
        //变更描述
        changeEntity.setcDes("新增交通小区类型");
        changeEntity.setcDateTime(DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
        changeEntity.setcContent(ODTypeEntity.getOtName());
        //新增 变更表
        trafficBuiltMapper.insertChange(changeEntity);

        ODTypeEntity ODTypeEntity2 = new ODTypeEntity();
        ODTypeEntity2.setOtName(ODTypeEntity.getOtName());
        ODTypeEntity2.setOtFlag(1);
        ODTypeEntity2.setOtAudit(2);
        //新增od类型表
        trafficBuiltMapper.insertODType(ODTypeEntity2);
        return RespMessage.getSuccessMsg("OD类型新增成功");
    }

    /**
     * 新增交通小区_变更表
     * @param plotEntity
     * @return
     */
    @Override
    public RespMessage insertPlot(PlotEntity plotEntity) throws ResponseException {
        //获取token信息
        String userId = TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        Integer num = trafficBuiltMapper.selectPlotIsExist(plotEntity.getTzName());
        if(num > 0){
            return RespMessage.getFailureMsg("该小区已存在");
        }
        //查询otId
        Integer otId = trafficBuiltMapper.queryOtId(plotEntity.getOtName());
        if(otId == null){
            return RespMessage.getFailureMsg("查询不到该交通小区类型");
        }
        ChangeEntity changeEntity = new ChangeEntity();
        //user id
        changeEntity.setUserId(Integer.valueOf(userId));
        //数据id
        int tzId = trafficBuiltMapper.getMaxPlotTableId() + 1;
        changeEntity.setcDataId(tzId);
        //数据类型
        changeEntity.setcType(1);
        //变更描述
        changeEntity.setcDes("新增交通小区");
        changeEntity.setcDateTime(DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
        //otName&&TzName&&tzCenterCoordinates&&tzScope
        changeEntity.setcContent(plotEntity.getOtName()+"&&"+plotEntity.getTzName()+"&&"+plotEntity.getTzCenterCoordinates()+"&&"+plotEntity.getTzScope());
        //新增 变更表
        trafficBuiltMapper.insertChange(changeEntity);

        plotEntity.setTzAudit(2);
        plotEntity.setTzFlag(1);
        plotEntity.setOtId(otId);
        trafficBuiltMapper.insertPlot(plotEntity);
        return RespMessage.getSuccessMsg("交通小区新增成功");
    }

    /**
     * 修改交通小区- 变更表
     * @param plotEntity
     * @return
     */
    @Override
    public RespMessage updatePlot(PlotEntity plotEntity,Integer id) throws ResponseException {
        int tzId = id;
        //获取token信息
        String userId = TrafficFlowODUtils.getToken(req, stringRedisTemplate);

        if(isPlotDelete(1,tzId,trafficBuiltMapper)){
            return RespMessage.getFailureMsg("该交通小区已被删除,请在审核列表中查看");
        }

        boolean[] type = equalsUserId(trafficBuiltMapper, Integer.valueOf(userId), tzId, 1);
        if(!type[0]){
            return RespMessage.getFailureMsg("已有用户修改该小区");
        }

        List<String> list = new ArrayList<>();
        if(plotEntity.getOtName() != null){
            //查询otId
            int otId = trafficBuiltMapper.queryOtId(plotEntity.getOtName());
            list.add("修改小区类型&&"+otId);
        }
        if(plotEntity.getTzName() != null){
            list.add("修改小区名称&&"+plotEntity.getTzName());
        }
        if(plotEntity.getTzCenterCoordinates() != null){
            list.add("修改小区中心坐标&&"+plotEntity.getTzCenterCoordinates());
        }
        if(plotEntity.getTzScope() != null){
            list.add("修改小区范围&&"+plotEntity.getTzScope());
        }

       ChangeEntity changeEntity = new ChangeEntity();
        for(String cDes : list){
            changeEntity.setcDes(cDes.split("&&")[0]);
            changeEntity.setcContent(cDes.split("&&")[1]);
            changeEntity.setcDateTime(DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
            changeEntity.setcDataId(tzId);
            changeEntity.setcType(1);
            changeEntity.setUserId(Integer.valueOf(userId));
            if(type[1]){
                //替换
                Map<String,Object> map = new HashMap<>();
                map.put("cType",1);
                map.put("cDataId",tzId);
                map.put("cDes",cDes.split("&&")[0]);
                map.put("cContent",cDes.split("&&")[1]);
                map.put("cDateTime",DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
                trafficBuiltMapper.updateChangeTable(map);
            }else{
                //Insert
                //变更表
                trafficBuiltMapper.insertChange(changeEntity);
            }

            changeEntity = new ChangeEntity();
        }

        //小区表 修改小区状态
        trafficBuiltMapper.updatePlotType(tzId);
        return RespMessage.getSuccessMsg("修改成功，请等待审核通过");
    }


    /**
     * 修改OD类型_变更表
     * @param ODTypeEntity
     * @return
     */
    @Override
    public RespMessage updateODType(ODTypeEntity ODTypeEntity,Integer id) throws ResponseException {
        //获取token信息
        String userId = TrafficFlowODUtils.getToken(req, stringRedisTemplate);

        if(isODTypeDelete(2,id,trafficBuiltMapper)){
            return RespMessage.getFailureMsg("该交通小区类型已被删除,请在审核列表中查看");
        }

        boolean[] type = equalsUserId(trafficBuiltMapper, Integer.valueOf(userId), id, 2);
        if(!type[0]){
            return RespMessage.getFailureMsg("已有用户修改该交通小区类型");
        }

        ChangeEntity changeEntity = new ChangeEntity();
        changeEntity.setcDes("修改交通小区类型");
        changeEntity.setcContent(ODTypeEntity.getOtName());
        changeEntity.setcDateTime(DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
        changeEntity.setcDataId(id);
        changeEntity.setcType(2);
        changeEntity.setUserId(Integer.valueOf(userId));
        if(type[1]){
            //替换
            Map<String,Object> map = new HashMap<>();
            map.put("cType",2);
            map.put("cDataId",id);
            map.put("cDes","修改交通小区类型");
            map.put("cContent",ODTypeEntity.getOtName());
            map.put("cDateTime",DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
            trafficBuiltMapper.updateChangeTable(map);
        }else{
            //Insert
            //变更表
            trafficBuiltMapper.insertChange(changeEntity);
        }

        //修改od表状态
        trafficBuiltMapper.updateODTableType(id);
        return RespMessage.getSuccessMsg("修改成功，请等待审核通过");
    }


    /**
     * 交通小区数据审核列表
     * @return
     */
    @Override
    public RespMessage auditPoltData() throws ResponseException {
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);

        List<Map<String, Object>> list = trafficBuiltMapper.selectAuditPlotData();
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        for(Map<String, Object> map : list){
//            if(map.get("cDes").toString().("新增")){
//                resultMap.put("tzName",map.get("cContent").toString().split("&&")[1]);
//                resultMap.put("flag",1);
//                resultMap.put("userName",map.get("userName"));
//                resultMap.put("cDes",map.get("cDes"));
//                resultList.add(resultMap);
//                resultMap = new HashMap<>();
//            }else{
                resultMap.put("id",map.get("tzId"));
//                resultMap.put("id",map.get("cId"));
//                resultMap.put("tzId",map.get("tzId"));
                resultMap.put("tzName",map.get("tzName"));
//                resultMap.put("flag",1);

                resultMap.put("flag",getFlag(map.get("cDes").toString()));
                resultMap.put("userName",map.get("userName"));
                resultMap.put("cDes",map.get("cDes"));

                resultList.add(resultMap);
                resultMap = new HashMap<>();
//            }
        }
        return RespMessage.getSuccessMsg(resultList);
    }


    /**
     * OD类型数据审核列表
     * @return
     */
    @Override
    public RespMessage auditODTypeData() throws ResponseException {
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);

        List<Map<String, Object>> list = trafficBuiltMapper.selectODTypeData();
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        for(Map<String, Object> map : list){
//            if(map.get("flag") == null){
//                resultMap.put("otName",map.get("cContent").toString());
//                resultMap.put("flag",1);
//                resultMap.put("userName",map.get("userName"));
//                resultMap.put("cDes",map.get("cDes"));
//                resultList.add(resultMap);
//                resultMap = new HashMap<>();
//            }else {
                resultMap.put("id",map.get("otId"));
//                resultMap.put("id",map.get("cId"));
//                resultMap.put("otId",map.get("otId"));
                resultMap.put("otName", map.get("otName"));
//                resultMap.put("flag", 1);
                resultMap.put("flag",getFlag(map.get("cDes").toString()));
                resultMap.put("userName", map.get("userName"));
                resultMap.put("cDes", map.get("cDes"));
                resultList.add(resultMap);
                resultMap = new HashMap<>();
//            }
        }

        return RespMessage.getSuccessMsg(resultList);
    }


    /**
     * 获取交通小区数据
     * @return
     */
    @Override
    public RespMessage getPlotsList(PlotEntity plotEntity) throws ResponseException {
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);

        Map<String, Object> paramMap = new HashMap<>();
        if(plotEntity.getOtName() != null){
            paramMap.put("otName",plotEntity.getOtName());
        }
        List<Map<String, Object>> plotsDetails = trafficBuiltMapper.getPlotsDetails(paramMap);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        for (Map<String, Object> map : plotsDetails){
//            resultMap.put("tzId", map.get("tzId"));
            resultMap.put("id", map.get("tzId"));
            resultMap.put("tzName", map.get("tzName"));
            resultMap.put("tzAudit", map.get("tzAudit"));
            resultMap.put("otName", map.get("otName"));
            resultMap.put("tzCenterCoordinates", map.get("tzCenterCoordinates"));
            resultMap.put("tzScope", map.get("tzScope"));
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }
        return RespMessage.getSuccessMsg(resultList);
    }

    /**
     * 获取OD类型列表数据
     * @return
     */
    @Override
    public RespMessage getODTypeList() throws ResponseException {
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        List<Map<String, Object>> odTypeDetails = trafficBuiltMapper.getODTypeDetails();
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        for (Map<String, Object> map : odTypeDetails){
//            resultMap.put("otId", map.get("otId"));
            resultMap.put("id", map.get("otId"));
            resultMap.put("otName", map.get("otName"));
            resultMap.put("otAudit", map.get("otAudit"));
            resultMap.put("otDescribe", map.get("otDescribe"));
            resultList.add(resultMap);
            resultMap = new HashMap<>();
        }
        return RespMessage.getSuccessMsg(resultList);
    }

    /**
     * 删除交通小区_变更表
     * @param plotEntity
     * @return
     */
    @Override
    public RespMessage deletePlot(PlotEntity plotEntity,Integer id) throws ResponseException {
        //获取token信息
        String userId = TrafficFlowODUtils.getToken(req, stringRedisTemplate);

        if(isPlotDelete(1,id,trafficBuiltMapper)){
            return RespMessage.getFailureMsg("该交通小区已被删除,请在审核列表中查看");
        }

        boolean type[] = equalsUserId(trafficBuiltMapper, Integer.valueOf(userId), id, 1);
        if(!type[0]){
            return RespMessage.getFailureMsg("已有用户修改该交通小区中,请在审核列表中查看");
        }

        ChangeEntity changeEntity = new ChangeEntity();
        changeEntity.setcDes("删除交通小区");
        changeEntity.setcDateTime(DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
        changeEntity.setcDataId(id);
        changeEntity.setcType(1);
        changeEntity.setUserId(Integer.valueOf(userId));

        //Insert
        //变更表
        trafficBuiltMapper.insertChange(changeEntity);

        //小区表
        trafficBuiltMapper.updatePlotTbale_delete(id);
        return RespMessage.getSuccessMsg();
    }

    /**
     * 删除OD类型 变更表
     * @param odTypeEntity
     * @return
     */
    @Override
    public RespMessage deletODType(ODTypeEntity odTypeEntity,Integer id) throws ResponseException {
        //获取token信息
        String userId = TrafficFlowODUtils.getToken(req, stringRedisTemplate);

        if(isODTypeDelete(2,id,trafficBuiltMapper)){
            return RespMessage.getFailureMsg("该交通小区类型已被删除,请在审核列表中查看");
        }
        boolean[] type = equalsUserId(trafficBuiltMapper, Integer.valueOf(userId), id, 2);
        if(!type[0]){
            return RespMessage.getFailureMsg("已有用户修改该交通小区类型,请在审核列表中查看");
        }
        ChangeEntity changeEntity = new ChangeEntity();
        changeEntity.setcDes("删除交通小区类型");
        changeEntity.setcDateTime(DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
        changeEntity.setcDataId(id);
        changeEntity.setcType(2);
        changeEntity.setUserId(Integer.valueOf(userId));
        //变更表
        trafficBuiltMapper.insertChange(changeEntity);
        //OD表
        trafficBuiltMapper.delteODTypeTbale_delete(id);
        return RespMessage.getSuccessMsg();
    }


    /**
     * 处理交通小区数据 审核过程
     * @param changeEntity
     * @return
     */
    @Override
    public RespMessage processPlotDataBefore(ChangeEntity changeEntity,Integer id) throws ResponseException {
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        Map<String, Object> plotMap = trafficBuiltMapper.idGetZoneDetails(id);
        Map<String, Object> map = new HashMap<>();
        map.put("otName",trafficBuiltMapper.queryOtName(Integer.valueOf(plotMap.get("otId")+"")));
        map.put("tzName",plotMap.get("tzName"));
        map.put("tzCenterCoordinates",plotMap.get("tzCenterCoordinates"));
        map.put("tzScope",plotMap.get("tzScope"));
        map.put("id",plotMap.get("tzId"));
        map.put("tzAudit",1);
        Map<String,Object> resultMap = new HashMap<>();
//        if(changeEntity.getcDes().contains("新增")){
//            //审核前
//            resultMap.put("before",null);
//            //审核后
//            resultMap.put("later",map);
//        }else if(changeEntity.getcDes().contains("修改")){
//            //审核前的数据
//            resultMap.put("before",map);
//
//            Map<String, Object> laterMap = new HashMap<>();
//            laterMap.put("otName",trafficBuiltMapper.queryOtName(Integer.valueOf(plotMap.get("otId")+"")));
//            laterMap.put("tzName",plotMap.get("tzName"));
//            laterMap.put("tzCenterCoordinates",plotMap.get("tzCenterCoordinates"));
//            laterMap.put("tzScope",plotMap.get("tzScope"));
//            laterMap.put("tzAudit",1);
//            laterMap.put("id",plotMap.get("tzId"));
//
//            Map<String, Object> paraMap = new HashMap<>();
//            paraMap.put("cDataId",id);///////////////////////
//            paraMap.put("cType",1);
//            if(changeEntity.getcDes().equals("修改小区类型")){
//
//                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
//                laterMap.put("otId",content);
//                laterMap.put("otName",trafficBuiltMapper.queryOtName(Integer.valueOf(content)));
//            }else if(changeEntity.getcDes().equals("修改小区名称")){
//                paraMap.put("cDes","修改小区名称");
//                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
//                laterMap.put("tzName",content);
//            }else if(changeEntity.getcDes().equals("修改小区中心坐标")){
//                paraMap.put("cDes","修改小区中心坐标");
//                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
//                laterMap.put("tzCenterCoordinates",content);
//            }else if(changeEntity.getcDes().equals("修改小区范围")){
//                paraMap.put("cDes","修改小区范围");
//                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
//                laterMap.put("tzScope",content);
//            }
//            //审核后
//            resultMap.put("later",laterMap);
//        }else if(changeEntity.getcDes().contains("删除")){
//            //获取该条小区数
//            Map<String, Object> plotsDetailsMap = trafficBuiltMapper.getPlotsDetails2(tzName);
//            //审核前
//            resultMap.put("before",plotsDetailsMap);
//            //审核后
//            resultMap.put("later",null);
//        }

        return RespMessage.getSuccessMsg(resultMap);
    }



    /**
     * 处理OD数据 审核过程
     * @param changeEntity
     * @return
     */
    @Override
    public RespMessage processODTyeDataBefore(ChangeEntity changeEntity,Integer otId) throws ResponseException {
        if(changeEntity.getcDes() == null){
            return RespMessage.getFailureMsg("操作描述(cDes)不能为空");
        }
        //获取token信息
        String userId = TrafficFlowODUtils.getToken(req, stringRedisTemplate);
//        Object userId = req.getSession().getAttribute("userId");
        if(userId == null){
            return RespMessage.getFailureMsg("请先登录");
        }
        Map<String,Object> resultMap = new HashMap<>();

//        if(changeEntity.getcDes().contains("新增")){
//            Map<String,Object> map = new HashMap<>();
//            map.put("otName",otName);
//            map.put("otAudit",1);
//            map.put("otFlag",1);
//            //审核前
//            resultMap.put("before",null);
//            //审核后
//            resultMap.put("later",map);
//        }else
            if(changeEntity.getcDes().contains("修改")){
            //获取该条OD数据
            Map<String, Object> odTypeDetails = trafficBuiltMapper.getOneODTypeDetails(otId);
            Map<String,Object> paraMap = new HashMap<>();
            paraMap.put("cType",2);
            paraMap.put("cDataId",otId);
            paraMap.put("cDes","修改交通小区类型");
            String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);

            Map<String,Object> otMap = new HashMap<>();
            otMap.put("otName",content);
//            otMap.put("otAudit",1);
            otMap.put("otFlag",2);
            //审核前
            resultMap.put("before",odTypeDetails);
            //审核后
            resultMap.put("later",otMap);
        }
//            else if(changeEntity.getcDes().contains("删除")){
//            //获取该条OD数据
//            Map<String, Object> odTypeDetails = trafficBuiltMapper.getOneODTypeDetails(otName);
//            //审核前
//            resultMap.put("before",odTypeDetails);
//            //审核后
//            resultMap.put("later",null);
//        }
        return RespMessage.getSuccessMsg(resultMap);
    }

    /**
     * 处理交通小区数据 审核通过
     * @param changeEntity
     * @return
     */
    @Override
    public RespMessage processPlotDataLater(ChangeEntity changeEntity,Integer tzId) throws ResponseException {
        if(changeEntity.getcDes() == null){
            return RespMessage.getFailureMsg("操作描述(cDes)不能为空");
        }
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        //insert
        if(changeEntity.getcDes().contains("新增")){
            trafficBuiltMapper.insertPlotTable_audit(tzId);
        }else if(changeEntity.getcDes().contains("修改")){
            Map<String,Object> plotMap = new HashMap<>();
            String cDes = changeEntity.getcDes();
//            Integer tzId = trafficBuiltMapper.selectPlotTzId(tzName);
            Map<String,Object> paraMap = new HashMap<>();
            paraMap.put("cType",1);
            paraMap.put("cDataId",tzId);
            if(cDes.equals("修改小区类型")){
                paraMap.put("cDes","修改小区类型");
                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
                plotMap.put("otId",content);
            }else if(cDes.equals("修改小区名称")){
                paraMap.put("cDes","修改小区名称");
                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
                plotMap.put("tzName",content);
            }else if(cDes.equals("修改小区中心坐标")){
                paraMap.put("cDes","修改小区中心坐标");
                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
                plotMap.put("tzCenterCoordinates",content);
            }else if(cDes.equals("修改小区范围")){
                paraMap.put("cDes","修改小区范围");
                String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
                plotMap.put("tzScope",content);
            }
            plotMap.put("tzId",tzId);
            plotMap.put("tzFlag",1);
            plotMap.put("tzAudit",1);
            trafficBuiltMapper.updatePlot(plotMap);
        }else if(changeEntity.getcDes().contains("删除")){
            //根据tzId删除
            trafficBuiltMapper.deletePlotTable_audit(tzId);
        }
        changeEntity.setcDataId(tzId);
        changeEntity.setcType(1);
        //删除变更表
        trafficBuiltMapper.delteChangeTbale(changeEntity);
        return RespMessage.getSuccessMsg();
    }


    /**
     * 处理交通小区数据 审核未通过
     * @param changeEntity
     * @return
     */
    @Override
    public RespMessage processPlotDataLater_false(ChangeEntity changeEntity,Integer tzId) throws ResponseException {
        if(changeEntity.getcDes() == null){
            return RespMessage.getFailureMsg("操作描述(cDes)不能为空");
        }
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        //insert
        if(changeEntity.getcDes().contains("新增")){
            trafficBuiltMapper.deletePlot_false(tzId);
        }else if(changeEntity.getcDes().contains("修改")){
            trafficBuiltMapper.insertPlotTable_audit(tzId);
        }else if(changeEntity.getcDes().contains("删除")){
            trafficBuiltMapper.insertPlotTable_audit(tzId);
        }
        changeEntity.setcDataId(tzId);
        changeEntity.setcType(1);
        //删除变更表
        trafficBuiltMapper.delteChangeTbale(changeEntity);
        return RespMessage.getSuccessMsg();
    }



    /**
     * 处理OD类型数据 审核通过
     * @param changeEntity
     * @return
     */
    @Override
    public RespMessage processODTyeDataLater(ChangeEntity changeEntity,Integer otId) throws ResponseException {
        if(changeEntity.getcDes() == null){
            return RespMessage.getFailureMsg("操作描述(cDes)不能为空");
        }
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        if(changeEntity.getcDes().contains("新增")){
            trafficBuiltMapper.insertODTypeTable_audit(otId);
        }else if(changeEntity.getcDes().contains("修改")){
            Map<String,Object> paraMap = new HashMap<>();
            paraMap.put("cType",2);
            paraMap.put("cDataId",otId);
            paraMap.put("cDes","修改交通小区类型");
            String content = trafficBuiltMapper.selectChangeData_cContent(paraMap);
            Map<String,Object> otMap = new HashMap<>();
            otMap.put("otName",content);
            otMap.put("otAudit",1);
            otMap.put("otFlag",1);
            otMap.put("otId",otId);
            trafficBuiltMapper.updateODType(otMap);
        }else if(changeEntity.getcDes().contains("删除")){
            //根据id删除
            trafficBuiltMapper.deleteODTypeTable_audit(otId);
            //删除该条od类型时，关联该od类型的小区全部删除
            trafficBuiltMapper.updatePlotType_ot(otId);
        }
        changeEntity.setcDataId(otId);
        changeEntity.setcType(2);
        //删除变更表
        trafficBuiltMapper.delteChangeTbale(changeEntity);
        return RespMessage.getSuccessMsg();
    }


    /**
     * 处理OD类型数据 审核未通过
     * @param changeEntity
     * @return
     */
    @Override
    public RespMessage processODTyeDataLater_false(ChangeEntity changeEntity,Integer otId) throws ResponseException {
        if(changeEntity.getcDes() == null){
            return RespMessage.getFailureMsg("操作描述(cDes)不能为空");
        }
        //获取token信息
        TrafficFlowODUtils.getToken(req, stringRedisTemplate);
        if(changeEntity.getcDes().contains("新增")){
            trafficBuiltMapper.deleteODType_false(otId);
        }else if(changeEntity.getcDes().contains("修改")){
            trafficBuiltMapper.insertODTypeTable_audit(otId);
        }else if(changeEntity.getcDes().contains("删除")){
            trafficBuiltMapper.insertODTypeTable_audit(otId);
        }
        changeEntity.setcDataId(otId);
        changeEntity.setcType(2);
        //删除变更表
        trafficBuiltMapper.delteChangeTbale(changeEntity);
        return RespMessage.getSuccessMsg();
    }


    /**
     * 判断数据是否存在
     * @param cDes
     * @param trafficBuiltMapper
     * @param reqUserId
     * @param dataId
     * @param cContent
     * @return
     */
    public static boolean[] isDataExits(String cDes, TrafficBuiltMapper trafficBuiltMapper, Integer reqUserId,Integer dataId,Object cContent,int cType){
        boolean flag = true;
        boolean addList = true;
        Map<String,Object> map = new HashMap<>();
        map.put("cType",cType);
        map.put("cDataId",dataId);
        map.put("cDes",cDes);
        Integer userId = trafficBuiltMapper.selectChange(map);
        if(userId != null){
            if(userId == reqUserId){
                //更新
                map.put("cContent",cContent);
                map.put("cDateTime",DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
                trafficBuiltMapper.updateChangeTable(map);
                addList = false;
            }else{
                //其它用户修改该条信息
                flag = false;
            }
        }
        return new boolean[]{flag,addList};
    }

    /**
     * 判断用户id 是否相同
     * @param trafficBuiltMapper
     * @param userId
     * @param dataId
     * @return
     */
    public static  boolean[] equalsUserId(TrafficBuiltMapper trafficBuiltMapper, Integer userId,Integer dataId,int cType){
        boolean flag = false;
        boolean flag1 = false;
        Map<String,Object> map = new HashMap<>();
        map.put("cType",cType);
        map.put("cDataId",dataId);
        List<Integer> userIdList = trafficBuiltMapper.selectChangUserId(map);
        //查询不到用户id时，说明没有修改
        if(userIdList.size() == 0){
            flag = true;
        }
        //有userId时，判断是否与当前userId相同
        for(Integer userId2 : userIdList){
            if(userId2 == userId){
                flag = true;
                flag1 = true;
            }
        }

        return new boolean[]{flag,flag1};
    }

    /**
     * 判断交通小区是否被删除
     * @param tzId
     * @param trafficBuiltMapper
     * @return
     */
    public static boolean isPlotDelete(Integer cType,Integer tzId,TrafficBuiltMapper trafficBuiltMapper){
        Map<String,Object> map = new HashMap<>();
        map.put("cType",cType);
        map.put("cDataId",tzId);
        map.put("cDes","删除交通小区");
        Integer userId = trafficBuiltMapper.selectChange(map);
        if(userId != null){
           return true;
        }
        return false;
    }

    /**
     * 判断交通小区类型是否被删除
     * @param otId
     * @param trafficBuiltMapper
     * @return
     */
    public static boolean isODTypeDelete(Integer cType,Integer otId,TrafficBuiltMapper trafficBuiltMapper){
        Map<String,Object> map = new HashMap<>();
        map.put("cType",cType);
        map.put("cDataId",otId);
        map.put("cDes","删除交通小区类型");
        Integer userId = trafficBuiltMapper.selectChange(map);
        if(userId != null){
            return true;
        }
        return false;
    }

    /**
     * 获取操作类型
     * @param cDes
     * @return
     */
    public static int getFlag(String cDes){
        int flag = 0;
        if(cDes.contains("新增")){
            flag = 1;
        }else if(cDes.contains("修改")){
            flag = 2;
        }else{
            flag = 3;
        }
        return flag;
    }
}
