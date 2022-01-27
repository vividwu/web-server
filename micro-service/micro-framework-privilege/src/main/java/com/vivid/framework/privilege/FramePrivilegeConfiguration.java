package com.vivid.framework.privilege;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivid.framework.common.data.privilege.PrivilegeConfigItemBean;
import com.vivid.framework.common.data.privilege.RegistDto;
import com.vivid.framework.privilege.nacos.InstanceDto;
import com.vivid.framework.privilege.nacos.ServiceInfoDto;
import com.vivid.framework.privilege.web.ApiProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@Configuration
@EnableConfigurationProperties(PrivilegeConfigProperties.class)//开启使用映射实体对象
//@ConditionalOnProperty//存在对应配置信息时初始化该配置类
//        (
//                prefix = "cfp",//存在配置前缀cfp.path
//                value = "open",//开启
//                matchIfMissing = true//缺失检查
//        )
public class FramePrivilegeConfiguration extends PrivilegeCheckConfigurerAdapter implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(FramePrivilegeConfiguration.class);
    @Autowired
    PrivilegeConfigProperties privilegeConfigProperties;
    //初始化正真权限处理的Bean
    IPrivilegeCheck privilegeCheck;

    @Bean
    @ConditionalOnMissingBean
    public NacosProperties nacos4Properties() {
        return new NacosProperties();
    }
    // 找com.**.controller*

    //=====2=====注册过滤器
    @Bean
    @ConditionalOnMissingBean(PrivilegeCheckFilter.class)
    public FilterRegistrationBean myFilter() {
        PrivilegeConfigItemBean bean = this.applicationContext.getBean(PrivilegeConfigItemBean.class);
        //如果被资源客户端重新，反射加载自定义逻辑
        if(privilegeConfigProperties.getCheckServiceImpl() != null){
            try {
                privilegeCheck = createCustomPrivilegeCheck(bean);
            } catch (Exception ex) {
                log.error("vivid privilege find custome impl error , check cfp.checkServiceImpl={}, impl IPrivilegeCheck error {}", ex);
            }
        } else {
            privilegeCheck = new PrivilegeCheckImpl();
            privilegeCheck.setConfigItem(bean);
        }
        log.warn("1.reg privilege config api for server:{}",bean.getSerName());
        //注册权限API配置
        try {
            regApi(nacos4Properties(), bean);
        }catch (Exception ex){
            log.error("privilege reg bean api err.",ex);
        }
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        log.warn("2.vivid privilege filter begin... privilege api serverName is:{},path is:{}, checkServiceImpl is:{}",
                privilegeConfigProperties.getServerName(),privilegeConfigProperties.getPath(),privilegeConfigProperties.getCheckServiceImpl());
        registrationBean.setFilter(new PrivilegeCheckFilter(privilegeCheck));
        //registrationBean.setUrlPatterns(Arrays.asList("/*"));
        //过滤应用程序中所有资源,当前应用程序根下的所有文件包括多级子目录下的所有文件
        registrationBean.addUrlPatterns("/*");
        //优先级设置高点让先执行认证
        registrationBean.setOrder(-900);
        return registrationBean;
    }
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public IPrivilegeCheck createCustomPrivilegeCheck(PrivilegeConfigItemBean bean) throws Exception {
        try {
            //"com.alibaba.nacos.client.naming.NacosNamingService"
            Class<?> driverImplClass = Class.forName(privilegeConfigProperties.getCheckServiceImpl());
            //Constructor constructor = driverImplClass.getConstructor(Properties.class);
            IPrivilegeCheck objImpl = (IPrivilegeCheck)driverImplClass.newInstance();
            driverImplClass.getMethod("setConfigItem",PrivilegeConfigItemBean.class).invoke(objImpl,bean);

            return objImpl;
        } catch (Exception ex) {
            throw ex;
        }
    }
    //todo 从网关获取权限中心？
    //{"hosts":[{"ip":"10.12.28.45","port":8825,"valid":true,"healthy":true,"marked":false,"instanceId":"10.12.28.45#8825#DEFAULT#DEFAULT_GROUP@@privilege-web","metadata":{"preserved.register.source":"SPRING_CLOUD"},"enabled":true,"weight":1.0,"clusterName":"DEFAULT","serviceName":"privilege-web","ephemeral":true}],"dom":"privilege-web","name":"DEFAULT_GROUP@@privilege-web","cacheMillis":3000,"lastRefTime":1609927693978,"checksum":"84a4f5a07c268177df709c1981315df2","useSpecifiedURL":false,"clusters":"","env":"","metadata":{}}
    private void regApi(NacosProperties properties, PrivilegeConfigItemBean bean) throws IOException {
        //随机找一个可用服务注册
        String[] urls = properties.getServerAddr().split(",");
        //todo 随机
        String url = "http://"+urls[0]+"/nacos/v1/ns/instance/list?serviceName=privilege-web";
        HttpClient.HttpResult res = HttpClient.httpGet(url,null,null,"utf-8");
        log.debug(res.content);
        ServiceInfoDto sInfo = new ObjectMapper().readValue(res.content, ServiceInfoDto.class);
        if(sInfo.getHosts().size()==0) {
            throw new IOException("from regist center get no host data");
        }
        //todo 随机
        InstanceDto randRegUrl =  sInfo.getHosts().get(0);
        String regUrl = "http://"+randRegUrl.getIp() +":"+ randRegUrl.getPort()+"/api/regist";
        String json = new ObjectMapper().writeValueAsString(bean);
        HttpClient.HttpResult regRes = HttpClient.httpPostJson(regUrl,null,json,"utf-8");
        log.debug(regRes.content);
    }
    //在注册中心获取所有可用的权限API接口
    private List<String> getAllPrivilegeRegApis(NacosProperties properties){
        return null;
    }
//    //处理注解
//    @Configuration
//    @Import({AutoConfiguredControllerScannerRegistrar.class})
//    @ConditionalOnMissingBean({PrivilegeControllerScannerConfigurer.class})  //不会被实例化，肯定执行
//    public static class ControllerScannerRegistrarNotFoundConfiguration implements InitializingBean {
//        public ControllerScannerRegistrarNotFoundConfiguration() {
//        }
//
//        public void afterPropertiesSet() {
//            FramePrivilegeConfiguration.log.debug("Not found configuration for registering controller bean using @ControllerScan, ControllerFactoryBean and ControllerScannerConfigurer.");
//        }
//    }
}
