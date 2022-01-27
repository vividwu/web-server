package com.vivid.framework.web.ou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.framework.web.ou.entity.UserInfoEnt;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoMapper extends BaseMapper<UserInfoEnt> {

    @Select("select * from  ou_user_info where id in(select user_id from ou_user_dept where dept_id = #{deptId})")
    List<UserInfoEnt> getUserInfoByDeptId(Integer deptId);
}
