package com.vivid.framework.web.ou.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ou_user_post")
public class UserPostEnt {
    private Integer userId;
    private Integer postId;
}
