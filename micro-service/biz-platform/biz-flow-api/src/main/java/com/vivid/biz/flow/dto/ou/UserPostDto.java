package com.vivid.biz.flow.dto.ou;

import com.vivid.biz.flow.entity.ou.PostInfoEnt;
import com.vivid.biz.flow.entity.ou.UserPostEnt;
import lombok.Data;

@Data
public class UserPostDto extends PostInfoEnt {
    private Integer userId;
    private String userName;
    private Integer deptId;
    private String deptName;
    private String fullPathName;
}
