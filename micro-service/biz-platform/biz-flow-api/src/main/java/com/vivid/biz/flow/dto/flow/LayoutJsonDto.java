package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayoutJsonDto {
    private LayoutJsonNodeDto[] nodeDataArray;
    private LayoutJsonLinkDto[] linkDataArray;
}
