package com.vivid.biz.flow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.dto.flow.*;
import com.vivid.biz.flow.entity.apply.SurrogateEnt;
import com.vivid.biz.flow.service.ApplyServiceImpl;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class ApplyController {
    @Autowired
    ApplyServiceImpl applyService;

    //获取表单配置-首次申请提交页面展示
    @RequestMapping(value = "/form/config/start/{process_name}", method = {RequestMethod.GET})
    public ResponseResult getApplyFormConfig(@PathVariable("process_name") String pname) {
        return ResponseResult.successed(applyService.getProcessFormConfig(pname));
    }
    //获取流程名+表单名获取表单设计配置数据
    @RequestMapping(value = "/form/config/code", method = {RequestMethod.GET})
    public ResponseResult getApplyFormConfig(String pname, String code) {
        return ResponseResult.successed(applyService.getProcessFormConfigByPnameAndCode(pname, code));
    }
    //提交申请单
    @RequestMapping(value = "/apply/{process_name}/submit", method = {RequestMethod.POST})
    public ResponseResult submitApply(@PathVariable("process_name") String pname, @RequestBody ApplySubmitDto dto) {
        return ResponseResult.string(applyService.submitApply(pname, dto));
    }
    //已提交
    @RequestMapping(value = "/workflow/my_list", method = {RequestMethod.GET})
    public ResponseResult<PageInfo<WorkItemDto>> myApplyList(QueryFilterDto query) {
        return ResponseResult.successed(applyService.getOrderHistory(query));
    }
    //待办
    @RequestMapping(value = "/workflow/todo", method = {RequestMethod.GET})
    public ResponseResult<PageInfo<WorkItemDto>> myTodoList(QueryFilterDto query) {
        return ResponseResult.successed(applyService.getTodoList(query));
    }
    //已审批
    @RequestMapping(value = "/workflow/complete_list", method = {RequestMethod.GET})
    public ResponseResult<PageInfo<WorkItemDto>> myCompleteList() {
        return ResponseResult.successed(applyService.myCompleteList());
    }
    //根据TaskId，获取提交的自定义表单数据
    @RequestMapping(value = "/order_info/task/{tid}", method = {RequestMethod.GET})
    public ResponseResult getFormDataByTaskId(@PathVariable("tid") String tid) {
        return applyService.getFormDataByTaskId(tid);
    }
    //根据OrderId，获取提交的自定义表单数据
    @RequestMapping(value = "/order_info/order/{oid}", method = {RequestMethod.GET})
    public ResponseResult getFormDataByOrderId(@PathVariable("oid") String oid) {
        return applyService.getFormDataByOrderId(oid);
    }
    //审批申请单
    @RequestMapping(value = "/approve/{task_id}", method = {RequestMethod.POST})
    public ResponseResult submitApply(@PathVariable("task_id") String taskId, @RequestBody ApproveSubmitDto dto) {
        return ResponseResult.string(applyService.submitApprove(taskId, dto));
    }
    //获取表单配置-审批界面，根据taskId获取
    @RequestMapping(value = "/form/config/approve/{task_id}", method = {RequestMethod.GET})
    public ResponseResult getApproveFormConfig(@PathVariable("task_id") String taskId) {
        return applyService.getProcessFormConfigByTaskId(taskId);
    }
    //获取表单配置-查看界面，根据orderId获取
    @RequestMapping(value = "/form/config/view/{order_id}", method = {RequestMethod.GET})
    public ResponseResult getApplyViewFormConfig(@PathVariable("order_id") String orderId) {
        return applyService.getProcessFormConfigByOrderId(orderId);
    }
    //执行指定任务 task_id=&actor=
    @RequestMapping(value = "/config/task/open/exec", method = {RequestMethod.POST})
    public ResponseResult execTask(@RequestParam("task_id") String taskId,String actor) {
        return applyService.execTaskByActor(taskId,actor);
    }
    //查询指定人的任务?actor=xxx
    @RequestMapping(value = "/config/task/open/query", method = {RequestMethod.GET})
    public ResponseResult getUserToDoListByActorId(String actor) {
        return ResponseResult.successed(applyService.getUserToDoListByActorId(actor));
    }
//    //获取申请单审批历史
//    @RequestMapping(value = "/approve/history/{order_id}", method = {RequestMethod.GET})
//    public ResponseResult getFlowHistoryByOrderId(@PathVariable("order_id") String orderId) {
//        return ResponseResult.successed(applyService.getFlowHistoryByOrderId(orderId));
//    }
    //获取申请单审批历史
    @RequestMapping(value = "/approve/history/{order_no}", method = {RequestMethod.GET})
    public ResponseResult getFlowHistoryByOrderNo(@PathVariable("order_no") String orderNo) {
        return ResponseResult.successed(applyService.getFlowHistoryByOrderNo(orderNo));
    }
    //加签
    @RequestMapping(value = "/approve/add_task_before/{task_id}", method = {RequestMethod.POST})
    public ResponseResult addTaskBefore(@PathVariable("task_id") String taskId, @RequestBody AddTaskBeforeDto dto) {
        return ResponseResult.string(applyService.addTaskBefore(taskId,dto));
    }
    //代理人
    @RequestMapping(value = "/surrogate/all", method = {RequestMethod.GET})
    public ResponseResult<PageInfo<SurrogateDto>> getSurrogateAll() {
        return ResponseResult.successed(applyService.getSurrogateAllPage());
    }
    //编辑代理人
    @RequestMapping(value = "/surrogate/update", method = {RequestMethod.POST})
    public ResponseResult updateSurrogate(@RequestBody SurrogateEnt dto) {
        return ResponseResult.string(applyService.updateSurrogate(dto));
    }
    //新增代理人
    @RequestMapping(value = "/surrogate/create", method = {RequestMethod.POST})
    public ResponseResult createSurrogate(@RequestBody SurrogateEnt dto) {
        return ResponseResult.string(applyService.createSurrogate(dto));
    }
    //删除代理人
    @RequestMapping(value = "/surrogate/delete", method = {RequestMethod.POST})
    public ResponseResult deleteSurrogate(String id) {
        return ResponseResult.string(applyService.deleteSurrogate(id));
    }
}