package com.vivid.framework.privilege.nacos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInfoDto {
    private List<InstanceDto> hosts = new ArrayList();

    public List<InstanceDto> getHosts() {
        return hosts;
    }

    public void setHosts(List<InstanceDto> hosts) {
        this.hosts = hosts;
    }
}
