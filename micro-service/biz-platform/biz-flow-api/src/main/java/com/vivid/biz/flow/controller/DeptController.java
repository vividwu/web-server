package com.vivid.biz.flow.controller;

import com.vivid.biz.flow.config.web.RequestHolder;
import com.vivid.biz.flow.dto.ou.DeptInfoDto;
import com.vivid.biz.flow.dto.ou.UserUpdateDto;
import com.vivid.biz.flow.entity.ou.DeptInfoEnt;
import com.vivid.biz.flow.service.DeptServiceImpl;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Autowired
    DeptServiceImpl deptService;
    @GetMapping("/list/p/{pid}")
    public ResponseResult<List<DeptInfoEnt>> getDeptsByParent(@PathVariable("pid")Integer pid) {
        return ResponseResult.successed(deptService.getListByPid(pid));
    }
    @GetMapping("/list/all")  //直接给表单设计器控件使用，不需要包装ResponseResult
    public List<DeptInfoEnt> getDeptsByParent() {
        RequestHolder.setPage(0, 1000);
        return deptService.getListAll();
    }
    @PostMapping("/info/create")
    public ResponseResult createDeptInParent(@RequestBody DeptInfoDto dept) {
        if (dept.getParentId() == null)
            return ResponseResult.failed(null, "parent must be set!");

//        if (dept.getManager() == null)
//            return ResponseResult.failed(null, "manager must be set!");

        Integer id = deptService.createDeptInParent(dept);
        return ResponseResult.successed(id);
    }

    @PostMapping("/user/copy")
    public ResponseResult copyUser2Dept(Integer userId,Integer deptId,String posts) {
        return ResponseResult.string(deptService.copyUser2Dept(userId,deptId,posts));
    }
    @PostMapping("/post/remove")
    public ResponseResult removeUserDeptPost(Integer userId,Integer deptId,String postCode) {
        return ResponseResult.string(deptService.removeUserDeptPost(userId,deptId,postCode));
    }
}
