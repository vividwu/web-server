package com.vivid.biz.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.vivid.biz.flow.dto.ou.DeptInfoDto;
import com.vivid.biz.flow.dto.ou.UserDeptDto;
import com.vivid.biz.flow.entity.ou.DeptInfoEnt;
import com.vivid.biz.flow.entity.ou.UserDeptEnt;
import com.vivid.biz.flow.entity.ou.UserDeptPostEnt;
import com.vivid.biz.flow.repository.ou.DeptInfoMapper;
import com.vivid.biz.flow.repository.ou.UserDeptMapper;
import com.vivid.biz.flow.repository.ou.UserDeptPostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
@Slf4j
@Service
public class DeptServiceImpl {
    @Autowired
    DeptInfoMapper deptInfoMapper;
    @Autowired
    UserDeptMapper userDeptMapper;
    @Autowired
    UserDeptPostMapper userDeptPostMapper;
    public List<UserDeptDto> getDeptListByUid(String uid){
        return deptInfoMapper.getUserAllDeptView(Integer.valueOf(uid));
    }

    public List<DeptInfoEnt> getListByPid(Integer pid) {
        return deptInfoMapper.selectList(new QueryWrapper<DeptInfoEnt>()
                .lambda().eq(DeptInfoEnt::getParentId, pid));
    }
    public List<DeptInfoEnt> getListAll() {
        return deptInfoMapper.selectList(null);
    }
    @Transactional
    public Integer createDeptInParent(DeptInfoDto dept){
        //递增ID
        PageHelper.clearPage();
        DeptInfoEnt max = deptInfoMapper.selectOne(new QueryWrapper<DeptInfoEnt>().orderByDesc("id").last("limit 1"));
        DeptInfoEnt ent = new DeptInfoEnt();
        ent.setId(max.getId()+1);
        //父节
        String fullId = "1";
        String fullName = "畅游天下";
        if(dept.getParentId()!=1) {
            DeptInfoEnt parent = deptInfoMapper.selectById(dept.getParentId());
            fullId = parent.getFullPathId();
            fullName = parent.getFullPathName();
        }
        ent.setFullPathId(fullId+"/"+ent.getId());
        ent.setFullPathName(fullName+"/"+dept.getName());
        ent.setName(dept.getName());
        ent.setParentId(dept.getParentId());
        ent.setCreateTime(LocalDateTime.now());
        deptInfoMapper.insert(ent);
        //负责人
//        UserDeptEnt udEnt = new UserDeptEnt();
//        udEnt.setDeptId(ent.getId());
//        udEnt.setUserId(dept.getManager().getId());
//        udEnt.setIsManager(1);
//        userDeptMapper.insert(udEnt);
        return ent.getId();
    }

    public String copyUser2Dept(Integer userId,Integer deptId,String posts) {
        String[] arr = posts.split(",");
        Arrays.stream(arr).forEach(x -> {
            Integer integer = userDeptPostMapper.selectCount(new QueryWrapper<UserDeptPostEnt>().lambda()
                    .eq(UserDeptPostEnt::getUserId, userId).eq(UserDeptPostEnt::getDeptId, deptId).eq(UserDeptPostEnt::getStatus, "0"));
            if (integer > 0) {
                log.info("部门下已存在该人员u:{},d:{},pcode:{}", userId, deptId, x);
            } else {
                UserDeptPostEnt udp = new UserDeptPostEnt();
                udp.setUserId(userId);
                udp.setDeptId(deptId);
                udp.setPostCode(x);
                udp.setStatus("0");
                userDeptPostMapper.insert(udp);
            }
        });
        return null;
    }

    public String removeUserDeptPost(Integer userId,Integer deptId,String postCode) {
        Integer count = userDeptPostMapper.selectCount(new QueryWrapper<UserDeptPostEnt>().lambda()
                .eq(UserDeptPostEnt::getUserId, userId).eq(UserDeptPostEnt::getStatus, "0"));
        if (count <= 1) {
            return "仅有的部门岗位不能删除";
        }
        userDeptPostMapper.delete(new QueryWrapper<UserDeptPostEnt>().lambda()
                .eq(UserDeptPostEnt::getUserId, userId).eq(UserDeptPostEnt::getDeptId, deptId).eq(UserDeptPostEnt::getPostCode, postCode));
        return null;
    }
}