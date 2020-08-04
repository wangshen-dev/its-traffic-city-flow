package its.traffic.flow.config;//package its.baidu.cut.image.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 遇见清晨
 * @date 创建时间： 2019年12月17日 下午4:48:03
 * @version 1.0
 * @user: Lenovo
 */
@Configuration
public class CrossDomain implements WebMvcConfigurer {

	public void addCorsMappings(CorsRegistry registry) {
		// 跨越允许
		registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods("*");
	}
}
