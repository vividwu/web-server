package com.vivid.framework.web.ou.dto;

import com.vivid.framework.web.ou.entity.UserInfoEnt;
import lombok.Data;

import java.util.List;
@Data
public class UserInfoDto extends UserInfoEnt {
    private List<UserPostDto> posts;
    private List<DeptInfoDto> depts;
}
