package com.vivid.biz.flow.service;

import com.vivid.biz.flow.entity.privilege.PvgResourceEnt;
import com.vivid.biz.flow.entity.privilege.PvgRoleEnt;
import com.vivid.biz.flow.repository.privilege.PvgResourceMapper;
import com.vivid.framework.security.dto.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PrivilegeServiceImpl {
    @Autowired
    PvgResourceMapper pvgResourceMapper;

    public List<PvgResourceEnt> getUserMenu(){
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map userMap = user.getAdditionalInformation();
        return pvgResourceMapper.getUserMenu((Integer)userMap.get("uid"));
    }
}
