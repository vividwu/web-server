package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

//提交表单结构，暂时没用，可以用来校验结构合法性
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplySubmitDto {
    @JsonProperty("args")
    private Map args;
    @JsonProperty("store_keys")
    private Map[] storeKeys;
    @JsonProperty("store_list_keys")
    private Map[] storeListKeys;
}
