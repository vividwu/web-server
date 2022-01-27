package com.vivid.framework.privilege;

import com.vivid.framework.common.data.privilege.PrivilegeConfigItem;
import com.vivid.framework.common.data.privilege.PrivilegeConfigItemBean;
import com.vivid.framework.privilege.annotation.PrivilegeMenu;
import com.vivid.framework.privilege.annotation.Privilege;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

public class AutoConfiguredControllerScannerRegistrar implements ResourceLoaderAware, ImportBeanDefinitionRegistrar, EnvironmentAware {
    //private BeanFactory beanFactory;
    private ResourceLoader resourceLoader;

    private Environment environment;
    public AutoConfiguredControllerScannerRegistrar() {
    }

    //=====1=====获取资源服务器接口的权限注解
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            /*if (!AutoConfigurationPackages.has(this.beanFactory)) {
                FramePrivilegeConfiguration.log.warn("Could not determine auto-configuration package, automatic privilegecontroller scanning disabled.");
            } else {
                FramePrivilegeConfiguration.log.warn("Searching for controllers annotated with @PrivilegeController");
                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                if (FramePrivilegeConfiguration.log.isDebugEnabled()) {
                    packages.forEach((pkg) -> {
                        FramePrivilegeConfiguration.log.warn("Using auto-configuration base package '{}'", pkg);
                    });
                }

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PrivilegeControllerScannerConfigurer.class);
                builder.addPropertyValue("annotationClass", Menu.class);
                builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
                //BeanWrapper beanWrapper = new BeanWrapperImpl(PrivilegeControllerScannerConfigurer.class);
                registry.registerBeanDefinition(PrivilegeControllerScannerConfigurer.class.getName(), builder.getBeanDefinition());
            }*/
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        Set<String> basePackages;
        Map<String, Object> attrs = importingClassMetadata
                .getAnnotationAttributes(EnablePrivilege.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                PrivilegeMenu.class);
        final Class<?>[] packages = attrs == null ? null
                : (Class<?>[]) attrs.get("packages");
        if (packages == null || packages.length == 0) {  //没有设置扫描的包名，按jar获取
            scanner.addIncludeFilter(annotationTypeFilter);
            basePackages = getBasePackages(importingClassMetadata);
        } else {  //有配置指定的包
            final Set<String> clientClasses = new HashSet<>();
            basePackages = new HashSet<>();
            for (Class<?> clazz : packages) {
                basePackages.add(ClassUtils.getPackageName(clazz));
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(
                    new AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
        }
        //扫描注解
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();

//                    Map<String, Object> attributes = annotationMetadata
//                            .getAnnotationAttributes(
//                                    PrivilegeMenu.class.getCanonicalName());
                    Set<MethodMetadata> methodMetadata = annotationMetadata.getAnnotatedMethods(Privilege.class.getCanonicalName());
                    if (methodMetadata == null)  //获取注解了权限的方法列表
                        continue;

                    Iterator<MethodMetadata> mItr = methodMetadata.iterator();
                    Map<String, Object> mappingDic = new HashMap<>();
                    List<PrivilegeConfigItem> privilegeConfigItemList = new ArrayList<>();
                    //获取Spring方法上的路由信息
                    while (mItr.hasNext()) {
                        MethodMetadata mData = mItr.next();
                        if (mData.isAnnotated(GetMapping.class.getCanonicalName())) {
                            mappingDic = mData.getAnnotationAttributes(GetMapping.class.getCanonicalName());
                        } else if (mData.isAnnotated(RequestMapping.class.getCanonicalName())) {
                            mappingDic = mData.getAnnotationAttributes(RequestMapping.class.getCanonicalName());
                        } else if (mData.isAnnotated(PostMapping.class.getCanonicalName())) {
                            mappingDic = mData.getAnnotationAttributes(PostMapping.class.getCanonicalName());
                        }
                        //增加权限注解的内容
                        mappingDic.putAll(mData.getAnnotationAttributes(Privilege.class.getCanonicalName()));
                        //类上定义的路由路径
                        if (annotationMetadata.isAnnotated(RequestMapping.class.getCanonicalName())) {
                            mappingDic.put("root", annotationMetadata.getAnnotationAttributes(RequestMapping.class.getCanonicalName()).get("value"));
                        }
                        //String className = annotationMetadata.getClassName();
                        privilegeConfigItemList.add(privilegeConfigItemBuilder(mappingDic));
                    }
                    //把解析出来的权限+路由数据添加到Bean中
                    BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(PrivilegeConfigItemBean.class);
                    definition.addPropertyValue("serName",environment.getProperty("spring.application.name"));
                    definition.addPropertyValue("privilegeConfigItemList", privilegeConfigItemList);
                    registry.registerBeanDefinition(PrivilegeConfigItemBean.class.getName(), definition.getBeanDefinition());
                    //annotationMetadata.getClass().getMethods()
                }
            }
        }
        //注册API
        ///regAPI();
    }

    private PrivilegeConfigItem privilegeConfigItemBuilder(Map<String, Object> attributes) {
        PrivilegeConfigItem privilegeConfigItem = new PrivilegeConfigItem();
//        definition.addPropertyValue("key", getKey(attributes));
//        definition.addPropertyValue("priName", getName(attributes));
//        definition.addPropertyValue("path", getValue(attributes));
//        definition.addPropertyValue("root", getRoot(attributes));
        privilegeConfigItem.setAKey(getKey(attributes));  //todo 如果没有抛出异常
        privilegeConfigItem.setPriName(getName(attributes));
        privilegeConfigItem.setPath(getValue(attributes));
        privilegeConfigItem.setRoot(getRoot(attributes));
        privilegeConfigItem.setParentKey(getParentKey(attributes));
        privilegeConfigItem.setType(getType(attributes));
        return  privilegeConfigItem;
    }
