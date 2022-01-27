package com.vivid.framework.web.ou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.vivid.framework.web.ou.dto.PostInfoDto;
import com.vivid.framework.web.ou.dto.UserInfoDto;
import com.vivid.framework.web.ou.dto.UserPostDto;
import com.vivid.framework.web.ou.entity.*;
import com.vivid.framework.web.ou.mapper.*;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    PostInfoMapper postInfoMapper;
    @Autowired
    Mapper dMapper;
    @Autowired
    UserDeptPostMapper userDeptPostMapper;
    @Autowired
    UserPostMapper userPostMapper;

    public PageInfo<UserInfoDto> getUsersByDeptId(Integer deptId) {
        List<UserInfoEnt> users = userInfoMapper.getUserInfoByDeptId(deptId);
        if (users.size() > 0) {
            String[] ids = users.stream().map(userInfoEnt -> {
                return userInfoEnt.getId().toString();
            }).toArray(String[]::new);
            //用户列表所有的岗位
            List<UserPostDto> posts = postInfoMapper.getPostInfoByUserIds(String.join(",", ids));
            List<UserInfoDto> res = users.stream().map(x -> {
                UserInfoDto ud = dMapper.map(x, UserInfoDto.class);
                List<UserPostDto> myPosts = posts.stream().filter(p -> p.getUserId().equals(ud.getId())).collect(Collectors.toList());
                ud.setPosts(myPosts);
                return ud;
            }).collect(Collectors.toList());
            return new PageInfo<UserInfoDto>(res);
        }
        return null;
    }

    @Transactional
    public Integer createUserInDept(UserInfoDto user) {
        UserInfoEnt ent = new UserInfoEnt();
        ent.setName(user.getName());
        ent.setNum(user.getNum());
        ent.setGender("M");
        ent.setCreateTime(LocalDateTime.now());
        ent.setUpdateTime(LocalDateTime.now());
        userInfoMapper.insert(ent);
        for(UserPostDto post : user.getPosts()) {
            UserDeptPostEnt udpEnt = new UserDeptPostEnt();
            udpEnt.setDeptId(post.getDeptId());
            udpEnt.setUserId(ent.getId());
            udpEnt.setPostCode(post.getCode());
            udpEnt.setStatus("0");
            userDeptPostMapper.insert(udpEnt);
        }
        return ent.getId();
    }
}
