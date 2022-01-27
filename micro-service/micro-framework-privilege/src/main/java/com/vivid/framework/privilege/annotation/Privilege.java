package com.vivid.framework.privilege.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Privilege {
    long key() default 0;  //菜单主键，必填
    String priName() default "";
    long parentKey() default 0;
    String remark() default "";
    PrivilegeMenu.PrivilegeType type() default PrivilegeMenu.PrivilegeType.MENU;
    public enum PrivilegeType {
        MENU,
        OPERATION
    }
}
