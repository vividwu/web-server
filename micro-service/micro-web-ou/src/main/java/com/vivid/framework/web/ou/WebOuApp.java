package com.vivid.framework.web.ou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebOuApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(WebOuApp.class, args);
    }

}
