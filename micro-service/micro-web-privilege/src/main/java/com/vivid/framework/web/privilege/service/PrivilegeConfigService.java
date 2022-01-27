package com.vivid.framework.web.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.vivid.framework.common.data.privilege.PrivilegeConfigItem;
import com.vivid.framework.common.data.privilege.PrivilegeConfigItemBean;
import com.vivid.framework.web.privilege.entity.PrivilegeConfigItemEnt;
import com.vivid.framework.web.privilege.mapper.PrivilegeConfigItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class PrivilegeConfigService {
    @Autowired
    PrivilegeConfigItemMapper privilegeConfigItemMapper;

    //之前作废的不清除，需要人为删
    public void registPrivilegeData(PrivilegeConfigItemBean pBean){
        List<PrivilegeConfigItem> newBeanList = new ArrayList<>();
        //先查重
        List<PrivilegeConfigItemEnt> oldList = privilegeConfigItemMapper.selectList(new QueryWrapper<PrivilegeConfigItemEnt>().lambda()
                .eq(PrivilegeConfigItemEnt::getSerName,pBean.getSerName()));
        if(oldList.size()>0){
            //对比两个列表
            for(PrivilegeConfigItem pc : pBean.getPrivilegeConfigItemList()) {  //循环本次注册对象
                PrivilegeConfigItemEnt exist = oldList.stream().filter(x -> {
                    return x.getaKey() == pc.getAKey() && x.getPath().equals(pc.getPath()) && x.getSerName().equals(pBean.getSerName());
                }).findFirst().orElse(null);
                if(exist != null){  //存在
                    //对比不同的
                    if(!pc.getPriName().equals(exist.getPriName()) || !pc.getRoot().equals(exist.getRoot())
                        || pc.getParentKey() != exist.getParentKey()){
                        UpdateWrapper<PrivilegeConfigItemEnt> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("a_key",pc.getAKey())
                                .eq("path",pc.getPath())
                                .set("pri_name", pc.getPriName()).set("root",pc.getRoot())
                                .set("parent_key",pc.getParentKey()).set("update_time",new Date());
                        privilegeConfigItemMapper.update(null,updateWrapper);
                        log.debug("privilege item {} change", pc.getAKey());
                    } else {
                        log.debug("privilege item {} did not change", pc.getAKey());
                    }
                } else {  // 本次新增
                    newBeanList.add(pc);
                }
            }
        } else {  //未注册过
            newBeanList = pBean.getPrivilegeConfigItemList();
        }

        for(PrivilegeConfigItem pci:newBeanList) {
            PrivilegeConfigItemEnt pe = fixBean2Ent(pci, pBean.getSerName());
            privilegeConfigItemMapper.insert(pe);
        }
    }

    private PrivilegeConfigItemEnt fixBean2Ent(PrivilegeConfigItem pci,String serverName){
        Date dt = new Date();
        PrivilegeConfigItemEnt pe = new PrivilegeConfigItemEnt();
        pe.setSerName(serverName);
        pe.setaKey(pci.getAKey());
        pe.setParentKey(pci.getParentKey());
        pe.setRoot(pci.getRoot());
        pe.setPath(pci.getPath());
        pe.setType(pci.getType());
        pe.setPriName(pci.getPriName());
        pe.setCreateTime(dt);
        pe.setUpdateTime(dt);
        return pe;
    }
}
