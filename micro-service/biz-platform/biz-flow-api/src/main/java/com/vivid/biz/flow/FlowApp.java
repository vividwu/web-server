package com.vivid.biz.flow;

import com.vivid.framework.security.EnableAuthorizationServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
@SpringBootApplication
@EnableAuthorizationServer
public class FlowApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(FlowApp.class, args);
    }
}
