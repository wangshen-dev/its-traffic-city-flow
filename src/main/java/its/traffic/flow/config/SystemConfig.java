//package its.traffic.flow.config;
//
//import org.apache.catalina.connector.Connector;
//
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.context.annotation.Configuration;
//
///**
// * 配置Spring boot支持在查询参数中加{}[]字符。
// * @author elon
// * @version 2019年1月6日
// */
//@Configuration
//public class SystemConfig {
//
//    public TomcatServletWebServerFactory webServerFactory() {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory ();
//        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
//
//            @Override
//            public void customize(Connector connector) {
//                connector.setProperty("relaxedQueryChars", "[]{}");
//            }
//        });
//
//        return factory;
//    }
//}
