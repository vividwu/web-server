package com.vivid.biz.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vivid.biz.flow.entity.ou.UserInfoEnt;
import com.vivid.biz.flow.repository.ou.UserInfoMapper;
import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.common.data.user.entity.OuUserEnt;
import com.vivid.framework.security.dto.SecurityUser;
import com.vivid.framework.security.service.UserInfoService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ouUserServiceImpl")
public class OuUserServiceImpl implements UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    Mapper dMapper;
    @Override
    public ResponseResult<OuUserEnt> getUserInfo(String userName) {
        UserInfoEnt userInfoEnt = userInfoMapper.selectOne(new QueryWrapper<UserInfoEnt>().lambda()
                .eq(UserInfoEnt::getName, userName));
        OuUserEnt ouUser = null;
        if (userInfoEnt != null) {
            ouUser = dMapper.map(userInfoEnt, OuUserEnt.class);
        }
        return ResponseResult.successed(ouUser);
    }

    @Override
    public boolean checkUsernamePassword(String userName, String password) {
        return true;
    }

    @Override
    public boolean createNewUser(SecurityUser sysUser) {
        return false;
    }
}
