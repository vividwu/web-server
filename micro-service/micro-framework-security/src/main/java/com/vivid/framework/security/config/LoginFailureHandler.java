package com.vivid.framework.security.config;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
        httpServletResponse.setStatus(401);
        //可以根据不同异常进行处理
        if (e instanceof UsernameNotFoundException){
            //log.info("【登录失败】"+exception.getMessage());
            httpServletResponse.getWriter().write("{\"success\":false,\"data\":null,\"message\":\"用户名不存在\"}");
            return;
        }
        if (e instanceof BadCredentialsException){
            httpServletResponse.getWriter().write("{\"success\":false,\"data\":null,\"message\":\"密码不正确\"}");
            return;
        }
        httpServletResponse.getWriter().write("{\"success\":false,\"data\":null,\"message\":\"登录失败\"}");
    }
}