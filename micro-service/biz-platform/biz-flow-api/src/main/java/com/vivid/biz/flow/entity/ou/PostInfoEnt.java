package com.vivid.biz.flow.entity.ou;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalTime;

@Data
@TableName("ou_post_info")
public class PostInfoEnt {
    private Integer id;
    private String name;
    private String code;
    private String flag;
    private LocalTime createTime;
}
