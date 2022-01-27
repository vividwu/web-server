package com.vivid.biz.testweb;

import com.vivid.framework.privilege.EnablePrivilege;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnablePrivilege  //("com.vivid.biz.testweb.controller")
public class TestWebApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(TestWebApp.class, args);
    }
}
