package com.vivid.biz.flow.controller;

import com.vivid.biz.flow.dto.flow.ApplySubmitDto;
import com.vivid.biz.flow.service.ApplyServiceImpl;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/ext")
@RestController
public class ApplyExtController {
    @Autowired
    ApplyServiceImpl applyService;

    //提交引擎接口，不存储数据
    @RequestMapping(value = "/apply/{process_name}/submit_raw", method = {RequestMethod.POST})
    public ResponseResult submitApply(@PathVariable("process_name") String pname, @RequestBody ApplySubmitDto dto) {
        return ResponseResult.string(applyService.submitApplyRaw(pname, dto));  //dto需要设置用户信息 emp_id/emp_display
    }
}
