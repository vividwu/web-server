package com.vivid.biz.flow.entity.apply;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wc_process_script")
public class ProcessScriptEnt {
    private String processId;
    private String processName;
    private String category;
    private String scriptName;
    private String scriptContent;
    private String testContent;
    private String remark;
    private LocalDateTime createTime;
}
