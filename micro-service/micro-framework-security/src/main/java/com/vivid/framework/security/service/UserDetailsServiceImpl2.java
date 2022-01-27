package com.vivid.framework.security.service;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.common.data.user.entity.OuUserEnt;
import com.vivid.framework.security.dto.SecurityUser;
import com.vivid.framework.security.utils.SecurityUtils;
import com.vivid.framework.security.utils.SpringContextUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Deprecated
public class UserDetailsServiceImpl2 ///implements UserDetailsService
{

    ///@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //TODO 此处给引入组件的客户端Boot去实现自己的个性化查询人员业务
        String beanId = "ouUserServiceImpl";
        String clientId = null;
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication != null) {
            clientId = authentication.getName();
        } else {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if ("/oauth/authorize".equals(request.getServletPath())) {
                clientId = request.getParameter("client_id");
                if (clientId == null || clientId.isEmpty()) {
                    //throw new InvalidRequestException("未找打到 client ID");
                }
            }
        }
//        if (!StringUtils.isEmpty(clientId)) {
//            ClientDetails clientDetails = redisClientDetailsService.loadClientByClientId(clientId);
//            if (clientDetails != null && clientDetails.getAdditionalInformation() != null) {
//                Object remoteUserInfoServiceBeanId =
//                        clientDetails.getAdditionalInformation().get("userInfoService");  //客户端用户登录获取用户信息 userInfoService
//                if (remoteUserInfoServiceBeanId != null) {
//                    beanId = remoteUserInfoServiceBeanId.toString();
//                }
//            }
//        }
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
            throw new UsernameNotFoundException("用户不存在");
        }
        OuUserEnt info = result.getData();
        // 构造security用户
        return new SecurityUser(null,
                null, info.getNum(), info.getPassword(), true, true, true, true,
                /*info.getAuthorities() != null ? info.getAuthorities() : */AuthorityUtils.NO_AUTHORITIES);
    }
}
