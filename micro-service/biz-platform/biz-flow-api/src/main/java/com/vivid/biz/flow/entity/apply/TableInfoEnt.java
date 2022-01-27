package com.vivid.biz.flow.entity.apply;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wc_table_info")
public class TableInfoEnt {
    private String id;
    private String name;
    private String memo;
    private LocalDateTime createTime;
}
