package its.traffic.flow.mapper;

import its.traffic.flow.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * TrafficLoginMapper
 *
 * @author Lenovo
 * @version 1.0
 * 2020/6/3 15:40
 **/
@Mapper
public interface TrafficLoginMapper {
    //查询用户
    int findUserByName(User user);

    @Insert("insert into t_user(`userName`,`userPwd`) values(#{userName},#{password})")
    void insertUser(User user);

//    @Insert("insert into t_role(`roleId`,`roleName`) values(#{roleId},#{roleName})")
//    void insertUserRole(Map<String,Object> map);
    //查询用户权限id
    @Select("select roleId from t_role where roleName = #{roleName}")
    int selectUserRole(String roleName);

    //用户-权限对应表
    @Insert("insert into t_user_role(`userId`,`roleId`) values(#{userId},#{roleId})")
    void insertUserRole(Map<String,Object> map);

    //查询用户、密码
    int findUserByIdAndPassword(User user);

    Integer findUserById(User user);

    String findRoleName(Integer userId);

    List<Map<String,Object>> getPermissonList();
}
