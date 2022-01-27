package com.vivid.biz.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.config.web.RequestHolder;
import com.vivid.biz.flow.dto.flow.*;
import com.vivid.biz.flow.entity.apply.FlowHistoryEnt;
import com.vivid.biz.flow.entity.apply.ProcessConfigEnt;
import com.vivid.biz.flow.entity.apply.ProcessFormConfigEnt;
import com.vivid.biz.flow.entity.apply.SurrogateEnt;
import com.vivid.biz.flow.entity.ou.UserInfoEnt;
import com.vivid.biz.flow.repository.apply.FlowHistoryMapper;
import com.vivid.biz.flow.repository.apply.ProcessConfigMapper;
import com.vivid.biz.flow.repository.apply.ProcessFormConfigMapper;
import com.vivid.biz.flow.repository.apply.SurrogateMapper;
import com.vivid.biz.flow.repository.ou.UserInfoMapper;
import com.vivid.biz.flow.repository.workflow.WorkflowMapper;
import com.vivid.biz.flow.utils.DbUtil;
import com.vivid.biz.flow.utils.FlowEngineUtil;
import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.security.dto.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ApplyServiceImpl {
    @Autowired
    ProcessFormConfigMapper processFormConfigMapper;
    @Autowired
    ProcessConfigMapper processConfigMapper;
    @Autowired
    WorkflowMapper workflowMapper;
    @Autowired
    FlowHistoryMapper flowHistoryMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    SurrogateMapper surrogateMapper;

    @Value("${workflow.host}")
    String workflowHost;
    @Autowired
    FlowEngineUtil flowEngineUtil;

    public ProcessFormConfigEnt getProcessFormConfig(String processName) {
        //TODO 根据流程配置xml解析出提交页面的form code
        //开始连接的第一个节点 linkDataArray form=begin->to
        String applyTaskNodeKey = "";
        LayoutJsonDto jsonLayout = getLayoutJsonDto(processName);

        if (jsonLayout == null)
            return null;
        //获取开始后的提交节点
        for (int i = 0; i < jsonLayout.getLinkDataArray().length; i++) {
            if ("begin".equals(jsonLayout.getLinkDataArray()[i].getFrom())) {
                applyTaskNodeKey = jsonLayout.getLinkDataArray()[i].getTo();
                break;
            }
        }
        return getProcessFormConfigByTaskKey(jsonLayout, processName, applyTaskNodeKey);
    }

    public ProcessFormConfigEnt getProcessFormConfigByPnameAndCode(String processName, String formCode) {
        ProcessFormConfigEnt processFormConfigEnt = processFormConfigMapper.selectOne(new QueryWrapper<ProcessFormConfigEnt>().lambda()
                .eq(ProcessFormConfigEnt::getProcessName, processName)
                .eq(ProcessFormConfigEnt::getFormCode, formCode));
        return processFormConfigEnt;
    }

    public String submitApply(String pname, ApplySubmitDto dto) {
        return submitApplyEngine(null,pname,dto,"/api/apply/" + pname + "/submit");
    }
    public String submitApplyRaw(String pname, ApplySubmitDto dto) {
        Map mapUser = new HashMap();
        mapUser.put("uid",dto.getArgs().get("emp_id"));
        mapUser.put("udisplay",dto.getArgs().get("emp_display"));
        return submitApplyEngine(mapUser,pname,dto,"/api/apply/" + pname + "/submit_raw");
    }
    public String submitApplyEngine(Map userMap,String pname, ApplySubmitDto dto,String path) { //todo 判断是否第一次提交，OrderNo？
        if(userMap == null) {
            SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userMap = user.getAdditionalInformation();
        }
        try {
            Map map = flowEngineUtil.post2WorkflowEngineMap(path, dto, userMap);
            if (map != null && "false".equals(map.get("success").toString())) {
                return map.get("message").toString();
            } else {
                return null;
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    public String submitApprove(String taskId, ApproveSubmitDto dto) {
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map userMap = user.getAdditionalInformation();
        try {
            Map map = flowEngineUtil.post2WorkflowEngineMap("/api/approve/" + taskId, dto, userMap);
            if (map != null && "false".equals(map.get("success").toString())) {
                return map.get("message").toString();
            } else {
                return null;
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    //已提交
    public PageInfo<WorkItemDto> getOrderHistory(QueryFilterDto query) {
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        query.setOperators(new String[]{user.getAdditionalInformation().get("uid").toString()});
        List<WorkItemDto> list = workflowMapper.getOrderHistory(query);
        return new PageInfo<>(list);
    }

    public PageInfo<WorkItemDto> getTodoList(QueryFilterDto query) {
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        query.setOperators(new String[]{user.getAdditionalInformation().get("uid").toString()});
        List<WorkItemDto> list = workflowMapper.getWorkItems(query);
        return new PageInfo<>(list);
    }
    public ResponseResult getFormDataByTaskId(String tid) {
        QueryFilterDto query = new QueryFilterDto();
        query.setTaskId(tid);
        List<WorkItemDto> workItems = workflowMapper.getWorkItems(query);
        if (workItems == null) {
            return ResponseResult.failed("未找到任务ID:" + tid);
        }
        return getFormDataByWorkItemDto(workItems);
    }
    public ResponseResult getFormDataByOrderId(String oid) {
        QueryFilterDto query = new QueryFilterDto();
        query.setOrderId(oid);
        List<WorkItemDto> workItems = workflowMapper.getOrderHistory(query);
        if (workItems == null) {
            return ResponseResult.failed("未找到OrderID:" + oid);
        }
        return getFormDataByWorkItemDto(workItems);
    }
    //重新实现不从流程引擎接口获取,see webapp\service\apply_service.go GetCustomTableDataMaps
    private ResponseResult getFormDataByWorkItemDto(List<WorkItemDto> workItems) {
        BindCustomDataDto bindDto = new BindCustomDataDto();

        Map<String, DBTableDto> table = new HashMap<>();
        Map<String, String> tableCategory = getProcessTableCategory(workItems.get(0).getProcessName());
        Map<String, String> tableFields = getProcessTableFields(workItems.get(0).getProcessName());
        //构造自定义表数据查询脚本
        tableFields.forEach((k, v) -> {
            String[] arr = k.split("\\$");
            DBTableDto dbt = table.get(arr[0]);
            if (dbt == null) {
                table.put(arr[0], new DBTableDto());
                dbt = table.get(arr[0]);
            }
            //添加查询字段列表
            appendArray(dbt, arr[1]);
        });
        //第一个表是主表，剩下都是明细表，多条记录
        table.forEach((k, v) -> {
            String fields = StringUtils.join(v.getFields(), ",");
            List<Map<String, Object>> dbDataMaps = workflowMapper.getDbDataMaps(fields, k, workItems.get(0).getOrderNo());
            List<Map<String, Object>> tableRows = new ArrayList<>();
            dbDataMaps.stream().forEach(rowData -> {
                Map<String, Object> toDBFieldMaps = new HashMap<>();
                rowData.forEach((col, val) -> {
                    toDBFieldMaps.put(col, val);
                });
                tableRows.add(toDBFieldMaps);
            });
            if ("main".equals(tableCategory.get(k))) {
                Map<String, Map<String, Object>> map = new HashMap<>();
                map.put(k, tableRows.get(0));
                bindDto.setMain(map);
            } else {
                if (bindDto.getDetail() == null)
                    bindDto.setDetail(new HashMap<>());
                bindDto.getDetail().put(k, tableRows);
            }
        });
        return ResponseResult.successed(bindDto);
    }

    private void appendArray(DBTableDto dbt, String elem) {
        if (dbt.getFields() == null)
            dbt.setFields(new ArrayList<>());
        dbt.getFields().add(elem);
    }

    //###############
    //获取流程定义配置的实体表（主/子表）
    private Map<String, String> getProcessTableCategory(String processName) {
        List<Map<String, String>> list = workflowMapper.getWcProcessTableLink(processName);
        if (list == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        list.stream().forEach(o -> {
            map.put(o.get("table_name"), o.get("category"));
        });
        return map;
    }

    //获取流程定义配置的实体表【表名+$+字段名】：类型
    private Map<String, String> getProcessTableFields(String processName) {
        List<Map<String, String>> list = workflowMapper.getWcProcessTableFields(processName);
        if (list == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        list.stream().forEach(o -> {
            map.put(o.get("table_name") + "$" + o.get("field_name"), o.get("data_type"));
        });
        return map;
    }

    //###############
    @Deprecated
    public ResponseResult getFormDataByTaskId2(String tid) {
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map map;
        try {
            map = flowEngineUtil.getWorkflowEngineFormDbDataDto("/api/order_info/task/" + tid, user);
        } catch (Exception ex) {
            return ResponseResult.failed(null, ex.toString());
        }
        return ResponseResult.successed(map);
    }

    private ProcessFormConfigEnt getProcessFormConfigByTaskKey(LayoutJsonDto jsonLayout, String processName, String taskKey) {
        //TODO 根据流程配置xml解析出提交页面的form code
        String formCode = "";

        //获取表单code
        for (int i = 0; i < jsonLayout.getNodeDataArray().length; i++) {
            if (taskKey.equals(jsonLayout.getNodeDataArray()[i].getKey())) {
                formCode = jsonLayout.getNodeDataArray()[i].getConf().get("action");
                break;
            }
        }
        log.info("start apply formCode:{}", formCode);
        ProcessFormConfigEnt processFormConfigEnt = processFormConfigMapper.selectOne(new QueryWrapper<ProcessFormConfigEnt>().lambda()
                .eq(ProcessFormConfigEnt::getProcessName, processName)
                .eq(ProcessFormConfigEnt::getFormCode, formCode));
        return processFormConfigEnt;
    }

    private LayoutJsonDto getLayoutJsonDto(String processName) {
        ProcessConfigEnt configEnt = processConfigMapper.selectOne(new QueryWrapper<ProcessConfigEnt>().lambda()
                .eq(ProcessConfigEnt::getProcessName, processName));
        LayoutJsonDto jsonLayout = null;
        try {
            jsonLayout = new ObjectMapper().readValue(configEnt.getFlowDesignLayout(), LayoutJsonDto.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonLayout;
    }

    public ResponseResult getProcessFormConfigByTaskId(String taskId) {
        //查询任务
        QueryFilterDto query = new QueryFilterDto();
        query.setTaskId(taskId);
        List<WorkItemDto> list = workflowMapper.getWorkItems(query);
        if (list == null)
            return ResponseResult.failed("找不到该任务,id:" + taskId);

        LayoutJsonDto jsonLayout = getLayoutJsonDto(list.get(0).getProcessName());

        if (jsonLayout == null)
            return ResponseResult.failed("找不到该任务的流程,pname:" + list.get(0).getProcessName());

        ProcessFormConfigEnt ent = getProcessFormConfigByTaskKey(jsonLayout, list.get(0).getProcessName(), list.get(0).getTaskKey());
        return ResponseResult.successed(ent);
    }
    public ResponseResult getProcessFormConfigByOrderId(String orderId) {
        //查询任务
        //SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        QueryFilterDto query = new QueryFilterDto();
        query.setOrderId(orderId);
        query.setParentTaskId("start");
        //query.setOperators(new String[]{user.getAdditionalInformation().get("uid").toString()});
        List<WorkItemDto> list = workflowMapper.getWorkItemHistoryPage(query);
        if(list!=null && list.size()>0) {
            ProcessFormConfigEnt processFormConfigEnt = processFormConfigMapper.selectOne(new QueryWrapper<ProcessFormConfigEnt>().lambda()
                    .eq(ProcessFormConfigEnt::getProcessName, list.get(0).getProcessName())
                    .eq(ProcessFormConfigEnt::getFormCode, list.get(0).getActionUrl()));
            return ResponseResult.successed(processFormConfigEnt);
        }
        return ResponseResult.failed("未获取到任何表单配置数据");
    }
    public ResponseResult execTaskByActor(String taskId, String actor) {
        try {
            Map userMap = new HashMap();
            userMap.put("uid",actor);
            userMap.put("udisplay",actor);
            Map map = flowEngineUtil.post2WorkflowEngineUrl("/openapi/task/exec?task_id=" + taskId + "&actor=" + actor, userMap);
            if (map != null && "false".equals(map.get("success").toString())) {
                return ResponseResult.failed(map.get("message").toString());
            } else {
                return ResponseResult.successed(null);
            }
        } catch (Exception ex) {
            return ResponseResult.failed(ex.toString());
        }
    }
    public PageInfo<WorkItemDto> getUserToDoListByActorId(String actor){
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        QueryFilterDto query = new QueryFilterDto();
        query.setOperators(new String[]{actor});
        List<WorkItemDto> list = workflowMapper.getWorkItems(query);
        return new PageInfo<>(list);
    }
    public List<FlowHistoryEnt> getFlowHistoryByOrderId(String orderId) {
        return flowHistoryMapper.selectList(new QueryWrapper<FlowHistoryEnt>().lambda()
                .eq(FlowHistoryEnt::getOrderId, orderId));
    }
    public List<FlowHistoryEnt> getFlowHistoryByOrderNo(String orderNo) {
//        List<FlowHistoryEnt> list = flowHistoryMapper.selectList(new QueryWrapper<FlowHistoryEnt>().lambda()
//                .eq(FlowHistoryEnt::getOrderNo, orderNo).orderByAsc(FlowHistoryEnt::getCreateTime));
        List<FlowHistoryEnt> list = new ArrayList<>();
        List<Map<String,String>> listHist = flowHistoryMapper.getHistoryActorsByOrdertNo(orderNo);
        listHist.stream().forEach(x->{
            FlowHistoryEnt ent = new FlowHistoryEnt();
            ent.setAction(x.get("action"));
            ent.setTaskId(x.get("task_id"));
            ent.setTaskKey(x.get("task_key"));
            ent.setTaskName(x.get("task_name"));
            ent.setOrderId(x.get("order_id"));
            ent.setMemo(x.get("memo"));
            ent.setOperatorReason(x.get("operator_reason"));
            ent.setOperatorView(x.get("operator_view"));
            ent.setCreateTime(LocalDateTime.parse(x.get("create_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if(StringUtils.isNotEmpty(x.get("surrogated_id"))){
                UserInfoEnt surrogated = userInfoMapper.selectOne(new QueryWrapper<UserInfoEnt>().lambda()
                        .eq(UserInfoEnt::getId, x.get("surrogated_id")));
                ent.setOperatorView(ent.getOperatorView()+"(代理:"+surrogated.getDisplayName()+")");
            }
            list.add(ent);
        });
        if(list!=null && list.size()>0){
            FlowHistoryEnt last = list.get(list.size()-1);
            if(!"end".equals(last.getTaskId())){  //未完结单子，找待审批人
                List<Map<String,String>> actors = flowHistoryMapper.getCandidateActorsByOrdertId(last.getOrderId());
                StringBuilder nextDisplayName = new StringBuilder();
                FlowHistoryEnt nextHis = new FlowHistoryEnt();
                nextHis.setAction("waiting");
                nextHis.setOrderId(last.getOrderId());
                nextHis.setTaskKey(actors.get(0).get("task_name"));
                nextHis.setTaskName(actors.get(0).get("display_name"));
                actors.stream().forEach(x->{
                    UserInfoEnt actor = userInfoMapper.selectOne(new QueryWrapper<UserInfoEnt>().lambda()
                            .eq(UserInfoEnt::getId, x.get("actor_id")));
                    nextDisplayName.append(actor.getDisplayName());
                    if(StringUtils.isNotEmpty(x.get("surrogated_id"))){
                        UserInfoEnt surrogated = userInfoMapper.selectOne(new QueryWrapper<UserInfoEnt>().lambda()
                                .eq(UserInfoEnt::getId, x.get("surrogated_id")));
                        nextDisplayName.append("(代理:").append(surrogated.getDisplayName()).append(")");
                    }
                    nextDisplayName.append(" ");
                    //nextHis.setTaskKey(x.get("task_name"));
                    //nextHis.setTaskName(x.get("display_name"));
                    //nextHis.setOperator(x.get("actor_id"));
                    //nextHis.setOperatorView(actor.getDisplayName());
                });
                nextHis.setOperatorView(nextDisplayName.toString());
                list.add(nextHis);
            }
        }
        return list;
    }
    public PageInfo<WorkItemDto> myCompleteList() {
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        QueryFilterDto query = new QueryFilterDto();
        query.setOperators(new String[]{user.getAdditionalInformation().get("uid").toString()});
        List<WorkItemDto> list = workflowMapper.getWorkItemHistoryPage(query);
        return new PageInfo<>(list);
    }
    public String addTaskBefore(String taskId,AddTaskBeforeDto dto) {
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map userMap = user.getAdditionalInformation();
        try {
            Map map = flowEngineUtil.post2WorkflowEngineMap("/api/approve/add_task_before/" + taskId, dto, userMap);
            if (map != null && "false".equals(map.get("success").toString())) {
                return map.get("message").toString();
            } else {
                return null;
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    public PageInfo<SurrogateDto> getSurrogateAllPage() {
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        List<SurrogateDto> surrogateEnts = surrogateMapper.getSurrogateAll();
        return new PageInfo<>(surrogateEnts);
    }
    public String updateSurrogate(SurrogateEnt ent){
        surrogateMapper.update(ent,new QueryWrapper<SurrogateEnt>().lambda().eq(SurrogateEnt::getId,ent.getId()));
        return null;
    }
    public String createSurrogate(SurrogateEnt ent){
        ent.setId(UUID.randomUUID().toString().replaceAll("-",StringUtils.EMPTY));
        ent.setState(true);
        ent.setOdate(LocalDateTime.now());
        surrogateMapper.insert(ent);
        return null;
    }
    public String deleteSurrogate(String id){
        surrogateMapper.delete(new QueryWrapper<SurrogateEnt>().lambda().eq(SurrogateEnt::getId,id));
        return null;
    }
}
