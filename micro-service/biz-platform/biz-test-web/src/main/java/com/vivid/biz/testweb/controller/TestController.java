package com.vivid.biz.testweb.controller;

import com.vivid.framework.privilege.annotation.PrivilegeMenu;
import com.vivid.framework.privilege.annotation.Privilege;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RefreshScope
@PrivilegeMenu(key=1001,name = "测试输出",remark = "测试输出页面")
@RequestMapping(value = "testApi")
public class TestController {
    @Value("${test.name}")
    private String name;

    @Privilege(key = 10001, priName = "测试输出1", remark = "测试输出页面", type = PrivilegeMenu.PrivilegeType.MENU)
    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String echo() {
        return name;
    }

    @Privilege(key = 10002,priName = "测试输出2", remark = "测试输出接口",type = PrivilegeMenu.PrivilegeType.OPERATION)
    @GetMapping(value = "/echo2")
    public String echo2() {
        return name;
    }
}
