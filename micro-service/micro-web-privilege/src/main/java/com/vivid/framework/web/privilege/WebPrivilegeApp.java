package com.vivid.framework.web.privilege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebPrivilegeApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(WebPrivilegeApp.class, args);
    }
}
