package com.vivid.biz.flow.repository.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.entity.workflow.BillcodeGenEnt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BillcodeGenMapper extends BaseMapper<BillcodeGenEnt> {
    @Select("select max(num) as Num from wc_billcode_gen where gen_date=#{genDate} and process_name=#{processName}")
    Integer getBillCode(@Param("genDate")String genDate, @Param("processName")String processName);
}
