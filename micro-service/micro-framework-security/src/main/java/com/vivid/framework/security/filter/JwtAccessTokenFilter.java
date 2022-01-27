package com.vivid.framework.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivid.framework.security.dto.SecurityUser;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * JWT接口请求校验拦截器
 * 请求接口时会进入这里验证Token是否合法和过期
 */
public class JwtAccessTokenFilter extends BasicAuthenticationFilter {
    public JwtAccessTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        logger.info("------资源服务器认证入口------");
//        // ----- /erp-api/open/emp/list/join_date/2019-07-25
//        if(request.getRequestURI().contains("/open/") || request.getRequestURI().contains("/skip/")) {
//            logger.info("------跳过强制身份验证路径------");
//            filterChain.doFilter(request, response);
//            return;
//        }
        // 获取请求头中JWT的Token
        String tokenHeader = request.getHeader("Authorization");
        if (null!=tokenHeader && tokenHeader.startsWith("bearer")) {
            try {
                // 截取JWT前缀
                String token = tokenHeader.replace("bearer ", "");
                // 解析JWT
                String publicKey = JwtTokenClient.getPublicKey();
                org.springframework.security.jwt.Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
                // 获取用户名
                String claims = jwt.getClaims();
                Map<String,Object> userMap = new ObjectMapper().readValue(claims, Map.class);
                String userId = userMap.get("uid").toString();
                String username = userMap.get("uname").toString();
                if(!StringUtils.isEmpty(username)&&!StringUtils.isEmpty(userId)) {
                    //组装参数
                    SecurityUser user = new SecurityUser(null,
                            userMap, username, "123", true, true, true, true,
                            /*info.getAuthorities() != null ? info.getAuthorities() : */AuthorityUtils.NO_AUTHORITIES);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, username, AuthorityUtils.NO_AUTHORITIES);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                String warningStr = "{\"error\":\"invalid_token\",\"error_description\":\"缺少Authorization头信息\",\"error_description\":\"660001\"}";
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(warningStr);
                return;
            }
        } else {
            System.out.println("没有携带头信息，放行，在SecurityConfiguration中配置了UnAuthAuthenticationEntryPoint");
        }
        filterChain.doFilter(request, response);
        return;
    }
}