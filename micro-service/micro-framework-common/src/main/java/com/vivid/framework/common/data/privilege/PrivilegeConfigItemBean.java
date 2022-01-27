package com.vivid.framework.common.data.privilege;

import java.util.List;

public class PrivilegeConfigItemBean {
    private String serName;
    private List<PrivilegeConfigItem> privilegeConfigItemList;

    public String getSerName() {
        return serName;
    }

    public void setSerName(String serName) {
        this.serName = serName;
    }

    public List<PrivilegeConfigItem> getPrivilegeConfigItemList() {
        return privilegeConfigItemList;
    }

    public void setPrivilegeConfigItemList(List<PrivilegeConfigItem> privilegeConfigItemList) {
        this.privilegeConfigItemList = privilegeConfigItemList;
    }
}
