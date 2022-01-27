package com.vivid.biz.flow.controller;

import com.vivid.biz.flow.entity.privilege.PvgResourceEnt;
import com.vivid.biz.flow.repository.privilege.PvgResourceMapper;
import com.vivid.biz.flow.service.PrivilegeServiceImpl;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/pvg")
@RestController
public class PrivilegeController {
    @Autowired
    PrivilegeServiceImpl privilegeService;

    @RequestMapping(value = "/menu/all", method = {RequestMethod.GET})
    public ResponseResult<List<PvgResourceEnt>> getUserMenu() {
        return ResponseResult.successed(privilegeService.getUserMenu());
    }
}
