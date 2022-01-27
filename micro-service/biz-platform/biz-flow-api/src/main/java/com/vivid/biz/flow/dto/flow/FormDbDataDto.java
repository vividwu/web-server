package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormDbDataDto {  //{"success":true,"message":"","data":{"main":{"fm_bx_info":{"amount":"",
    private Boolean success;
    private Map data;
}
