package com.vivid.framework.web.ou.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.vivid.framework.common.data.privilege.PrivilegeConfigItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("ou_dept_info")
public class DeptInfoEnt {
    private Integer id;
    private String name;
    private Integer parentId;
    private String fullPathId;
    private String fullPathName;
    private LocalDateTime createTime;
    private String remark;
}