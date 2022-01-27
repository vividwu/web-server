package com.vivid.biz.flow.controller;

import com.vivid.biz.flow.dto.ou.UserDeptDto;
import com.vivid.biz.flow.entity.ou.DeptInfoEnt;
import com.vivid.biz.flow.entity.ou.UserInfoEnt;
import com.vivid.biz.flow.service.BillcodeGenServiceImpl;
import com.vivid.biz.flow.service.CodeServiceImpl;
import com.vivid.biz.flow.service.DeptServiceImpl;
import com.vivid.biz.flow.service.UserServiceImpl;
import com.vivid.framework.security.dto.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequestMapping("/api")
@RestController
public class FormDesignComponentController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    DeptServiceImpl deptService;
    @Autowired
    BillcodeGenServiceImpl billcodeGenService;
    @Autowired
    CodeServiceImpl codeService;

    @RequestMapping(value = "/select", method = {RequestMethod.GET})
    public List<Map> select() {
        List<Map> list = IntStream.range(0, 10).mapToObj(value -> {
            Map map = new HashMap<String, String>();
            map.put("label", "lb:" + value);
            map.put("value", String.valueOf(value));
            return map;
        }).collect(Collectors.toList());
        return list;
    }

    @RequestMapping(value = "/user_dept_select", method = {RequestMethod.GET})
    public List<Map> userDeptSelect(String uid) {
//        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String uid = user.getAdditionalInformation().get("uid").toString();
        List<UserDeptDto> deptListByUid = deptService.getDeptListByUid(uid);
        List<Map> list = new ArrayList<>();
        deptListByUid.stream().forEach(x -> {
            Map map = new HashMap<String, String>();
            map.put("label", x.getFullPathName());
            map.put("value", String.valueOf(x.getDeptId()));
            list.add(map);
        });
        return list;
    }

    //字典配置列表项 控件获取
    @RequestMapping(value = "/common/dictionary/{query_code}", method = {RequestMethod.GET})
    public List<Map> getDicConfByQueryCode(@PathVariable("query_code")String queryCode) throws IOException {
        return codeService.getDicConfByQueryCode(queryCode);
    }

    @RequestMapping(value = "/common/billcode_gen/{process_name}", method = {RequestMethod.GET})
    public String billcode(@PathVariable("process_name")String pname) {
        return billcodeGenService.GetBillCode(pname);
    }

    @RequestMapping(value = "/user", method = {RequestMethod.GET})
    public UserInfoEnt userinfo(String uid) {
//        System.out.println("receive uid:"+uid);
//        Map<String,String> map = new HashMap<>();
//        map.put("id","8033");
//        map.put("name","vivid");
//        map.put("code","CY000001");
//        map.put("dept_id","0001");
//        map.put("dept_name","测试部门1");
//        map.put("dept_full_id","0/00001/0000231");
//        map.put("dept_full_name","畅游天下/研发部/测试部门1");
        return userService.getUserById(uid);
    }

    @RequestMapping(value = "/user_dept", method = {RequestMethod.GET})
    public List<UserDeptDto> userDeptList(String uid) {
        return deptService.getDeptListByUid(uid);
    }
}
