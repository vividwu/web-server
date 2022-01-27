package com.vivid.framework.privilege;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("spring.cloud.nacos.discovery")
public class NacosProperties {
    private String serverAddr;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    @PostConstruct
    public void init() throws SocketException {
        //重写env
    }
}
