package com.vivid.biz.flow.entity.workflow;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wf_process")
public class ProcessEnt {
    private String id;
    private String name;
    private String displayName;
    private String content;
    private Integer version;
    private LocalDateTime createTime;
}
