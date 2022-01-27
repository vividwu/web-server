package com.vivid.biz.testweb.config;

import com.vivid.biz.testweb.service.impl.CustomPrivilegeServiceImpl;
import com.vivid.framework.privilege.FramePrivilegeConfiguration;
import com.vivid.framework.privilege.IPrivilegeCheck;
import com.vivid.framework.privilege.PrivilegeCheckImpl;
import com.vivid.framework.privilege.PrivilegeManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class CustomPrivilegeConfig {
//    @Autowired
//    CustomPrivilegeServiceImpl customPrivilegeService;
//
//    @Bean
//    @ConditionalOnMissingBean
//    public IPrivilegeCheck privilegeCheck(){
//        return new CustomPrivilegeServiceImpl();
//    }
}
