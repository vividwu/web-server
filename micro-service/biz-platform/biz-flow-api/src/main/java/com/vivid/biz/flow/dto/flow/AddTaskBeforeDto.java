package com.vivid.biz.flow.dto.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddTaskBeforeDto {
    @JsonProperty("before_task_id")
    private String beforeTaskId;
    @JsonProperty("assginee_key")
    private String assgineeKey;
    @JsonProperty("assginee_ids")
    private String assgineeIds;
}
