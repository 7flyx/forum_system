package com.fly.demo.interceptor;

import com.fly.demo.config.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${fly-forum.login.url}")
    private String defaultURL;

    /**
     *  前置处理（预处理）
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return true：继续流程。false：表示流程中断
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取session对象
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AppConfig.USER_SESSION) != null) {
            // 用户已经登录, 校验通过
            return true;
        }
        if (defaultURL.startsWith("/")) { // 有没有 / 这个
            defaultURL = "/" + defaultURL;
        }
        // 校验不通过，跳转到登录页面
        response.sendRedirect(defaultURL);
        return false;
    }
}
