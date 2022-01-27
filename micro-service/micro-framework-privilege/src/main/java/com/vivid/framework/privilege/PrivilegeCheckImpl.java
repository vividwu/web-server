package com.vivid.framework.privilege;
import com.vivid.framework.common.data.privilege.PrivilegeConfigItemBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//默认实现，可以被覆盖
public class PrivilegeCheckImpl implements IPrivilegeCheck {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public boolean check4User(String userKey) {
        logger.info("user:{},default check from {}",userKey,"feign api");
        return false;
    }

    PrivilegeConfigItemBean privilegeConfigItemBean;
    @Override
    public void setConfigItem(PrivilegeConfigItemBean privilegeConfigItemBean) {
        this.privilegeConfigItemBean = privilegeConfigItemBean;
    }
}
