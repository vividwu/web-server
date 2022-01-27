package com.vivid.framework.privilege;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cfp")
public class PrivilegeConfigProperties {
    private boolean open;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    private String serverName;
    private String path;
    private String checkServiceImpl;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCheckServiceImpl() {
        return checkServiceImpl;
    }

    public void setCheckServiceImpl(String checkServiceImpl) {
        this.checkServiceImpl = checkServiceImpl;
    }

}
