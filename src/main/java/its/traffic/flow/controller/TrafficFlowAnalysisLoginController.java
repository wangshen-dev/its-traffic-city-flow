package its.traffic.flow.controller;


import its.traffic.flow.RespMessage;
import its.traffic.flow.ResponseException;
import its.traffic.flow.entity.User;
import its.traffic.flow.service.TrafficFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * TrafficFlowAnalysisController
 * 登录、权限模块
 * @author wangshen
 * @version 1.0
 * 2020/6/3 15:34
 **/
@Controller
@RequestMapping("/traffic/flow")
public class TrafficFlowAnalysisLoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrafficFlowAnalysisLoginController.class);


    @Autowired
    TrafficFlowService trafficFlowService;

    /**
     * 注册接口
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("/regist")
    public RespMessage regist(User user){
        try{
            if(StringUtils.isEmpty(user.getUserName())){
                throw new ResponseException("用户名不能为空");
            }
            if(StringUtils.isEmpty(user.getPassword())){
                throw new ResponseException("登录密码不能为空");
            }
            if(StringUtils.isEmpty(user.getConPassword())){
                throw new ResponseException("确认密码不能为空");
            }
            return trafficFlowService.regist(user);
        }catch (ResponseException e){
            return RespMessage.getFailureMsg(e.getMessage());
        }catch (Exception e){
            LOGGER.error(e.toString());
            e.printStackTrace();
            return RespMessage.getFailureMsg("查询接口异常，请联系后台管理员查看");
        }
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("/login")
    public RespMessage login(User user){

        try{
            if(StringUtils.isEmpty(user.getUserName())){
                return RespMessage.getFailureMsg("用户名不能为空");
            }
            if(StringUtils.isEmpty(user.getPassword())){
                throw new ResponseException("登录密码不能为空");
            }

            return trafficFlowService.login(user);
        }catch (ResponseException e){
            return RespMessage.getFailureMsg(e.getMessage());
        }catch (Exception e){
            LOGGER.error(e.toString());
            e.printStackTrace();
            return RespMessage.getFailureMsg("查询接口异常，请联系后台管理员查看");
        }
    }

    /**
     * 退出
     * @return
     */
    @ResponseBody
    @RequestMapping("/quit")
    public RespMessage quit(){
        try{
            return trafficFlowService.quit();
        }catch (Exception e){
            LOGGER.error(e.toString());
            e.printStackTrace();
            return RespMessage.getFailureMsg("查询接口异常，请联系后台管理员查看");
        }
    }

    /**
     * 获取权限列表
     */
    @ResponseBody
    @RequestMapping("/getPermissonList")
    public RespMessage getPermissonList(){
        try{
            return trafficFlowService.getPermissonList();
        }catch (Exception e){
            LOGGER.error(e.toString());
            e.printStackTrace();
            return RespMessage.getFailureMsg("查询接口异常，请联系后台管理员查看");
        }
    }




}
