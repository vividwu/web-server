package com.vivid.framework.privilege;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import java.io.IOException;
//在身份拦截器之后执行，否则获取不到用户上下文
public class PrivilegeCheckFilter extends OncePerRequestFilter {

    IPrivilegeCheck privilegeCheck;
    PrivilegeCheckFilter(IPrivilegeCheck privilegeCheck){
        this.privilegeCheck = privilegeCheck;
    }
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(javax.servlet.http.HttpServletRequest httpServletRequest, javax.servlet.http.HttpServletResponse httpServletResponse, javax.servlet.FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return;
        }
        String userName = auth.getName();
        //获取配置中心的权限服务列表
        privilegeCheck.check4User(userName);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}