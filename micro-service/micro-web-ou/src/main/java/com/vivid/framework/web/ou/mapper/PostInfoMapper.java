package com.vivid.framework.web.ou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.framework.web.ou.dto.UserPostDto;
import com.vivid.framework.web.ou.entity.PostInfoEnt;
import com.vivid.framework.web.ou.entity.UserInfoEnt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostInfoMapper extends BaseMapper<PostInfoEnt> {

    @Select("select a.*,b.name post_name from ou_user_post a,ou_post_info b where a.post_id=b.id and a.user_id in (${uids})")
    List<UserPostDto> getPostInfoByUserIds(@Param("uids")String uids);
}
