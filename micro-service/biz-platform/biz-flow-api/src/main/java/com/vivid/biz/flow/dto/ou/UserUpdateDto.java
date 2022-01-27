package com.vivid.biz.flow.dto.ou;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vivid.biz.flow.entity.ou.UserDeptEnt;
import com.vivid.biz.flow.entity.ou.UserInfoEnt;
import com.vivid.biz.flow.entity.ou.UserPostEnt;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateDto extends UserInfoEnt {
    private List<UserPostDto> posts;
}
