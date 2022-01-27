package com.vivid.biz.flow.dto.ou;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vivid.biz.flow.entity.ou.UserInfoEnt;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto extends UserInfoEnt {
    private Integer isManager;
    private List<UserPostDto> posts;
}
