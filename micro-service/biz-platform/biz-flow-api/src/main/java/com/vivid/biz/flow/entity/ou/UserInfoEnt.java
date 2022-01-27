package com.vivid.biz.flow.entity.ou;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vivid.framework.common.data.user.entity.OuUserEnt;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ou_user_info")
public class UserInfoEnt {
    @TableId(type = IdType.AUTO)//指定自增策略
    private Integer id;
    private String name;
    private String num;
    private String displayName;
    private String gender;
    private String mail;
    private String mobile;
    private String remark;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String extC1;
    private String extC2;
    private String extC3;
    private Integer extI1;
    private Integer extI2;
    private Integer extI3;
    private LocalDateTime extD1;
}
