package com.vivid.framework.privilege.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//注解在方法上，拦截菜单配置（GET方式） class RequestMapping+method RequestMapping
//@Target(ElementType.METHOD)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivilegeMenu {
    /**
     * 顺序编号，尽量保持不变
     * @return
     */
    long key() default 0;  //ID，编号
    String name() default "";  //名称
    String remark() default "";  //描述
    PrivilegeType type() default PrivilegeType.MENU;
    public enum PrivilegeType {
        MENU,
        OPERATION
    }
}
