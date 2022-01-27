//package com.vivid.biz.flow.config;//package com.vivid.framework.security.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) //开启权限注解,默认是关闭的
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
////    @Autowired
////    LoginProvider loginProvider;
////    @Autowired
////    private VividSecurityProperties vividSecurityProperties;
////    @Autowired
////    protected UnAuthAuthenticationEntryPoint unauthAuthenticationEntryPoint;
////    @Autowired
////    SecurityAuthenticationEntryPoint authenticationEntryPoint;  //未登录
////    @Autowired
////    SecurityAuthenticationSuccessHandler authenticationSuccessHandler;
////    @Autowired
////    SecurityAuthenticationFailureHandler authenticationFailureHandler;
////    /**
////     * 解决 无法直接注入 AuthenticationManager
////     */
////    @Bean
////    @Override
////    public AuthenticationManager authenticationManagerBean() throws Exception {
////        return super.authenticationManagerBean();
////    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http.cors()
////                //关闭csrf，便于测试
////                .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .and()
////                .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
////                .and()
////                .authorizeRequests()
////                .anyRequest().authenticated()
////
////                // 开启form表单登录
////                .and()
////                .formLogin()
////                .permitAll();
////        // 当鉴权失败时，不返回html页面，而是返回403状态码
////        http.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
////        //http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
////        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.authorizeRequests()
//                //不进行权限验证的请求或资源(从配置文件中读取)
//                ///.antMatchers(JWTConfig.antMatchers.split(",")).permitAll()
//                //其他的需要登陆后才能访问
//                .anyRequest().authenticated()
//                .and()
//                //配置未登录自定义处理类
//                //.httpBasic().authenticationEntryPoint(authenticationEntryPoint)
//                //.and()
//                //配置登录地址
//                .formLogin().loginPage("/loginPage")
//                .loginProcessingUrl("/login/userLogin")
//                //配置登录成功自定义处理类
////                .successHandler(authenticationSuccessHandler)
////                //配置登录失败自定义处理类
////                .failureHandler(authenticationFailureHandler)
//                .and()
//                //配置登出地址
//                .logout()
//                .logoutUrl("/login/userLogout").permitAll()
//                //配置用户登出自定义处理类
//                ///.logoutSuccessHandler(userLogoutSuccessHandler)
//                .and()
//                //配置没有权限自定义处理类
//               ///.exceptionHandling().accessDeniedHandler(userAuthAccessDeniedHandler)
//                ///.and()
//                // 开启跨域
//                .cors()
//                .and()
//                // 取消跨站请求伪造防护
//                .csrf().disable();
//        // 基于Token不需要session
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        // 禁用缓存
//        http.headers().cacheControl();
//        // 添加JWT过滤器
//        ///http.addFilter(new JWTAuthenticationTokenFilter(authenticationManager()));
//    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/static/**", "/favicon.ico");
//    }
//
//    /**
//     * 身份认证接口
//     */
////    @Override
////    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////       //auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
////        auth.authenticationProvider(loginProvider);
////    }
//
////    @Bean
////    LoginFilter loginFilter() throws Exception {
////        LoginFilter loginFilter = new LoginFilter();
////        loginFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
////        loginFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
////        loginFilter.setFilterProcessesUrl("/login2");
////        //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
////        loginFilter.setAuthenticationManager(authenticationManagerBean());
////        return loginFilter;
////    }
//}
