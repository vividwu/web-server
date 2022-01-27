package com.vivid.biz.flow.dto.dict;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DicConfParamsDto implements Serializable {
    private String sql;
}
