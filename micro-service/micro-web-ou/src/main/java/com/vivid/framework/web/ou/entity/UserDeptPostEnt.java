package com.vivid.framework.web.ou.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ou_user_dept_post")
public class UserDeptPostEnt {
    private Integer userId;
    private Integer deptId;
    private String postCode;
    private String status;
}
