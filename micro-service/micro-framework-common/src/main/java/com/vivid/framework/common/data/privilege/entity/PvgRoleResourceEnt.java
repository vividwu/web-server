package com.vivid.framework.common.data.privilege.entity;

import java.util.Date;

public class PvgRoleResourceEnt {
    private Integer roleId;
    private String serName;
    private Integer aKey;
    private Date createTime;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getSerName() {
        return serName;
    }

    public void setSerName(String serName) {
        this.serName = serName;
    }

    public Integer getaKey() {
        return aKey;
    }

    public void setaKey(Integer aKey) {
        this.aKey = aKey;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
