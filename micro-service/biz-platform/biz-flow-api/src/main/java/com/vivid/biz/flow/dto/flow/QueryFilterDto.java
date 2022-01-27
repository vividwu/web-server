package com.vivid.biz.flow.dto.flow;

import lombok.Data;

@Data
public class QueryFilterDto {
    private String orderBy;  //默认asc
    /*********common parameters***********/
    private String processId;  //流程定义id
    private Integer version;      //流程定义版本号
    private String orderId;   //流程实例id
    private String taskId;   //任务id
    private String taskCreateTimeStart;   //任务创建开始时间
    private String taskCreateTimeEnd;   //任务创建结束时间
    private String orderCreateTimeStart;   //实例创建开始时间
    private String orderCreateTimeEnd;   //实例创建结束时间
    private String operateTime;   //操作时间
    private String[] operators; //操作人员id
    private String[] names; //名称
    private String processDisplayName;   //显示名称
    private Integer state;      //状态
    private String processType;   //流程类型
    private String[] excludedIds; //exclude ids
    /*********order parameters***********/
    private String parentId; //父实例id
    private String orderNo; //实例编号
    //IsOrderFinished bool  //是否流程实例结束  his_order表end_time有值
    /*********task parameters***********/
    private String taskType;    //任务类型
    private String performType; //任务参与类型
    private String parentTaskId; //前一任务节点Id
}
