package com.wusd.codegenerator.utils;

import org.springframework.beans.BeanUtils;

/**
 * @author Wusd
 * @date 2025/6/25
 * @description
 */
public class MyBeanUtils extends BeanUtils {
    public static <T> T convert(Object source, Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
            copyProperties(source, t);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return t;
    }
}
