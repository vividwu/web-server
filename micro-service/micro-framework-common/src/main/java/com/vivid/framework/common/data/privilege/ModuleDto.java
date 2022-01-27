package com.vivid.framework.common.data.privilege;

import java.util.List;

public class ModuleDto {
    private String name;
    private List<DefineDto> defines;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DefineDto> getDefines() {
        return defines;
    }

    public void setDefines(List<DefineDto> defines) {
        this.defines = defines;
    }
}
