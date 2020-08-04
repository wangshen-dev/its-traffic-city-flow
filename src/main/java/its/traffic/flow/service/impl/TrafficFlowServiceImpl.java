package its.traffic.flow.service.impl;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.User;
import its.traffic.flow.mapper.TrafficLoginMapper;
import its.traffic.flow.service.TrafficFlowService;
import its.traffic.flow.utils.TrafficFlowODUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * TrafficFlowServiceImpl
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/3 15:38
 **/
@Service
public class TrafficFlowServiceImpl implements TrafficFlowService {

    @Autowired
    private TrafficLoginMapper trafficLoginMapper;

    @Autowired
    private HttpServletRequest req;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 注册功能
     *
     * @param user
     * @return
     */
    @Override
    public RespMessage regist(User user) {

        int num = trafficLoginMapper.findUserByName(user);
        if (num > 0) {
            return RespMessage.getFailureMsg("当前用户名存在");
        }
        if (user.getPassword().length() > 50 || user.getUserName().length() > 50) {
            return RespMessage.getFailureMsg("用户名与密码长度不能超过50");
        }
        if (!user.getPassword().equals(user.getConPassword())) {
            return RespMessage.getFailureMsg("前后两次密码输入不同");
        }
        //注册成功，插入信息
        trafficLoginMapper.insertUser(user);
        Integer userId = trafficLoginMapper.findUserById(user);

        //查询普通用户id
        int roleId = trafficLoginMapper.selectUserRole("普通用户");
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", roleId);
        map.put("userId", userId);

        trafficLoginMapper.insertUserRole(map);
        return RespMessage.getSuccessMsg("注册成功");
    }

    /**
     * 登录功能
     *
     * @param user
     * @return
     */
    @Override
    public RespMessage login(User user) {

        int userNums = trafficLoginMapper.findUserByName(user);
        if (userNums == 0) {
            return RespMessage.getFailureMsg("当前用户名不存在");
        }
        int num = trafficLoginMapper.findUserByIdAndPassword(user);
        if (num == 0) {
            return RespMessage.getSuccessMsg("登录失败");
        }

        //查询该用户角色
        Integer userId = trafficLoginMapper.findUserById(user);
        //查询权限
        String roleName = trafficLoginMapper.findRoleName(userId);
//        req.getSession().setAttribute("roleName", roleName);
//        req.getSession().setAttribute("userId", userId);
        //存取uuid信息
        String uuId = TrafficFlowODUtils.createUUId();
        //存取userId,设置过期时间
        stringRedisTemplate.opsForValue().set(uuId,userId+"", 60*24, TimeUnit.SECONDS);
        Map<String,Object> map = new HashMap<>();
        map.put("userName",user.getUserName());
        map.put("roleName",roleName);
        map.put("token",uuId);
        map.put("userId",userId);
        return RespMessage.getSuccessMsg("登录成功", map);
    }

    @Override
    public RespMessage quit() {
        HttpSession session = req.getSession();
        session.removeAttribute("sessionTime");
        session.removeAttribute("roleName");
        return RespMessage.getSuccessMsg("已退出");
    }

    @Override
    public RespMessage getPermissonList() {
        List<Map<String, Object>> permissonList = trafficLoginMapper.getPermissonList();

        return RespMessage.getSuccessMsg(permissonList);
    }


}
