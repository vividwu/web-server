package com.vivid.framework.security.service;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.common.data.user.entity.OuUserEnt;
import com.vivid.framework.security.dto.SecurityUser;
import com.vivid.framework.security.utils.SpringContextUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //TODO 此处给引入组件的客户端Boot去实现自己的个性化查询人员业务
        String beanId = "ouUserServiceImpl";
        UserInfoService userInfoService = SpringContextUtils.getBean(beanId);
        ResponseResult<OuUserEnt> result = userInfoService.getUserInfo(username);
        return getUserDetails(result);
    }

    /**
     * 构建userdetails
     *
     * @param result 用户信息
     * @return
     */
    private UserDetails getUserDetails(ResponseResult<OuUserEnt> result) {
        if (result == null || result.getData() == null) {
            return null;
            //throw new UsernameNotFoundException("用户不存在");
        }
        OuUserEnt info = result.getData();
        Map<String,Object> additional = new HashMap<>();
        additional.put("uid",result.getData().getId());
        additional.put("udisplay",result.getData().getDisplayName());
        additional.put("unum",result.getData().getNum());
        // 构造security用户
        return new SecurityUser(null,
                additional, info.getName(), info.getPassword(), true, true, true, true,
                /*info.getAuthorities() != null ? info.getAuthorities() : */AuthorityUtils.NO_AUTHORITIES);
    }
}
