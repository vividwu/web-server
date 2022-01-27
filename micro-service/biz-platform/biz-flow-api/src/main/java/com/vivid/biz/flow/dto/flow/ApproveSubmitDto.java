package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApproveSubmitDto extends ApplySubmitDto {
    //"agree" //同意
    //"reject"  //拒绝
    @JsonProperty("approve_type")
    private String approveType;
    @JsonProperty("reason")
    private String reason;
}
