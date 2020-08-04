package its.traffic.flow.config;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//import its.baidu.cut.image.util.DateUtil;
//import its.baidu.cut.image.util.RedisUtil;
//import net.sf.json.JSONObject;

/**
 *   登录拦截
 * @author 遇见清晨
 * @date 创建时间： 2019年12月27日 上午11:05:01
 * @version 1.0
 * @user: Lenovo
 */

@Component
public class UserLoginInterceptor implements HandlerInterceptor {

//	@Resource
//	private RedisUtil redisUtil;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String token = request.getHeader("token");
		if(token == null) {//未登录
			response.getWriter().print("请登录");
			response.sendRedirect(request.getContextPath()+"/login.html");
			return false;
		}
		String value = stringRedisTemplate.opsForValue().get(token);
		if(value == null){
			response.getWriter().print("请登录");
			response.sendRedirect(request.getContextPath()+"/login.html");
			return false;
		}
		return true;
//		System.out.println(DateUtil.getNowTime() +" service get token is ["+token+"]");
//		// token 检测查看当前句柄是否存在缓存
//		if(redisUtil.hasKey(token)) {//已登录
//			return true;
//		} else {//未登录
//			//直接重定向到登录页面
////			response.sendRedirect(request.getContextPath()+"/login.html");
//			// 前后端分离
//			response.setContentType("text/html;charset=UTF-8");
//			JSONObject json = new JSONObject();
//			json.put("code", 403);
//			json.put("msg", "未登录");
//			response.getWriter().print(json);
//
//
//			return false;
//		}
	}

}
