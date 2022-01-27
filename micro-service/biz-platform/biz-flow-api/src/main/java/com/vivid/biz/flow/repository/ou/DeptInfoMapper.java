package com.vivid.biz.flow.repository.ou;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.dto.ou.UserDeptDto;
import com.vivid.biz.flow.entity.ou.DeptInfoEnt;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptInfoMapper extends BaseMapper<DeptInfoEnt> {
    @Select("select a.*,b.name dept_name,b.full_path_name from ou_user_dept_post a,ou_dept_info b where a.dept_id=b.id and a.user_id =#{uid}")
    List<UserDeptDto> getUserAllDeptView(Integer uid);
}
