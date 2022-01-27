package com.vivid.framework.security.config;

import com.vivid.framework.security.controller.AuthController;
import com.vivid.framework.security.service.UserDetailsServiceImpl;
import com.vivid.framework.security.utils.JwtTokenUtils;
import com.vivid.framework.security.utils.SpringContextUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

public class AuthorizationServerAutoConfig {
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
    }

    @Bean
    public LoginProvider loginProvider(){
        return new LoginProvider();
    }
    @Bean
    public LoginSuccessHandler loginSuccessHandler(){
        return new LoginSuccessHandler();
    }
    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }
    @Bean
    public UnAuthAuthenticationEntryPoint unAuthAuthenticationEntryPoint(){
        return new UnAuthAuthenticationEntryPoint();
    }
    @Bean
    public SpringContextUtils springContextUtils() {
        return new SpringContextUtils();
    }
    @Bean
    public AuthController authController(){
        return new AuthController();
    }

}
