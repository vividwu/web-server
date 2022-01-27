package com.vivid.framework.security;

import com.vivid.framework.security.config.AuthorizationServerAutoConfig;
import com.vivid.framework.security.config.SecurityConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Inherited
@EnableAuthorizationServer
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({AuthorizationServerAutoConfig.class,SecurityConfiguration.class})
public @interface EnableAuthorizationServer {
}
