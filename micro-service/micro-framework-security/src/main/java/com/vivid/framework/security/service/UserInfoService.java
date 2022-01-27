package com.vivid.framework.security.service;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.common.data.user.entity.OuUserEnt;
import com.vivid.framework.security.dto.SecurityUser;

import java.util.List;
//提供给Jar的引用端实现
public interface UserInfoService {
    ResponseResult<OuUserEnt> getUserInfo(String userName);

    boolean checkUsernamePassword(String userName, String password);

    boolean createNewUser(SecurityUser sysUser);
}