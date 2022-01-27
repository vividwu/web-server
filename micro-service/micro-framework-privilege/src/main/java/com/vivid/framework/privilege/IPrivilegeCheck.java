package com.vivid.framework.privilege;

import com.vivid.framework.common.data.privilege.PrivilegeConfigItemBean;

public interface IPrivilegeCheck {
    boolean check4User(String userKey);
    void setConfigItem(PrivilegeConfigItemBean privilegeConfigItemBean);
}
