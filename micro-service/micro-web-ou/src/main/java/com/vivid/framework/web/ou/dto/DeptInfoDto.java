package com.vivid.framework.web.ou.dto;

import com.vivid.framework.web.ou.entity.DeptInfoEnt;
import lombok.Data;

@Data
public class DeptInfoDto extends DeptInfoEnt {
    private UserInfoDto manager;
}
