package com.vivid.framework.common.data.privilege;

public class PrivilegeConfigItem {
    private long aKey;
    private String priName;
    private String path;
    private String root;
    private String type;
    private long parentKey;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getParentKey() {
        return parentKey;
    }

    public void setParentKey(long parentKey) {
        this.parentKey = parentKey;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAKey() {
        return aKey;
    }

    public void setAKey(long aKey){
        this.aKey = aKey;
    }

    public String getPriName() {
        return priName;
    }

    public void setPriName(String priName) {
        this.priName = priName;
    }
}
