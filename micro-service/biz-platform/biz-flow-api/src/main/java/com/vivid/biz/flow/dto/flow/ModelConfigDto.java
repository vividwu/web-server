package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vivid.biz.flow.entity.apply.TableFieldsEnt;
import com.vivid.biz.flow.entity.apply.TableInfoEnt;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelConfigDto {
    private String processName;
    private String category;
    private TableInfoEnt info;
    private List<TableFieldsEnt> fields;
}
