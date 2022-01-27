package com.vivid.biz.flow.dto.flow;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkItemDto {
    private String id;
    private String processId;                    //流程定义ID
    private String orderId;                      //流程实例ID
    private String taskId;                       //任务ID
    private String processName;                  //流程名称
    private String processDisplayName;           //流程显示名称
    private String instanceUrl;                  //流程实例url
    private String parentId;                     //流程实例为子流程时，该字段标识父流程实例ID
    private String parentTaskId;                 //已执行的上一任务节点的ID
    private String creator;                      //流程实例创建者ID
    private String orderCreateTime;              //流程实例创建时间
    private String orderEndTime;                 //流程实例结束时间
    private String orderExpireTime;              //流程实例期望完成时间
    private String orderNo;                      //流程实例编号
    private String orderVariable;                //流程实例附属变量
    private String taskName;                     //任务名称
    private String taskKey;                      //任务标识名称
    private String PerformType; //参与类型（any：普通任务；all：参与者fork任务[即：如果10个参与者，需要每个人都要完成，才继续流转]）
    private String TaskType;  //任务类型
    private Integer TaskState;               //任务状态（0：结束；1：活动）
    private String TaskCreateTime;                //任务创建时间
    private String TaskEndTime;                   //任务完成时间
    private String TaskExpireTime;                //期望任务完成时间
    private String TaskVariable;                  //任务附属变量
    private String Operator;                      //任务处理者ID
    private String ActionUrl;                     //任务关联的表单url
    private String ActorIds;                      //任务参与者列表
    private String ParentTaskId;                  //任务上一任务节点Id
}
