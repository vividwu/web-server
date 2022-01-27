package com.vivid.framework.privilege;

import java.util.ArrayList;
import java.util.List;

//参考perAuth("xxService.check()")模式
public class PrivilegeManagerBuilder {
    private List<IPrivilegeCheck> privilegeProviders = new ArrayList<>();
    public PrivilegeManagerBuilder  privilegeProvider(IPrivilegeCheck privilegeCheckProvider) {
        this.privilegeProviders.add(privilegeCheckProvider);
        return this;
    }
    public List<IPrivilegeCheck> getPrivilegeCheckImpls(){
        return privilegeProviders;
    }
}
