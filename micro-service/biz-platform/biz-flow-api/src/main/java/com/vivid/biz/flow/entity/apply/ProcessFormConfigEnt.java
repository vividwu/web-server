package com.vivid.biz.flow.entity.apply;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("wc_process_form_config")
public class ProcessFormConfigEnt {
    private String processId;
    private String processName;
    private String formCode;
    private String formMemo;
    private String content;
    private String config;
    private Integer status;
    private LocalDateTime createTime;
    private String operation;
}
