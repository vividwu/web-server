package com.vivid.biz.flow.entity.ou;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ou_user_dept")
public class UserDeptEnt {
    private Integer userId;
    private Integer deptId;
    private Integer isManager;
}
