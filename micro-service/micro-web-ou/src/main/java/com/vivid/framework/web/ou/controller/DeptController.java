package com.vivid.framework.web.ou.controller;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.web.ou.dto.DeptInfoDto;
import com.vivid.framework.web.ou.entity.DeptInfoEnt;
import com.vivid.framework.web.ou.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Autowired
    DeptService deptService;
    @GetMapping("/list/p/{pid}")
    public ResponseResult<List<DeptInfoEnt>> getDeptsByParent(@PathVariable("pid")Integer pid) {
        return ResponseResult.successed(deptService.getListByPid(pid));
    }

    @PostMapping("/info/create")
    public ResponseResult createDeptInParent(@RequestBody DeptInfoDto dept) {
        if (dept.getParentId() == null)
            return ResponseResult.failed(null, "parent must be set!");

        if (dept.getManager() == null)
            return ResponseResult.failed(null, "manager must be set!");

        Integer id = deptService.createDeptInParent(dept);
        return ResponseResult.successed(id);
    }
}
