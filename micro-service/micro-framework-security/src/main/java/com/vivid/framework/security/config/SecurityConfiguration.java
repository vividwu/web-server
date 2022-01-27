package com.vivid.framework.security.config;

import com.vivid.framework.security.filter.JwtAccessTokenFilter;
import com.vivid.framework.security.utils.SpringContextUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//see https://www.jianshu.com/p/62d50f0da181
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启权限注解,默认是关闭的
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Autowired
    LoginProvider loginProvider;
//    @Autowired
//    protected UnAuthAuthenticationEntryPoint unauthAuthenticationEntryPoint;
//    @Autowired
//    SecurityAuthenticationEntryPoint authenticationEntryPoint;  //未登录
      //自定义登录成功处理器
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    //自定义登录失败处理器
    @Autowired
    LoginFailureHandler loginFailureHandler;
    //自定义未登录处理器
    @Autowired
    UnAuthAuthenticationEntryPoint unAuthAuthenticationEntryPoint;
//    /**
//     * 解决 无法直接注入 AuthenticationManager
//     */
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//    private ApplicationContext applicationContext;
//    @Override
//    public void setApplicationContext(ApplicationContext var1) throws BeansException {
//        this.applicationContext = var1;
//        //SpringContextUtils.setApplicationContext(applicationContext);
//    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.cors()
//                //关闭csrf，便于测试
//                .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
//                .and()
//                .authorizeRequests()
//                .anyRequest().authenticated()
//
//                // 开启form表单登录
//                .and()
//                .formLogin()
//                .permitAll();
//        // 当鉴权失败时，不返回html页面，而是返回403状态码
//        http.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
//        //http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
//        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers("/signIn", "/404", "/oauth/token_key","/oauth/extract_token","/**/open/**").permitAll()
                //不进行权限验证的请求或资源(从配置文件中读取)
                ///.antMatchers(JWTConfig.antMatchers.split(",")).permitAll()
                //其他的需要登陆后才能访问
                .anyRequest().authenticated()
                .and()
                //配置未登录自定义处理类
                .httpBasic().authenticationEntryPoint(unAuthAuthenticationEntryPoint)
                .and()
                //配置登录地址
                .formLogin()//.loginPage("/loginPage") 不使用页面登录
                .loginProcessingUrl("/oauth/token")
                //配置登录成功自定义处理类
                .successHandler(loginSuccessHandler)
                //配置登录失败自定义处理类
                .failureHandler(loginFailureHandler)
                .and()
                //配置登出地址
                .logout()
                .logoutUrl("/login/userLogout").permitAll()
                //配置用户登出自定义处理类
                ///.logoutSuccessHandler(userLogoutSuccessHandler)
                .and()
                //配置没有权限自定义处理类
                ///.exceptionHandling().accessDeniedHandler(authAccessDeniedHandler)
                ///.and()
                // 开启跨域
                .cors()
                .and()
                // 取消跨站请求伪造防护
                .csrf().disable();
        // 基于Token不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 禁用缓存
        http.headers().cacheControl();
        // todo 添加JWT过滤器，此处和登录分离后的验证客户端后需要注释掉！！！
        http.addFilter(new JwtAccessTokenFilter(authenticationManager()));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**", "/favicon.ico");
    }

    /**
     * 身份认证接口
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       //auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationProvider(loginProvider);  //启用自定义的登陆验证逻辑
    }

//    @Bean
//    LoginFilter loginFilter() throws Exception {
//        LoginFilter loginFilter = new LoginFilter();
//        loginFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
//        loginFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
//        loginFilter.setFilterProcessesUrl("/login2");
//        //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
//        loginFilter.setAuthenticationManager(authenticationManagerBean());
//        return loginFilter;
//    }
}
