package com.vivid.biz.testweb.service.impl;

import com.vivid.framework.common.data.privilege.PrivilegeConfigItemBean;
import com.vivid.framework.privilege.FramePrivilegeConfiguration;
import com.vivid.framework.privilege.IPrivilegeCheck;
import org.springframework.stereotype.Service;

@Service
public class CustomPrivilegeServiceImpl implements IPrivilegeCheck {

    @Override
    public boolean check4User(String userKey) {
        return false;
    }
    PrivilegeConfigItemBean privilegeConfigItemBean;
    @Override
    public void setConfigItem(PrivilegeConfigItemBean privilegeConfigItemBean) {
        this.privilegeConfigItemBean = privilegeConfigItemBean;
    }
}
