package com.vivid.biz.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.common.Contracts;
import com.vivid.biz.flow.config.web.RequestHolder;
import com.vivid.biz.flow.dto.flow.LayoutJsonDto;
import com.vivid.biz.flow.dto.flow.LayoutJsonNodeDto;
import com.vivid.biz.flow.dto.flow.ModelConfigDto;
import com.vivid.biz.flow.entity.apply.*;
import com.vivid.biz.flow.entity.code.CommonCodeEnt;
import com.vivid.biz.flow.entity.workflow.ProcessEnt;
import com.vivid.biz.flow.repository.apply.*;
import com.vivid.biz.flow.repository.workflow.ProcessMapper;
import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.security.dto.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlowDesignServiceImpl {
     @Autowired
     ProcessConfigMapper processConfigMapper;
     @Autowired
     ProcessMapper processMapper;
     @Autowired
     ProcessScriptMapper processScriptMapper;
     @Autowired
     ProcessFormConfigMapper processFormConfigMapper;
     @Autowired
     TableInfoMapper tableInfoMapper;
     @Autowired
     TableFieldsMapper tableFieldsMapper;
     @Autowired
     ProcessTableLinkMapper processTableLinkMapper;
     @Value("${workflow.host}")
     String workflowHost;
     @Autowired
     CodeServiceImpl codeService;

     @Value("${db.datasource.driver-class-name}")
     String dsClass;
     @Value("${db.datasource.url}")
     String dsUrl;
     @Value("${db.datasource.username}")
     String dsUsername;
     @Value("${db.datasource.password}")
     String dsPassword;

     public PageInfo<ProcessConfigEnt> getProcessConfigAllPage() {
          PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
          List<ProcessConfigEnt> list = processConfigMapper.selectList(new QueryWrapper<ProcessConfigEnt>()
                  .select(ProcessConfigEnt.class,x -> {
                       return !x.getColumn().equals("flow_design_xml") && !x.getColumn().equals("flow_design_layout");
                  }));
          return new PageInfo<>(list);
     }
     public ProcessConfigEnt getProcessConfigByName(String name){
          ProcessConfigEnt processConfigEnt = processConfigMapper.selectOne(new QueryWrapper<ProcessConfigEnt>().lambda()
                  .eq(ProcessConfigEnt::getProcessName, name));
          return processConfigEnt;
     }

     public ProcessEnt getProcessByName(String name){
          ProcessEnt processEnt = processMapper.selectOne(new QueryWrapper<ProcessEnt>()
                  .eq("name",name).orderByDesc("version").last("limit 1"));
          return processEnt;
     }

     public List<ProcessScriptEnt> getAllProcessScript(String category) {
          return processScriptMapper.selectList(new QueryWrapper<ProcessScriptEnt>().lambda()
                  .eq(ProcessScriptEnt::getCategory, category));
     }
     public List<ProcessScriptEnt> getProcessScriptByProcessName(String processName) {
          return processScriptMapper.selectList(new QueryWrapper<ProcessScriptEnt>().lambda()
                  .eq(ProcessScriptEnt::getProcessName, processName));
     }
     public List<ProcessFormConfigEnt> getProcessActionByProcessName(String processName) {
          return processFormConfigMapper.selectList(new QueryWrapper<ProcessFormConfigEnt>().lambda()
                  .eq(ProcessFormConfigEnt::getProcessName, processName));
     }
     public ProcessScriptEnt getProcessScriptContentByScriptName(String processName,String scriptName) {
          return processScriptMapper.selectOne(new QueryWrapper<ProcessScriptEnt>().lambda()
                  .eq(ProcessScriptEnt::getProcessName, processName)
          .eq(ProcessScriptEnt::getScriptName,scriptName));
     }
     public Integer updateFlowLayout(String processName, String json){
          LambdaQueryWrapper<ProcessConfigEnt> eq = new QueryWrapper<ProcessConfigEnt>().lambda()
                  .eq(ProcessConfigEnt::getProcessName, processName);
          ProcessConfigEnt processConfigEnt = processConfigMapper.selectOne(eq);
          processConfigEnt.setFlowDesignLayout(json);
          return processConfigMapper.update(processConfigEnt,eq);
     }
     @Transactional
     public String createScriptFile(ProcessScriptEnt processScriptEnt) {
          Integer integer = processScriptMapper.selectCount(new QueryWrapper<ProcessScriptEnt>().lambda()
                  .eq(ProcessScriptEnt::getProcessName, processScriptEnt.getProcessName())
                  .eq(ProcessScriptEnt::getScriptName, processScriptEnt.getScriptName()));
          if (integer > 0) {
               return processScriptEnt.getProcessName() + "下,已经存在脚本文件" + processScriptEnt.getScriptName();
          }
          processScriptEnt.setCreateTime(LocalDateTime.now());
          try {
               processScriptMapper.insert(processScriptEnt);
               //
               MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
               form.add("process_name", processScriptEnt.getProcessName());
               form.add("file_name",processScriptEnt.getScriptName()+".vds");
               String res = post2WorkflowEngine("/openapi/script/create",form);
               if (res != null) {
                    return "远程文件失败：" + res;
               }
          }catch (Exception ex){
               return ex.toString();
          }
          return null;
     }
     public String updateScriptContent(ProcessScriptEnt processScriptEnt) {
          LambdaQueryWrapper<ProcessScriptEnt> eq = new QueryWrapper<ProcessScriptEnt>().lambda()
                  .eq(ProcessScriptEnt::getProcessName, processScriptEnt.getProcessName())
                  .eq(ProcessScriptEnt::getScriptName, processScriptEnt.getScriptName());
          ProcessScriptEnt existEnt = processScriptMapper.selectOne(eq);
          if (existEnt == null) {
               return "没有该脚本数据,process:" + processScriptEnt.getProcessName()+",name:"+processScriptEnt.getScriptName();
          }
          existEnt.setRemark(processScriptEnt.getRemark());
          existEnt.setTestContent(processScriptEnt.getTestContent());
          existEnt.setScriptContent(processScriptEnt.getScriptContent());

          try {
               processScriptMapper.update(existEnt,eq);
               //
               String param = String.format("process_name=%s&file_name=%s&content=%s",
                       processScriptEnt.getProcessName(), processScriptEnt.getScriptName()+".vds",
                       URLEncoder.encode(processScriptEnt.getScriptContent(), "UTF-8"));
//               formSB.add("process_name", processScriptEnt.getProcessName());
//               formSB.add("file_name",processScriptEnt.getScriptName()+".vds");
//               formSB.add("content", processScriptEnt.getScriptContent());
               String res = post2WorkflowEngine("/openapi/script/write",param);
               if (res != null) {
                    return "远程修改失败：" + res;
               }
          }catch (Exception ex){
               return ex.toString();
          }
          return null;
     }
     @Transactional
     public String createModelConfig(String processName, TableInfoEnt info, List<TableFieldsEnt> fields, String category) throws Exception {
          if (info.getName().indexOf("fm_") != 0 || info.getName().indexOf("wf_") == 0 || info.getName().indexOf("wc_") == 0
                  || info.getName().length() <= 5) {
               return "必须长度大于5个字符，fm_开头，不能wf_、wc_开头" + info.getName();
          }
          Integer count = tableInfoMapper.selectCount(new QueryWrapper<TableInfoEnt>().lambda()
                  .eq(TableInfoEnt::getName, info.getName()));
          if (count > 0) {
               return "存在模型编码" + info.getName();
          }
          info.setId(UUID.randomUUID().toString());
          tableInfoMapper.insert(info);
          ProcessTableLinkEnt tableLinkEnt = new ProcessTableLinkEnt();
          tableLinkEnt.setTableId(info.getId());
          tableLinkEnt.setProcessName(processName);
          tableLinkEnt.setTableName(info.getName());
          tableLinkEnt.setCategory(category);
          processTableLinkMapper.insert(tableLinkEnt);
          fields.forEach(tableFieldsEnt -> {
               tableFieldsEnt.setTableName(info.getName());
               tableFieldsEnt.setId(UUID.randomUUID().toString());
               tableFieldsMapper.insert(tableFieldsEnt);
          });
          //查看步骤
          LambdaQueryWrapper<ProcessConfigEnt> eq = new QueryWrapper<ProcessConfigEnt>().lambda()
                  .eq(ProcessConfigEnt::getProcessName, processName);
          ProcessConfigEnt conf = processConfigMapper.selectOne(eq);
          if (conf.getStep() == Contracts.PROCESS_CONF_STEP_DEFIND)  //1:流程定义
               conf.setStep(Contracts.PROCESS_CONF_STEP_MODEL);  //2
          processConfigMapper.update(conf, eq);
          //创建物理表结构
          StringBuilder createSql = new StringBuilder("CREATE TABLE ");
          createSql.append(info.getName()).append("(");
          fields.forEach(field -> {
               createSql.append(field.getFieldName()).append(" ").append(field.getDataType())
                       .append("(").append(getFieldLenght(field.getDataType())).append("),");
          });
          String sql = createSql.substring(0, createSql.length() - 1);
          //try {
          Class.forName(dsClass);
          Connection conn = DriverManager.getConnection(dsUrl, dsUsername, dsPassword);
          //执行创建表
          Statement stmt = conn.createStatement();
          long res = stmt.executeLargeUpdate(sql + ")charset=utf8;");
          stmt.close();
          conn.close();
          return null;
          //return res > 0 ? null : "无法创建表结构";
          //} catch (Exception ex) {
//               log.error("创建表结构失败{}", ex);
//               return "创建表结构失败";
//          }
     }
     private int getFieldLenght(String fieldType){
          if("nvarchar".equals(fieldType)){
               return 255;
          }else if("int".equals(fieldType)){
               return 10;
          }else if("decimal".equals(fieldType)){
               return 10;
          }
          return 10;
     }
     public List<TableInfoEnt> getProcessModelTables(String processName){
          List<TableInfoEnt> tbs = null;
          List<ProcessTableLinkEnt> processTableLinkEnts = processTableLinkMapper.selectList(new QueryWrapper<ProcessTableLinkEnt>().lambda()
                  .eq(ProcessTableLinkEnt::getProcessName, processName));
          if(processTableLinkEnts != null && processTableLinkEnts.size()>0){
               List<String> ids = processTableLinkEnts.stream().map(x -> {
                    return x.getTableId();
               }).collect(Collectors.toList());
               tbs = tableInfoMapper.selectList(new QueryWrapper<TableInfoEnt>().lambda()
                       .in(TableInfoEnt::getId, ids));
          }
          return tbs;
     }
     public List<ProcessFormConfigEnt> getProcessFormTables(String processName){
          List<ProcessFormConfigEnt> processForm = processFormConfigMapper.selectList(new QueryWrapper<ProcessFormConfigEnt>().lambda()
                  .eq(ProcessFormConfigEnt::getProcessName, processName)
          .eq(ProcessFormConfigEnt::getStatus,1));
          return processForm;
     }
     @Transactional
     public String createProcessConfig(ProcessConfigEnt dto) {
          Integer count = processConfigMapper.selectCount(new QueryWrapper<ProcessConfigEnt>().lambda()
                  .eq(ProcessConfigEnt::getProcessName, dto.getProcessName()));
          if (count > 0) {
               return "存在该流程编码" + dto.getProcessName();
          }
          ProcessConfigEnt configEnt = new ProcessConfigEnt();
          //发布流程后更新成WF里的PID  configEnt.setProcessId(UUID.randomUUID().toString());
          configEnt.setName(dto.getName());
          configEnt.setProcessName(dto.getProcessName());
          configEnt.setCreateTime(LocalDateTime.now());
          configEnt.setUpdateTime(configEnt.getCreateTime());
          configEnt.setIcon(dto.getIcon());
          configEnt.setRemark(dto.getRemark());
          configEnt.setStep(1);
          configEnt.setStatus(0);
          try {
               processConfigMapper.insert(configEnt);
               //
               MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
               form.add("process_name", dto.getProcessName());
               String res = post2WorkflowEngine("/openapi/process/create",form);
               if (res!=null) {
                    return "远程目录失败：" + res;
               }
          }catch (Exception ex){
               return ex.toString();
          }
          return null;
     }
     public String deleteProcessModelLink(String modelName,String processName){
          processTableLinkMapper.delete(new QueryWrapper<ProcessTableLinkEnt>().lambda()
          .eq(ProcessTableLinkEnt::getTableName,modelName).eq(ProcessTableLinkEnt::getProcessName,processName));
          return null;
     }
     public String createFormConfig(ProcessFormConfigEnt dto) {
          Integer count = processFormConfigMapper.selectCount(new QueryWrapper<ProcessFormConfigEnt>().lambda()
                  .eq(ProcessFormConfigEnt::getProcessName, dto.getProcessName())
          .eq(ProcessFormConfigEnt::getFormCode,dto.getFormCode()));
          if (count > 0) {
               return "存在改流程编码" + dto.getProcessName();
          }
          ProcessFormConfigEnt configEnt = new ProcessFormConfigEnt();
          //configEnt.setProcessId(UUID.randomUUID().toString());
          configEnt.setProcessName(dto.getProcessName());
          configEnt.setFormCode(dto.getFormCode());
          configEnt.setCreateTime(LocalDateTime.now());
          configEnt.setFormMemo(dto.getFormMemo());
          configEnt.setOperation(dto.getOperation());
          configEnt.setStatus(1);
          processFormConfigMapper.insert(configEnt);
          //查看步骤
          LambdaQueryWrapper<ProcessConfigEnt> eq = new QueryWrapper<ProcessConfigEnt>().lambda()
                  .eq(ProcessConfigEnt::getProcessName, dto.getProcessName());
          ProcessConfigEnt conf = processConfigMapper.selectOne(eq);
          if (conf.getStep() == Contracts.PROCESS_CONF_STEP_MODEL)  //2:模型定义
               conf.setStep(Contracts.PROCESS_CONF_STEP_FORM);  //3
          processConfigMapper.update(conf, eq);
          return null;
     }
     public String updateProcessFormConfigStatus(String processName,String formCode,Integer status){
          LambdaQueryWrapper<ProcessFormConfigEnt> eq = new QueryWrapper<ProcessFormConfigEnt>().lambda()
                  .eq(ProcessFormConfigEnt::getProcessName, processName)
                  .eq(ProcessFormConfigEnt::getFormCode, formCode);
          ProcessFormConfigEnt processFormConfigEnt = processFormConfigMapper.selectOne(eq);
          processFormConfigEnt.setStatus(status);  //-1  1
          processFormConfigMapper.update(processFormConfigEnt,eq);
          return null;
     }
     public String updateFormLayout(String processName,String formCode,String layout,String config) {
          LambdaQueryWrapper<ProcessFormConfigEnt> eq = new QueryWrapper<ProcessFormConfigEnt>().lambda()
                  .eq(ProcessFormConfigEnt::getProcessName, processName)
                  .eq(ProcessFormConfigEnt::getFormCode, formCode);
          ProcessFormConfigEnt processFormConfigEnt = processFormConfigMapper.selectOne(eq);
          processFormConfigEnt.setContent(layout);
          processFormConfigEnt.setConfig(config);
          processFormConfigMapper.update(processFormConfigEnt, eq);
          //查看步骤
          LambdaQueryWrapper<ProcessConfigEnt> configQuery = new QueryWrapper<ProcessConfigEnt>().lambda()
                  .eq(ProcessConfigEnt::getProcessName, processName);
          ProcessConfigEnt conf = processConfigMapper.selectOne(configQuery);
          if (conf.getStep() == Contracts.PROCESS_CONF_STEP_FORM)  //3:自定义表单
               conf.setStep(Contracts.PROCESS_CONF_STEP_GRAPH);  //4
          processConfigMapper.update(conf, configQuery);
          return null;
     }
     public String publishProcess(String processName,String processDisplayName){
          LambdaQueryWrapper<ProcessConfigEnt> eq = new QueryWrapper<ProcessConfigEnt>().lambda()
                  .eq(ProcessConfigEnt::getProcessName, processName);
          ProcessConfigEnt conf = processConfigMapper.selectOne(eq);
          if(conf == null){
               return "没有找到该流程:"+processName;
          }
          if(conf.getStep()!=Contracts.PROCESS_CONF_STEP_GRAPH){
               return "完成所有步骤才能发布";
          }
          try {
               LayoutJsonDto jsonLayout = new ObjectMapper().readValue(conf.getFlowDesignLayout(), LayoutJsonDto.class);
               Map<String,List<String>> linkMap = new HashMap<>();  //节点name:<变迁to,name,display
               Arrays.stream(jsonLayout.getLinkDataArray()).forEach(x->{
                    List<String> list = linkMap.get(x.getFrom());
                    if(list == null) {
                         list = new ArrayList<>();
                    }
                    list.add(String.join(",",x.getTo(),x.getKey(),x.getText()));
                    linkMap.put(x.getFrom(),list);
               });
               StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
               sb.append("<process displayName=\"").append(processDisplayName);
               sb.append("\" name=\"").append(processName).append("\" instanceAction=\"\">");
               Arrays.stream(jsonLayout.getNodeDataArray()).forEach(x->{
                    switch (x.getType()) {
                         case "begin":
                              sb.append(createBeginNode(linkMap, x));
                              return;
                         case "end":
                              sb.append(createEndNode(x));
                              return;
                         case "task":
                              sb.append(createTaskNode(linkMap, x));
                              return;
                         case "decision":
                              sb.append(createDecisionNode(linkMap, x));
                              return;
                    }
               });
               sb.append("<layout>").append("<![CDATA[\n").append(conf.getFlowDesignLayout())
                       .append("\n]]></layout></process>");
               log.info(sb.toString());
               conf.setFlowDesignXml(sb.toString());
               conf.setUpdateTime(LocalDateTime.now());
               //调用工作流引擎发布新流程模板   redeploy仅更新流程图不生成新版本
               //if(StringUtils.isEmpty(conf.getProcessId())){
                    //deploy
                    try {
                         String param = String.format("xml=%s&creator=%s",
                                 URLEncoder.encode(conf.getFlowDesignXml(), "UTF-8"),
                                 ((SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAdditionalInformation().get("uid"));
                         Map map = post2WorkflowEngineMap("/api/apply/publish",param);
                         if(map == null){
                              return "工作流引擎发布调用失败";
                         }
                         if("false".equals(map.get("success").toString())){
                              return map.get("message").toString();
                         }else {
                              conf.setProcessId(map.get("data").toString());
                         }
                    } catch (Exception e) {
                         e.printStackTrace();
                         return "发布失败:"+e.toString();
                    }
//               }else{
//                    //再次重新deploy，生成新版本
//               }
               processConfigMapper.update(conf,eq);
          } catch (IOException e) {
               e.printStackTrace();
               return "解析流程图错误"+e.getMessage();
          }
          return null;
     }
     private StringBuilder createTransNode(Map<String,List<String>> linkMap, String nodeKey){
          StringBuilder sb = new StringBuilder();
          List<String> links = linkMap.get(nodeKey);
          if(links != null){
               links.forEach(link->{
                    String[] arr = link.split(",",3);
                    sb.append(createTransition(arr[0],arr[1],arr[2]));
               });
          }
          return sb;
     }
     private StringBuilder createBeginNode(Map<String,List<String>> linkMap, LayoutJsonNodeDto node){
          StringBuilder sb = new StringBuilder();
          sb.append("<start displayName=\"").append(node.getText()).append("\" ");
          sb.append("name=\"").append(node.getKey()).append("\">");
          sb.append(createTransNode(linkMap,node.getKey()));
          sb.append("</start>");
          return sb;
     }
     private StringBuilder createEndNode(LayoutJsonNodeDto node){
          StringBuilder sb = new StringBuilder();
          sb.append("<end displayName=\"").append(node.getText()).append("\" ");
          sb.append("name=\"").append(node.getKey()).append("\">");
          sb.append("</end>");
          return sb;
     }
     private StringBuilder createTaskNode(Map<String,List<String>> linkMap, LayoutJsonNodeDto node){
          StringBuilder sb = new StringBuilder();
          sb.append("<task displayName=\"").append(node.getText()).append("\" ");
          sb.append("name=\"").append(node.getKey()).append("\" ");
          sb.append("performType=\"").append(node.getConf().get("performType")).append("\" ");
          if("true".equals(node.getConf().get("autoExecute"))){
               sb.append("autoExecute=\"true\" ");
          }
          if(StringUtils.isNotEmpty(node.getConf().get("assigneeScript"))){
               sb.append("assigneeScript=\"").append(node.getConf().get("assigneeScript")).append("\" ");
          }
          if(StringUtils.isNotEmpty(node.getConf().get("assignee"))){
               sb.append("assignee=\"").append(node.getConf().get("assignee")).append("\" ");
          }
          if(StringUtils.isNotEmpty(node.getConf().get("remark"))){
               sb.append("remark=\"").append(node.getConf().get("remark")).append("\" ");
          }
          sb.append("action=\"").append(node.getConf().get("action")).append("\" ");
          sb.append(">");
          sb.append(createTransNode(linkMap,node.getKey()));
          sb.append("</task>");
          return sb;
     }
     private StringBuilder createDecisionNode(Map<String,List<String>> linkMap, LayoutJsonNodeDto node){
          StringBuilder sb = new StringBuilder();
          sb.append("<decision displayName=\"").append(node.getConf().get("displayName")).append("\" ");
          sb.append("name=\"").append(node.getKey()).append("\" ");
          sb.append("expScript=\"").append(node.getConf().get("expScript")).append("\" ");
          sb.append(">");
          sb.append(createTransNode(linkMap,node.getKey()));
          sb.append("</decision>");
          return sb;
     }
     private StringBuilder createTransition(String to,String name,String display){
          StringBuilder sb = new StringBuilder();
          sb.append("<transition name=\"").append(name);
          sb.append("\" to=\"").append(to).append("\" ");
          if (StringUtils.isNotEmpty(display)){
               sb.append("displayName=\"").append(display).append("\"");
          }
          sb.append("/>");
          return sb;
     }
     private String post2WorkflowEngine(String path,MultiValueMap<String, String> form)throws Exception{
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
          HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);
          String result = new RestTemplate().postForObject(workflowHost + path, requestEntity, String.class);
          Map map = new ObjectMapper().readValue(result, Map.class);
          if (map != null && !(Boolean) map.get("success")) {
               return map.get("message").toString();
          }
          return null;
     }
     private Map post2WorkflowEngineMap(String path,String urlParams)throws Exception {
          SecurityUser user = (SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
          Map userMap = user.getAdditionalInformation();
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  //10位Time,uid,udisplay
          String auth = String.format("bearer %d,%s,%s",new Date().getTime()/1000,userMap.get("uid"),userMap.get("udisplay"));
          headers.set("Authorization",auth);
          headers.set("aid","123");
          HttpEntity<?> requestEntity = new HttpEntity<>(urlParams, headers);
          String result = new RestTemplate().postForObject(workflowHost + path, requestEntity, String.class);
          //restTemplate.exchange(url, HttpMethod.POST, entity2, JSONObject.class);
          Map map = new ObjectMapper().readValue(result, Map.class);
          return map;
     }
     private String post2WorkflowEngine(String path,String urlParams)throws Exception {
          Map map = post2WorkflowEngineMap(path,urlParams);
          if (map != null && !(Boolean) map.get("success")) {
               return map.get("message").toString();
          }
          return null;
     }
     public List<Map> getProcessFormFieldsTree(String pname){
          List<ProcessTableLinkEnt> processTableLinkEnts = processTableLinkMapper.selectList(new QueryWrapper<ProcessTableLinkEnt>().lambda()
                  .eq(ProcessTableLinkEnt::getProcessName, pname));
          List<Map> list = new ArrayList<>();
          for(ProcessTableLinkEnt tb : processTableLinkEnts){
               TableInfoEnt tableInfoEnt = tableInfoMapper.selectOne(new QueryWrapper<TableInfoEnt>().lambda()
                       .eq(TableInfoEnt::getId, tb.getTableId()));
               Map<String,Object> tbMap = new HashMap<>();
               tbMap.put("value",tb.getTableName());
               tbMap.put("label",tableInfoEnt.getMemo());
               //字段
               List<TableFieldsEnt> tableFieldsEnts = tableFieldsMapper.selectList(new QueryWrapper<TableFieldsEnt>().lambda()
                       .eq(TableFieldsEnt::getTableName, tb.getTableName()));
               List<Map> fdList = new ArrayList<>();
               for(TableFieldsEnt field : tableFieldsEnts){
                    Map<String,String> fdMap = new HashMap<>();
                    fdMap.put("value",field.getFieldName());
                    fdMap.put("label",field.getMemo());
                    fdMap.put("required",field.getRequired());
                    fdList.add(fdMap);
               }
               tbMap.put("children",fdList);
               list.add(tbMap);
          }

          return list;
     }
     public ResponseResult testScript(String category, String context, String script){
          String param = null;
          Map res = null;
          try {
              //获取共享配置脚本
               String sc = codeService.getCommonScript("script_const");
               param = String.format("context=%s&script=%s&call=%s",
                       "let ctx = "+URLEncoder.encode(context, "UTF-8")+";",
                       URLEncoder.encode(sc+script, "UTF-8"),"decision".equals(category)?"DecisionCall(ctx);":"TaskCall(ctx);");
               res = post2WorkflowEngineMap("/openapi/script/test",param);
          } catch (Exception e) {
               e.printStackTrace();
               return ResponseResult.failed(res,"脚本服务失败");
          }
          if (res == null) {
               return ResponseResult.failed(res,"远程脚本调用失败");
          } else {
               if((Boolean)res.get("success")){
                    return ResponseResult.successed(res.get("data"));
               }else{
                    return ResponseResult.failed(null,res.get("message").toString());
               }
          }
     }
}

