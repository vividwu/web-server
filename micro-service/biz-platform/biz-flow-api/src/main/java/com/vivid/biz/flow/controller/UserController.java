package com.vivid.biz.flow.controller;

import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.dto.ou.UserInfoDto;
import com.vivid.biz.flow.dto.ou.UserUpdateDto;
import com.vivid.biz.flow.entity.ou.PostInfoEnt;
import com.vivid.biz.flow.service.UserServiceImpl;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @GetMapping("/list/dept/{id}")
    public ResponseResult<PageInfo<UserInfoDto>> getDeptsByParent(@PathVariable("id")Integer id) {
        return ResponseResult.successed(userService.getUsersByDeptId(id));
    }
    //全路径层级下的所有人员
    @GetMapping("/full/dept/{id}")
    public ResponseResult<PageInfo<UserInfoDto>> getFullDeptsByParent(@PathVariable("id")Integer id) {
        return ResponseResult.successed(userService.getUsersByFullDeptId(id));
    }
    @PostMapping("/info/create")
    public ResponseResult createUserInDept(@RequestBody UserInfoDto user) {
        if (user.getPosts() == null)
            return ResponseResult.failed(null, "user post must be set!");

        if (userService.findLoginName(user.getName()))
            return ResponseResult.failed(null, "has user named:" + user.getName());

        Integer id = userService.createUserInDept(user);
        return ResponseResult.successed(id);
    }
    @PostMapping("/info/update")
    public ResponseResult updateUserInDept(@RequestBody UserUpdateDto user) {
        if (user.getPosts() == null)
            return ResponseResult.failed(null, "user post must be set!");

        Integer id = userService.updateUserInDept(user);
        return ResponseResult.successed(id);
    }
    @GetMapping("/list/all_post")
    public ResponseResult<List<PostInfoEnt>> getAllPost() {
        return ResponseResult.successed(userService.getAllPost());
    }
    @GetMapping("/edit/user_info/{uid}")
    public ResponseResult<UserUpdateDto> getUserInfo4Update(@PathVariable("uid")Integer uid) {
        return ResponseResult.successed(userService.getUserInfo4Update(uid));
    }
}
