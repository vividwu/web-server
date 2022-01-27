package com.vivid.biz.flow.repository.code;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vivid.biz.flow.entity.code.DicConfEnt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DicConfMapper extends BaseMapper<DicConfEnt> {
    @Select("${sql}")
    @ResultType(ArrayList.class)
    List<Map> getSqlList(@Param("sql") String dicSql);
}
