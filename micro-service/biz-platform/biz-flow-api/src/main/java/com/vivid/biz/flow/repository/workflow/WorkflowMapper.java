package com.vivid.biz.flow.repository.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.dto.flow.QueryFilterDto;
import com.vivid.biz.flow.dto.flow.WorkItemDto;
import com.vivid.biz.flow.entity.workflow.ProcessEnt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface WorkflowMapper extends BaseMapper<ProcessEnt> {
    List<WorkItemDto> getOrderHistory(@Param("query")QueryFilterDto query);
    List<WorkItemDto> getWorkItems(@Param("query")QueryFilterDto query);
    @Select("select * from wc_process_table_link where process_name=#{processName}")
    List<Map<String,String>> getWcProcessTableLink(@Param("processName")String processName);
    @Select("select t.table_name,t.process_name,f.field_name,f.data_type from wc_process_table_link t join wc_table_fields f on t.table_name=f.table_name where t.process_name=#{processName}")
    List<Map<String,String>> getWcProcessTableFields(@Param("processName")String processName);
    @Select("select ${fields} from ${table} where order_no=#{orderNo}")
    List<Map<String,Object>> getDbDataMaps(@Param("fields")String fields,@Param("table")String table,@Param("orderNo")String orderNo);
    List<WorkItemDto> getWorkItemHistoryPage(@Param("query")QueryFilterDto query);
}
