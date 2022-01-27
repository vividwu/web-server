package com.vivid.biz.flow.repository.privilege;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.entity.privilege.PvgResourceEnt;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PvgResourceMapper extends BaseMapper<PvgResourceEnt> {
    @Select("select r.* from pvg_role_user ru left join pvg_role_resource rr on ru.role_id=rr.role_id left join pvg_resource r on rr.resource_id=r.id and category='menu' and ru.user_id=#{uid}")
    List<PvgResourceEnt> getUserMenu(Integer uid);
}
