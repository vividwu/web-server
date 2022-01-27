package com.vivid.biz.flow.entity.privilege;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pvg_resource")
public class PvgResourceEnt {  //TODO  继承privilege项目
    private Integer id;
    private Integer parentId;
    private String code;
    private String name;
    private String category;
    private String resourceUri;
    private Integer sort;
    private String memo;
    private LocalDateTime createTime;
}
