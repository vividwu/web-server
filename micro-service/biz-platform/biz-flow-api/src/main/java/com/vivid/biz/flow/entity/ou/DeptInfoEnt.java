package com.vivid.biz.flow.entity.ou;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

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