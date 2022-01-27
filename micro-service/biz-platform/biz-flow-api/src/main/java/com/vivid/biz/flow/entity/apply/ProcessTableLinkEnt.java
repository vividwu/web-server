package com.vivid.biz.flow.entity.apply;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wc_process_table_link")
public class ProcessTableLinkEnt {
    private String tableId;
    private String tableName;
    private String processName;
    private String category;
}
