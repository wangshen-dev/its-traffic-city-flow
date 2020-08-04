//package its.traffic.flow.filter;
//
//
//import its.traffic.flow.utils.DateTimeUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.util.NestedServletException;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//
///**
// * @ClassName LoginFilter
// * @Description 单点登陆认证过滤器
// * @Author yangchongshun
// * @Date 2019/5/15 10:35
// * @Version v1.0
// */
//@WebFilter(filterName = "loginFilter", urlPatterns = {"/*"})
//public class LoginFilter implements Filter {
//    private static final Log log = LogFactory.getLog(LoginFilter.class);
//
//
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        log.info("loginFilter初始化");
//    }
//
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        try {
//            cors(request, response);
//            String path = request.getRequestURI();
//
////            boolean b1 = true;
////            while (b1) {
////                if (path.lastIndexOf("/") == path.length() - 1) {
////                    path = path.substring(0, path.lastIndexOf("/"));
////                } else {
////                    b1 = false;
////                }
////            }
//
////            String lastPath = path.substring(path.lastIndexOf("/"), path.length());
////            //无需登录，就能访问的接口
////            if(lastPath.equals("/login") || lastPath.equals("/regist")){
////                filterChain.doFilter(servletRequest, servletResponse);
////                return;
////            }
////
////            //获取用户角色
////            Object roleName = request.getSession().getAttribute("roleName");
////            if(roleName == null){
////                response.setContentType("text/html;charset=utf-8");
////                PrintWriter out = response.getWriter();
////                out.println("{\"code\":501,\"msg\":\"请登录账号\"}");
////                return;
////            }
////            if(roleName.equals("超级用户")){
////                filterChain.doFilter(servletRequest, servletResponse);
////                return;
////            }
//
//
////            if (path.indexOf("/api/v1/yumma/app")==-1&&path.indexOf("/file")==-1) {
////                userNotNormal(request,response);
////                return;
////            }
//
//            String systemTime = DateTimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss");
//            long sessionTime = DateTimeUtils.getTimeStampByDate(systemTime, "yyyy-MM-dd HH:mm:ss");
////            System.out.println(request.getRequestedSessionId() +" -- " +request.getSession().getAttribute("sessionTime")+" --- "+sessionTime);
//            if( request.getSession().getAttribute("sessionTime") == null){
//                filterChain.doFilter(servletRequest, servletResponse);
//                request.getSession().setAttribute("sessionTime",sessionTime);
////                System.out.println("null: "+request.getRequestedSessionId());
//                return;
//            }
//            if(sessionTime - Long.valueOf( request.getSession().getAttribute("sessionTime")+"") >= 1000*60*30){
//                request.getSession().setAttribute("sessionTime",sessionTime);
//                userNotLgon(request, response);
//                return;
//            }
//            request.getSession().setAttribute("sessionTime",sessionTime);
//            filterChain.doFilter(servletRequest, servletResponse);
//        } catch (NestedServletException e) {
//            response.setContentType("text/html;charset=utf-8");
//            PrintWriter out = response.getWriter();
//            String msg = e.getMessage();
//            if (msg.indexOf("ResponseException: ") != -1) {
//                msg = msg.substring(msg.indexOf("ResponseException: ") + "ResponseException: ".length());
//            } else {
//                e.printStackTrace();
//                msg = "系统异常，请联系技术人员";
//            }
//            out.println("{\"code\":500,\"msg\":\"" + msg + "\"}");
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.setContentType("text/html;charset=utf-8");
//            PrintWriter out = response.getWriter();
//            out.println("{\"code\":500,\"msg\":\"系统异常，请联系技术人员\"}");
//            return;
//        }
//    }
//
//    @Override
//    public void destroy() {
//        log.info("loginFilter销毁");
//
//    }
//
//    private void userNotNormal(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        PrintWriter out = response.getWriter();
//        out.println("{\"code\":501,\"msg\":\"您不是超级管理员用户\"}");
//    }
//
//
//
//    private void userNotLgon(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HttpSession session = request.getSession();
//        session.removeAttribute("sessionTime");
//        session.removeAttribute("roleName");
//        response.setContentType("text/html;charset=utf-8");
//        PrintWriter out = response.getWriter();
//        out.println("{\"code\":501,\"msg\":\"由于您已长时间没有操作，请重新登陆\"}");
//        return;
//    }
//
//
//    private String getSsoToken(HttpServletRequest request) {
//        String ssoToken = null;
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (int i = 0; i < cookies.length; i++) {
//                if ("ssoToken".equals(cookies[i].getName())) {
//                    ssoToken = cookies[i].getValue();
//                    break;
//                }
//            }
//        }
//        return ssoToken;
//    }
//
//    private void cors(HttpServletRequest request, HttpServletResponse response) {
//        String allowOrigin = request.getHeader("Origin");
//        String allowMethods = "GET,PUT, POST, DELETE";
//        String allowHeaders = "Origin,No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified,Cache-Control, Expires, Content-Type, X-E4M-With";
//        response.addHeader("Access-Control-Allow-Credentials", "true");
//        response.addHeader("Access-Control-Allow-Headers", allowHeaders);
//        response.addHeader("Access-Control-Allow-Methods", allowMethods);
//        response.addHeader("Access-Control-Allow-Origin", allowOrigin);
//        response.addHeader("Access-Control-Max-Age", "3600");//30 min
//
//
////        response.addHeader("Access-Control-Allow-Origin", "*");
////        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
////        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
////        response.addHeader("Access-Control-Max-Age", "1800");//30 min
//    }
//
//
//
//
//}
