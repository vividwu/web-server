package com.vivid.biz.flow.entity.privilege;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pvg_role")
public class PvgRoleEnt {  //TODO  继承privilege项目
    private Integer id;
    private String code;
    private String name;
    private String memo;
    private LocalDateTime createTime;
}
