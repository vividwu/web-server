package com.vivid.framework.privilege;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;

import java.util.List;

//参考WebSecurityConfigurerAdapter，业务系统使用方自定义可以覆盖默认的权限验证
@Order(99)
public abstract class PrivilegeCheckConfigurerAdapter {
    private boolean disableLocalConfigurePrivilege;  //没有被业务系统重写
    private IPrivilegeCheck iPrivilegeCheck;
    //用来被重写
    protected void configure(PrivilegeManagerBuilder builder){//(PrivilegeManagerBuilder privilege) throws Exception {
        this.disableLocalConfigurePrivilege = true;
        List<IPrivilegeCheck> checks = builder.getPrivilegeCheckImpls();
        if(checks!=null && checks.size()>0)
            this.iPrivilegeCheck = checks.get(0);
    }
    public IPrivilegeCheck getServiceImpl(){
        return  this.iPrivilegeCheck;
    }
}
