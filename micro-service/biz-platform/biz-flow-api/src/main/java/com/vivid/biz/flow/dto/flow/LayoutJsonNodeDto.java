package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayoutJsonNodeDto {
    private String key;
    private String text;
    private String type;
    private Map<String,String> conf;  //
}
