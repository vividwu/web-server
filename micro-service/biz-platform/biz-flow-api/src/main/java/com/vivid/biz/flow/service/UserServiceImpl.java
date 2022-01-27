package com.vivid.biz.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.config.web.RequestHolder;
import com.vivid.biz.flow.dto.ou.*;
import com.vivid.biz.flow.entity.ou.*;
import com.vivid.biz.flow.repository.ou.*;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    PostInfoMapper postInfoMapper;
    @Autowired
    Mapper dMapper;
    @Autowired
    UserDeptMapper userDeptMapper;
    @Autowired
    UserDeptPostMapper userDeptPostMapper;
    @Autowired
    DeptInfoMapper deptInfoMapper;

    public PageInfo<UserInfoDto> getUsersByFullDeptId(Integer deptId) {
        DeptInfoEnt deptInfoEnt = deptInfoMapper.selectOne(new QueryWrapper<DeptInfoEnt>().lambda()
                .eq(DeptInfoEnt::getId, deptId));
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        List<UserInfoDto> users = userInfoMapper.getUserInfoByDeptFullId(deptInfoEnt.getFullPathId());
        PageInfo pages = new PageInfo<UserInfoDto>(users);
        PageHelper.clearPage();
        if (users.size() > 0) {
            String[] ids = users.stream().map(userInfoEnt -> {
                return userInfoEnt.getId().toString();
            }).toArray(String[]::new);
            //用户列表所有的岗位
            List<UserPostDto> posts = postInfoMapper.getPostInfoByUserIds(String.join(",", ids));
            List<UserInfoDto> res = users.stream().map(x -> {
                List<UserPostDto> myPosts = posts.stream().filter(p -> p.getUserId().equals(x.getId())).collect(Collectors.toList());
                x.setPosts(myPosts);
                return x;
            }).collect(Collectors.toList());
            return pages;
        }
        return null;
    }

    public PageInfo<UserInfoDto> getUsersByDeptId(Integer deptId) {
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        List<UserInfoDto> users = userInfoMapper.getUserInfoByDeptId(deptId);
        PageInfo pages = new PageInfo<UserInfoDto>(users);
        PageHelper.clearPage();
        if (users.size() > 0) {
            String[] ids = users.stream().map(userInfoEnt -> {
                return userInfoEnt.getId().toString();
            }).toArray(String[]::new);
            //用户列表所有的岗位
            List<UserPostDto> posts = postInfoMapper.getPostInfoByUserIds(String.join(",", ids));
            List<UserInfoDto> res = users.stream().map(x -> {
                //UserInfoDto ud = new UserInfoDto();
                //BeanUtils.copyProperties(x,ud);//dMapper.map(x, UserInfoDto.class);
                List<UserPostDto> myPosts = posts.stream().filter(p -> p.getUserId().equals(x.getId())).collect(Collectors.toList());
                x.setPosts(myPosts);
                return x;
            }).collect(Collectors.toList());
            return pages;
        }
        return null;
    }
    public UserUpdateDto getUserInfo4Update(Integer uId) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        UserInfoEnt user = userInfoMapper.selectOne(new QueryWrapper<UserInfoEnt>().lambda()
                .eq(UserInfoEnt::getId, uId));
        BeanUtils.copyProperties(user, userUpdateDto);
        //人员列表所有的岗位
        List<UserPostDto> posts = postInfoMapper.getPostInfoByUserIds(uId.toString());
        userUpdateDto.setPosts(posts);
        return userUpdateDto;
    }
    public boolean findLoginName(String userName){
        Integer integer = userInfoMapper.selectCount(new QueryWrapper<UserInfoEnt>().lambda()
                .eq(UserInfoEnt::getName, userName));
        return integer>0;
    }
    @Transactional
    public Integer createUserInDept(UserInfoDto user) {
        UserInfoEnt ent = new UserInfoEnt();
        ent.setName(user.getName());
        ent.setNum(user.getNum());
        ent.setDisplayName(user.getDisplayName());
        ent.setPassword(new BCryptPasswordEncoder().encode("123"));
        ent.setGender("M");
        ent.setCreateTime(LocalDateTime.now());
        ent.setUpdateTime(LocalDateTime.now());
        userInfoMapper.insert(ent);

        for(UserPostDto post : user.getPosts()) {
            UserDeptPostEnt udpEnt = new UserDeptPostEnt();
            udpEnt.setUserId(ent.getId());
            udpEnt.setPostCode(post.getCode());
            udpEnt.setDeptId(post.getDeptId());
            udpEnt.setStatus("0");
            userDeptPostMapper.insert(udpEnt);
        }
        return ent.getId();
    }

    public UserInfoEnt getUserById(String uid){
        UserInfoEnt userInfoEnt = userInfoMapper.selectOne(new QueryWrapper<UserInfoEnt>().lambda()
                .eq(UserInfoEnt::getId, uid));
        return userInfoEnt;
    }

    public List<PostInfoEnt> getAllPost(){
        return postInfoMapper.selectList(null);
    }

    @Transactional
    public Integer updateUserInDept(UserUpdateDto user) {
        LambdaQueryWrapper<UserInfoEnt> eq = new QueryWrapper<UserInfoEnt>().lambda()
                .eq(UserInfoEnt::getId, user.getId());
        UserInfoEnt ent = userInfoMapper.selectOne(eq);

        ent.setNum(user.getNum());
        ent.setDisplayName(user.getDisplayName());
        if(StringUtils.isNotEmpty(user.getPassword()))
            ent.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        ent.setGender("M");
        ent.setUpdateTime(LocalDateTime.now());
        userInfoMapper.update(ent,eq);
        Integer deptId = user.getPosts().get(0).getDeptId();
        //部门岗位
        userDeptPostMapper.delete(new QueryWrapper<UserDeptPostEnt>().lambda()
                .eq(UserDeptPostEnt::getUserId,user.getId()).eq(UserDeptPostEnt::getDeptId,deptId));
        for(UserPostDto post : user.getPosts()) {
            UserDeptPostEnt udpEnt = new UserDeptPostEnt();
            udpEnt.setUserId(ent.getId());
            udpEnt.setPostCode(post.getCode());
            udpEnt.setDeptId(post.getDeptId());
            udpEnt.setStatus("0");
            userDeptPostMapper.insert(udpEnt);
        }
        return ent.getId();
    }
}
