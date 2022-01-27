package com.vivid.biz.flow.repository.ou;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.dto.ou.UserInfoDto;
import com.vivid.biz.flow.entity.ou.UserInfoEnt;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoMapper extends BaseMapper<UserInfoEnt> {

    @Select("select u.*,d.is_manager from ou_user_dept d left join ou_user_info u on d.user_id=u.id where d.dept_id=#{deptId}")
    List<UserInfoDto> getUserInfoByDeptId(Integer deptId);

    //@Select("select u.*,d.is_manager from ou_user_dept d left join ou_user_info u on d.user_id=u.id left join ou_dept_info di on d.dept_id=di.id where di.full_path_id like concat(#{deptId},'%')")
    @Select("select u.* from ou_user_info u join (select distinct user_id from ou_user_dept_post udp left join ou_dept_info di on udp.dept_id=di.id where di.full_path_id like concat(#{deptId},'%')) ud on ud.user_id=u.id")
    List<UserInfoDto> getUserInfoByDeptFullId(String deptFullId);
}
