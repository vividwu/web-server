package com.vivid.framework.web.ou.controller;

import com.github.pagehelper.PageInfo;
import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.web.ou.dto.UserInfoDto;
import com.vivid.framework.web.ou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/list/dept/{id}")
    public ResponseResult<PageInfo<UserInfoDto>> getDeptsByParent(@PathVariable("id")Integer id) {
        return ResponseResult.successed(userService.getUsersByDeptId(id));
    }

    @PostMapping("/info/create")
    public ResponseResult createUserInDept(@RequestBody UserInfoDto user) {
        if (user.getPosts() == null)
            return ResponseResult.failed(null, "user post must be set!");

        Integer id = userService.createUserInDept(user);
        return ResponseResult.successed(id);
    }
}
