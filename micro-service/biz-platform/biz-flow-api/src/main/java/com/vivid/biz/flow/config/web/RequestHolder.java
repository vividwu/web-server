package com.vivid.biz.flow.config.web;

import com.github.pagehelper.PageHelper;

import java.util.Map;

public class RequestHolder {
    private static final ThreadLocal<PagerHolder> threadLocal = new ThreadLocal();

    public static void setPage(int page, int size) {
        //PageHelper.startPage(page, size);
        threadLocal.set(new PagerHolder(page,size));
    }
    public static PagerHolder getPage() {
        return threadLocal.get();
    }
}