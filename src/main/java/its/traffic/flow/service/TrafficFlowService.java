package its.traffic.flow.service;

import its.traffic.flow.RespMessage;
import its.traffic.flow.entity.User;

/**
 * TrafficFlowService
 *
 * @author wangshen
 * @version 1.0
 * 2020/6/3 15:33
 **/
public interface TrafficFlowService {

    /**
     * 注册功能
     * @param user
     * @return
     */
    RespMessage regist(User user);

    /**
     * 登录功能
     * @param user
     * @return
     */
    RespMessage login(User user);

    /**
     * 退出功能
     * @return
     */
    RespMessage quit();

    /**
     * 获取权限列表
     * @return
     */
    RespMessage getPermissonList();
}
