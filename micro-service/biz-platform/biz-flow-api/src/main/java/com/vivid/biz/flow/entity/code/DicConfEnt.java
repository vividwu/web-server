package com.vivid.biz.flow.entity.code;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wc_dic_conf")
public class DicConfEnt {
    private String appId;
    private String queryCode;
    private String dsName;
    private String dsType;
    private String fetchType;
    private String params;
}
