package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayoutJsonLinkDto {
    private String from;
    private String to;
    private String key;  //name
    private String text;  //displayname
}
