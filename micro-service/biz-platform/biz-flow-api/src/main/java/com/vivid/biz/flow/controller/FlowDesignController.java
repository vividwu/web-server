package com.vivid.biz.flow.controller;

import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.dto.flow.ModelConfigDto;
import com.vivid.biz.flow.entity.apply.ProcessConfigEnt;
import com.vivid.biz.flow.entity.apply.ProcessFormConfigEnt;
import com.vivid.biz.flow.entity.apply.ProcessScriptEnt;
import com.vivid.biz.flow.service.FlowDesignServiceImpl;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class FlowDesignController {
    @Autowired
    FlowDesignServiceImpl flowDesignService;
    //获取流程XML配置
    @RequestMapping(value = "/config/process/list_page", method = {RequestMethod.GET})
    public ResponseResult<PageInfo<ProcessConfigEnt>> getProcessConfigAllPage() {
        return ResponseResult.successed(flowDesignService.getProcessConfigAllPage());
    }
    //获取流程XML配置
    @RequestMapping(value = "/config/process/{process_name}", method = {RequestMethod.GET})
    public ResponseResult<ProcessConfigEnt> getProcessConfig(@PathVariable("process_name")String pname) {
        return ResponseResult.successed(flowDesignService.getProcessConfigByName(pname));
    }

    //获取所有流程脚本
    @RequestMapping(value = "/config/process/script/category", method = {RequestMethod.GET})
    public ResponseResult<List<ProcessScriptEnt>> getAllProcessScriptByCategory(String category) {
        return ResponseResult.successed(flowDesignService.getAllProcessScript(category));
    }
    //获取指定流程全部脚本
    @RequestMapping(value = "/config/process/script/porcess_scripts", method = {RequestMethod.GET})
    public ResponseResult<List<ProcessScriptEnt>> getProcessScriptByProcessName(String processName) {
        return ResponseResult.successed(flowDesignService.getProcessScriptByProcessName(processName));
    }
    //获取指定流程指定脚本
    @RequestMapping(value = "/config/process/script/content", method = {RequestMethod.GET})
    public ResponseResult<ProcessScriptEnt> getProcessScriptByScriptName(String processName,String scriptName) {
        return ResponseResult.successed(flowDesignService.getProcessScriptContentByScriptName(processName,scriptName));
    }
    //获取指定流程全部脚本
    @RequestMapping(value = "/config/process/action/porcess_actions", method = {RequestMethod.GET})
    public ResponseResult<List<ProcessFormConfigEnt>> getProcessActionByProcessName(String processName) {
        return ResponseResult.successed(flowDesignService.getProcessActionByProcessName(processName));
    }
    //创建一个新的脚本文件
    @RequestMapping(value = "/config/process/script/new_script_file", method = {RequestMethod.POST})
    public ResponseResult createScriptFile(@RequestBody ProcessScriptEnt scriptEnt) {
        if (scriptEnt == null || scriptEnt.getProcessName() == null || scriptEnt.getScriptName() == null || scriptEnt.getCategory() == null)
            return ResponseResult.failed(null, "processName,scriptName,category参数缺失");

        return ResponseResult.string(flowDesignService.createScriptFile(scriptEnt));
    }
    //保存脚本内容
    @RequestMapping(value = "/config/process/script/update_script_content", method = {RequestMethod.POST})
    public ResponseResult updateScriptContent(@RequestBody ProcessScriptEnt scriptEnt) {
        if (scriptEnt == null || scriptEnt.getProcessName() == null || scriptEnt.getScriptName() == null || scriptEnt.getCategory() == null)
            return ResponseResult.failed(null, "processName,scriptName,category参数缺失");

        return ResponseResult.successed(flowDesignService.updateScriptContent(scriptEnt));
    }
    //保存流程图设计时Layout json
    @RequestMapping(value = "/config/process/flow/layout", method = {RequestMethod.POST})
    public ResponseResult updateFlowLayout(@RequestBody ProcessConfigEnt config) {
        if(config == null || config.getProcessName() == null)
            return ResponseResult.failed(null,"没有要修改的数据");

        return ResponseResult.successed(flowDesignService.updateFlowLayout(config.getProcessName(),config.getFlowDesignLayout()));
    }
    //保存模型字段配置
    @RequestMapping(value = "/config/process/model/create", method = {RequestMethod.POST})
    public ResponseResult createModelConfig(@RequestBody ModelConfigDto model) throws Exception {
        String res = flowDesignService.createModelConfig(model.getProcessName(), model.getInfo(), model.getFields(), model.getCategory());
        return ResponseResult.string(res);
    }
    //获取指定流程下模型表列表
    @RequestMapping(value = "/config/process/model/table_list", method = {RequestMethod.GET})
    public ResponseResult getProcessModelTables(String pname){
        return  ResponseResult.successed(flowDesignService.getProcessModelTables(pname));
    }
    //获取指定流程下自定义表单表列表
    @RequestMapping(value = "/config/process/form/table_list", method = {RequestMethod.GET})
    public ResponseResult getProcessFormTables(String pname){
        return  ResponseResult.successed(flowDesignService.getProcessFormTables(pname));
    }
    //保存流程定义配置
    @RequestMapping(value = "/config/process/define/create", method = {RequestMethod.POST})
    public ResponseResult createProcessConfig(@RequestBody ProcessConfigEnt dto){
        String res = flowDesignService.createProcessConfig(dto);
        return ResponseResult.string(res);
    }
    //通过流程名和模型名删除关联关系
    @RequestMapping(value = "/config/process/model/del_link", method = {RequestMethod.POST})
    public ResponseResult deleteProcessModelLink(String modelName,String processName){
        String res = flowDesignService.deleteProcessModelLink(modelName,processName);
        return ResponseResult.string(res);
    }
    //新增表单定义配置
    @RequestMapping(value = "/config/process/form/create", method = {RequestMethod.POST})
    public ResponseResult createFormConfig(@RequestBody ProcessFormConfigEnt dto){
        String res = flowDesignService.createFormConfig(dto);
        return ResponseResult.string(res);
    }
    //删除表单定义配置
    @RequestMapping(value = "/config/process/form/status", method = {RequestMethod.POST})
    public ResponseResult deleteFormConfig(String processName,String formCode,Integer status){
        String res = flowDesignService.updateProcessFormConfigStatus(processName,formCode,status);
        return ResponseResult.string(res);
    }
    //更新表单定义布局配置内容
    @RequestMapping(value = "/config/process/form/layout", method = {RequestMethod.POST})
    public ResponseResult updateFormLayout(@RequestBody ProcessFormConfigEnt dto){
        String res = flowDesignService.updateFormLayout(dto.getProcessName(),dto.getFormCode(),dto.getContent(),dto.getConfig());
        return ResponseResult.string(res);
    }
    //发布流程
    @RequestMapping(value = "/config/process/publish", method = {RequestMethod.POST})
    public ResponseResult publishProcess(String processName,String processDisplayName){
        String res = flowDesignService.publishProcess(processName,processDisplayName);
        return ResponseResult.string(res);
    }
    //获取指定流程下模型2级下拉
    //    表名1-
    //         -字段名1
    @RequestMapping(value = "/config/process/form/fields_tree", method = {RequestMethod.GET})
    public ResponseResult getProcessFormFieldsTree(String pname){
        return  ResponseResult.successed(flowDesignService.getProcessFormFieldsTree(pname));
    }
    //运行测试脚本
    @RequestMapping(value = "/config/process/script/test", method = {RequestMethod.POST})
    public ResponseResult testScript(@RequestBody ProcessScriptEnt scriptEnt) {
        if (scriptEnt == null || scriptEnt.getCategory() == null || scriptEnt.getTestContent() == null || scriptEnt.getScriptContent() == null)
            return ResponseResult.failed(null, "processName,scriptName,category参数缺失");

        return flowDesignService.testScript(scriptEnt.getCategory(),scriptEnt.getTestContent(),scriptEnt.getScriptContent());
    }
}
