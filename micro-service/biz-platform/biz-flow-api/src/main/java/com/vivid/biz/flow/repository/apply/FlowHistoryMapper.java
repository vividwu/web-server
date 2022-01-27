package com.vivid.biz.flow.repository.apply;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.entity.apply.FlowHistoryEnt;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface FlowHistoryMapper extends BaseMapper<FlowHistoryEnt> {
    @Select("select t.id,t.task_name,t.display_name,ta.actor_id,ta.surrogated_id from wf_task t left join wf_task_actor ta on t.id=ta.task_id where t.order_id=#{oid}")
    List<Map<String,String>> getCandidateActorsByOrdertId(String oid);

    @Select("select wch.*,wfa.surrogated_id from wc_flow_history wch left join wf_hist_task_actor wfa on wch.task_id=wfa.task_id and wch.operator=wfa.actor_id where wch.order_no=#{orderNo} order by create_time asc")
    List<Map<String,String>> getHistoryActorsByOrdertNo(String orderNo);
}
