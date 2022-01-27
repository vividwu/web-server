package com.vivid.framework.common.data.privilege;

import java.util.List;

public class RegistDto {
    private String client;
    private List<ModuleDto> modules;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<ModuleDto> getModules() {
        return modules;
    }

    public void setModules(List<ModuleDto> modules) {
        this.modules = modules;
    }
}
