package com.vivid.framework.security.config;
import com.vivid.framework.security.service.UserDetailsServiceImpl;
import com.vivid.framework.security.utils.UserServiceUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


//@Slf4j
@Component
public class LoginProvider implements AuthenticationProvider {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
    @Override  //client_id
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        //log.info("username{}",username);
        String password = authentication.getCredentials().toString();
        //log.info("password{}",password);
        //log.info("username:{}",user.getUsername());
        //登录查询，成功的话返回用户信息，不成功则抛出异常
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        if (!ENCODER.matches(password,userDetails.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}