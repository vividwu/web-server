package com.vivid.framework.security.config;

import com.vivid.framework.security.dto.SecurityUser;
import com.vivid.framework.security.utils.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

//@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        // 获取登录用户信息
        SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();
        // 生成token
        Map<String, Object> jwtContent = new HashMap<>();
        jwtContent.put("uid", userDetails.getAdditionalInformation().get("uid"));
        jwtContent.put("uname", userDetails.getUsername());
        jwtContent.put("udisplay", userDetails.getAdditionalInformation().get("udisplay"));
        jwtContent.put("unum", userDetails.getAdditionalInformation().get("unum"));
        jwtContent.put("exp", LocalDateTime.now().plusHours(2).toEpochSecond(ZoneOffset.UTC));
        String jwtToken = null;
        //log.info("生成jwt token");
            jwtToken = JwtTokenUtils.createToken(jwtContent);
            //log.info("jwt token:{}", jwtToken);
        //log.info("get cookie{}", httpServletRequest.getCookies());
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
        String json = "{\"success\":true,\"data\":{\"access_token\":\"" + jwtToken + "\"},\"message\":null}";
        httpServletResponse.getWriter().write(json);
    }
}