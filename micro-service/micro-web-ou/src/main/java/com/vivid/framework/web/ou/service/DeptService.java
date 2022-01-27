package com.vivid.framework.web.ou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vivid.framework.web.ou.dto.DeptInfoDto;
import com.vivid.framework.web.ou.entity.DeptInfoEnt;
import com.vivid.framework.web.ou.entity.UserDeptEnt;
import com.vivid.framework.web.ou.mapper.DeptInfoMapper;
import com.vivid.framework.web.ou.mapper.UserDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptService {
    @Autowired
    DeptInfoMapper deptInfoMapper;
    @Autowired
    UserDeptMapper userDeptMapper;

    public List<DeptInfoEnt> getListByPid(Integer pid) {
        return deptInfoMapper.selectList(new QueryWrapper<DeptInfoEnt>()
                .lambda().eq(DeptInfoEnt::getParentId, pid));
    }
    @Transactional
    public Integer createDeptInParent(DeptInfoDto dept){
        //递增ID
        DeptInfoEnt max = deptInfoMapper.selectOne(new QueryWrapper<DeptInfoEnt>().orderByDesc("id").last("limit 1"));
        DeptInfoEnt ent = new DeptInfoEnt();
        ent.setId(max.getId()+1);
        //父节
        String fullId = "0";
        String fullName = "畅游天下";
        if(dept.getParentId()!=0) {
            DeptInfoEnt parent = deptInfoMapper.selectById(dept.getParentId());
            fullId = parent.getFullPathId();
        }
        ent.setFullPathId(fullId+"/"+ent.getId());
        ent.setFullPathName(fullName+"/"+dept.getName());
        ent.setName(dept.getName());
        ent.setParentId(dept.getParentId());
        ent.setCreateTime(LocalDateTime.now());
        deptInfoMapper.insert(ent);
        //负责人
        UserDeptEnt udEnt = new UserDeptEnt();
        udEnt.setDeptId(ent.getId());
        udEnt.setUserId(dept.getManager().getId());
        udEnt.setIsManager(1);
        userDeptMapper.insert(udEnt);
        return ent.getId();
    }
}