//    public void setBeanFactory(BeanFactory beanFactory) {
//        this.beanFactory = beanFactory;
//    }
private String resolve(String value) {
    if (StringUtils.hasText(value)) {
        return this.environment.resolvePlaceholders(value);
    }
    return value;
}
    private long getKey(Map<String, Object> attributes) {
        long key = Long.valueOf(resolve(attributes.get("key").toString()));
        return key;
    }
    private long getParentKey(Map<String, Object> attributes) {
        long key = Long.valueOf(resolve(attributes.get("parentKey").toString()));
        return key;
    }
    private String getName(Map<String, Object> attributes) {
        String name = resolve((String) attributes.get("priName"));
        return name;
    }
    private String getType(Map<String, Object> attributes) {
        String name = resolve(attributes.get("type").toString());
        return name;
    }
    private String getValue(Map<String, Object> attributes) {
        String name = resolve(((String[])attributes.get("value"))[0]);
        return name;
    }
    private String getRoot(Map<String, Object> attributes) {
        String name = resolve(((String[])attributes.get("root"))[0]);
        return name;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnablePrivilege.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
//        for (String pkg : (String[]) attributes.get("value")) {
//            if (StringUtils.hasText(pkg)) {
//                basePackages.add(pkg);
//            }
//        }
//        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
//        }
        return basePackages;
    }
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Helper class to create a {@link TypeFilter} that matches if all the delegates
     * match.
     */
    private static class AllTypeFilter implements TypeFilter {
        private final List<TypeFilter> delegates;
        /**
         * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
         *
         * @param delegates must not be {@literal null}.
         */
        public AllTypeFilter(List<TypeFilter> delegates) {
            Assert.notNull(delegates, "This argument is required, it must not be null");
            this.delegates = delegates;
        }
        @Override
        public boolean match(MetadataReader metadataReader,
                             MetadataReaderFactory metadataReaderFactory) throws IOException {
            for (TypeFilter filter : this.delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }
            return true;
        }
    }
}



