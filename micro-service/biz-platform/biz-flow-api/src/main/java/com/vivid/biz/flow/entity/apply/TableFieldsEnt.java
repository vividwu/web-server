package com.vivid.biz.flow.entity.apply;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wc_table_fields")
public class TableFieldsEnt {
    private String id;
    private String tableName;
    private String fieldName;
    private String dataType;
    private String required;
    private String memo;
    private LocalDateTime createTime;
}
