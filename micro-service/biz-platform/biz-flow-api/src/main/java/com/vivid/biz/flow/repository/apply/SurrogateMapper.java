package com.vivid.biz.flow.repository.apply;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.dto.flow.SurrogateDto;
import com.vivid.biz.flow.entity.apply.SurrogateEnt;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SurrogateMapper extends BaseMapper<SurrogateEnt> {
    @Select("select s.*,ou.display_name as operator_view,su.display_name as surrogate_view from wf_surrogate s left join ou_user_info ou on s.operator=ou.id left join ou_user_info su on s.surrogate=su.id")
    List<SurrogateDto> getSurrogateAll();
}
