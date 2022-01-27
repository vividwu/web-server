package com.vivid.biz.flow.entity.code;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wc_common_code")
public class CommonCodeEnt {
    private String code;
    private String parentCode;
    private String text;
    private Integer sort;
    private String category;
    private String flag;
    private String remark;
    private String deleted;
}
