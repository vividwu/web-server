package com.vivid.biz.flow.dto.flow;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BindCustomDataDto {
    private Map<String, Map<String,Object>> main;
    private Map<String, List<Map<String,Object>>> detail;
}
