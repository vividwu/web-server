package com.vivid.biz.flow.dto.ou;

import com.vivid.biz.flow.entity.ou.UserDeptEnt;
import lombok.Data;

@Data
public class UserDeptDto extends UserDeptEnt {
    private String deptName;
    private String fullPathName;
}
