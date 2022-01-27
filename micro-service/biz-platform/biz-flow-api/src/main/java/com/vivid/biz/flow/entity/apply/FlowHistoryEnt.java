package com.vivid.biz.flow.entity.apply;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wc_flow_history")
public class FlowHistoryEnt {
    private String orderId;
    private String orderNo;
    private String taskId;
    private String taskKey;
    private String taskName;
    private String action;
    private String operator;
    private String operatorView;
    private LocalDateTime createTime;
    private String operatorReason;
    private String memo;
}
