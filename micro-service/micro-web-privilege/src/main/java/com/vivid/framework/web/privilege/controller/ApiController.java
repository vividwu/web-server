package com.vivid.framework.web.privilege.controller;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.common.data.privilege.PrivilegeConfigItemBean;
import com.vivid.framework.common.data.privilege.RegistDto;
import com.vivid.framework.web.privilege.service.PrivilegeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    PrivilegeConfigService privilegeConfigService;
    //客户端启动后注册并发送权限定义表
    //Map<String,List<Map<String,String>>
    //{module:test1,[{key:01,priName:测试菜单1,type:menu,path:/a/b/c,remark:xxx}]
    @PostMapping(value = "/regist")
    public ResponseResult regist(@RequestBody PrivilegeConfigItemBean data) {
        //todo 查看开关，可以停止自动注册配置数据
        privilegeConfigService.registPrivilegeData(data);
        System.out.println(data);
        return ResponseResult.successed(null);
    }

    //权限配置端获取权限定义表信息
    public void getPrivilegeDefineTable(){

    }
}
