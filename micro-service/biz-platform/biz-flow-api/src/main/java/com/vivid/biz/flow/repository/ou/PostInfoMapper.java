package com.vivid.biz.flow.repository.ou;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.dto.ou.UserPostDto;
import com.vivid.biz.flow.entity.ou.PostInfoEnt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostInfoMapper extends BaseMapper<PostInfoEnt> {

    //@Select("select a.*,b.name post_name from ou_user_post a,ou_post_info b where a.post_id=b.id and a.user_id in (${uids})")
    @Select("select a.*,b.code,b.name,d.name dept_name,d.full_path_name from ou_user_dept_post a,ou_post_info b,ou_dept_info d where a.post_code=b.code and a.dept_id=d.id and a.user_id in (${uids})")
    List<UserPostDto> getPostInfoByUserIds(@Param("uids") String uids);
}
