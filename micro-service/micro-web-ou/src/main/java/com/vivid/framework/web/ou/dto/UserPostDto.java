package com.vivid.framework.web.ou.dto;

import com.vivid.framework.web.ou.entity.PostInfoEnt;
import com.vivid.framework.web.ou.entity.UserPostEnt;
import lombok.Data;

@Data
public class UserPostDto extends PostInfoEnt {
    private Integer userId;
    private String userName;
    private Integer deptId;
    private String deptName;
    private String fullPathName;
}
