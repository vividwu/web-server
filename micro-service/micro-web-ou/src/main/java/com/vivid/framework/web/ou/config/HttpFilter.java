package com.vivid.framework.web.ou.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class HttpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.debug("do filter, {}, {}", Thread.currentThread().getId(), request.getServletPath());
        //当前线程的id
        int page = 1;
        int size = 5;
        if (StringUtils.isNumeric(request.getParameter("page_num"))) {
            page = Integer.parseInt(request.getParameter("page_num"));
        }
        if (StringUtils.isNumeric(request.getParameter("page_size"))) {
            size = Integer.parseInt(request.getParameter("page_size"));
        }
        RequestHolder.setPage(page, size);
//        RoleHeader rd = new RoleHeader();
//        rd.setRoleId(request.getHeader("role_id"));
//        rd.setCyId(request.getHeader("account_id"));
//        RequestHolder.setRole(rd);
//        if(rd.getWxId() == null) {
//            HttpServletResponse response = (HttpServletResponse)servletResponse;
//            response.sendError(401,"[account_id]用户身份参数缺失");
//            //response.setCharacterEncoding("utf-8");
//            //response.getWriter().write("{\"result\":false,\"message\":\"[wx_id]用户身份参数缺失\"}");
//            return;
//        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
