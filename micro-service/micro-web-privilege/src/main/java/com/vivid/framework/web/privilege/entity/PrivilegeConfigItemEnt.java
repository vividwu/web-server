package com.vivid.framework.web.privilege.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.vivid.framework.common.data.privilege.entity.PvgResourceItemEnt;
import lombok.Data;

@Data
@TableName("privilege_resource_item")
public class PrivilegeConfigItemEnt extends PvgResourceItemEnt {  //todo 加sort?
}
