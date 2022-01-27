package com.vivid.biz.flow.dto.ou;

import com.vivid.biz.flow.entity.ou.DeptInfoEnt;
import lombok.Data;

import java.util.List;

@Data
public class DeptInfoDto extends DeptInfoEnt {
    private List<UserInfoDto> manager;
}
