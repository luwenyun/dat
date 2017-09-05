package com.lwy.dat.interceptor;/**
 * Created by lwy on 2017/6/4.
 */

import com.lwy.dat.controller.*;
import com.lwy.dat.pojo.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * interceptor login
 *
 * @author 陆文云
 * @create 2017-06-04 14:53
 **/
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url=request.getRequestURI();
        System.out.println("url:"+url);
        if(url.endsWith("login.html")||url.endsWith("reg.html")||url.endsWith("home.html")){
            return true;
        }
        User user=(User)request.getSession().getAttribute("user");
        if(user==null){
            response.sendRedirect(request.getContextPath()+"/login.html");
            return false;
        }
//        User user=new User();
//        user.setUserName("1623905995");
        System.out.println("username:"+user.getUserName());
        UploadFileController.username=user.getUserName();
        UserController.username=user.getUserName();
        FileController.username=user.getUserName();
        ChartControlller.username=user.getUserName();
        FolderController.username=user.getUserName();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
