package com.vivid.framework.security.utils;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.common.data.user.entity.OuUserEnt;
import com.vivid.framework.security.dto.SecurityUser;
import com.vivid.framework.security.service.UserInfoService;

public class UserServiceUtils {
    private static final  String beanId = "ouUserServiceImpl";
    public static ResponseResult<OuUserEnt> getUserInfo(String username){
        UserInfoService userInfoService = SpringContextUtils.getBean(beanId);
        ResponseResult<OuUserEnt> result = userInfoService.getUserInfo(username);
        return result;
    }

    public static boolean checkUsernamePassword(String userName, String password){
        UserInfoService userInfoService = (UserInfoService)SpringContextUtils.getApplicationContext().getBean(beanId);
        return userInfoService.checkUsernamePassword(userName,password);
    }

    public static boolean createNewUser(SecurityUser sysUser){
        UserInfoService userInfoService = SpringContextUtils.getBean(beanId);
        return userInfoService.createNewUser(sysUser);
    }
}
