package com.vivid.biz.flow.config.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class HttpInterceptor implements HandlerInterceptor {
    private static final String PAGE_NUM = "page_num";
    private static final String PAGE_SIZE = "page_size";

    private static final String DT_PAGE_NUM = "iDisplayStart";
    private static final String DT_PAGE_SIZE = "iDisplayLength";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("do filter, {}, {}", Thread.currentThread().getId(), request.getServletPath());
        //当前线程的id
        int page = 1;
        int size = 10;
        if (StringUtils.isNumeric(request.getParameter(DT_PAGE_NUM))) {
            page = Integer.parseInt(request.getParameter(DT_PAGE_NUM))/Integer.parseInt(request.getParameter(DT_PAGE_SIZE)) + 1;
        }
        if (StringUtils.isNumeric(request.getParameter(DT_PAGE_SIZE))) {
            size = Integer.parseInt(request.getParameter(DT_PAGE_SIZE));
        }

        if (StringUtils.isNumeric(request.getParameter(PAGE_NUM))) {
            page = Integer.parseInt(request.getParameter(PAGE_NUM));
        }
        if (StringUtils.isNumeric(request.getParameter(PAGE_SIZE))) {
            size = Integer.parseInt(request.getParameter(PAGE_SIZE));
        }
        RequestHolder.setPage(page, size);  //默认不开启分页
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
