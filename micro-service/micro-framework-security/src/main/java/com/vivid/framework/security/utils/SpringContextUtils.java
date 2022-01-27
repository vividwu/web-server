package com.vivid.framework.security.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("SpringContextUtils...init");
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static <T> T getBeanIgnoreNotFound(String name) {
        T t = null;
        try {
            t = getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            // 忽略错误，不处理
        }
        return t;
    }

    public static <T> T getBeanIgnoreNotFound(Class<T> clazz) {
        T t = null;
        try {
            t = getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            // 忽略错误，不处理
        }
        return t;
    }

    public static <T> T getBeanIgnoreNotFound(String name, Class<T> clazz) {
        T t = null;
        try {
            t = getBean(name, clazz);
        } catch (NoSuchBeanDefinitionException e) {
            // 忽略错误，不处理
        }
        return t;
    }
}
