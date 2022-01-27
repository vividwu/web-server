package com.vivid.biz.flow.dto.flow;

import lombok.Data;

import java.util.List;

@Data
public class DBTableDto {
    private String table;
    private List<String> fields;
    private List<Object> values;
}
