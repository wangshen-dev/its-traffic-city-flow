//package its.traffic.flow.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * 登录适配器
// * @author 遇见清晨
// * @date 创建时间： 2019年12月27日 上午11:08:02
// * @version 1.0
// * @user: Lenovo
// */
//
//@Configuration
//public class UserLoginAdapter implements WebMvcConfigurer {
//
//	@Autowired
//	private UserLoginInterceptor userLoginInterceptor;
//
//	public void addInterceptors(InterceptorRegistry registry) {
//
//		//添加对用户是否登录的拦截器，并添加过滤项、排除项
//		registry.addInterceptor(userLoginInterceptor).addPathPatterns("/**");
////			.excludePathPatterns("/traffic/flow/regist").excludePathPatterns("/traffic/flow/login");//排除登录页面
//
//	}
//
//}
