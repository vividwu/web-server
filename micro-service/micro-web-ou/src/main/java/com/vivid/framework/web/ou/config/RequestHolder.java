package com.vivid.framework.web.ou.config;

import com.github.pagehelper.PageHelper;

public class RequestHolder {
    //private static final ThreadLocal<RoleHeader> threadLocal = new ThreadLocal();

    public static void setPage(int page, int size) {
        PageHelper.startPage(page, size);
    }

//    public static void setRole(RoleHeader role) {
//        threadLocal.set(role);
//    }
//
//    public static RoleHeader getRole() {
//        RoleHeader role = threadLocal.get();
//        if(role == null)
//            return new RoleHeader();
//        return role;
//    }
//
//    public static void clear() {
//        threadLocal.remove();
//    }
}