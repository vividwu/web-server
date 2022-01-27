package com.vivid.biz.flow.entity.apply;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wc_process_config")
public class ProcessConfigEnt {
    private String processId;
    private String processName;
    private String name;
    private Integer step;
    private String flowDesignXml;
    private String flowDesignLayout;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String icon;
    private Integer status;
    private String remark;
}
