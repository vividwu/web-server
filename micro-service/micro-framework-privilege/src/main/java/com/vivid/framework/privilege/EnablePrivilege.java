package com.vivid.framework.privilege;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启权限认证
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(AutoConfiguredControllerScannerRegistrar.class)
public @interface EnablePrivilege {
    /**
     *  获取controller下@PrivilegeMenu，@PrivilegeOperation注解的包名，默认不设置扫描所有的
     * @return
     */
    Class<?>[] packages() default {};
}
