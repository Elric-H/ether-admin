package com.baomidou.dynamic.datasource.annotation;

import java.lang.annotation.*;

/**
 *
 *
 * @author xuyuxiang
 * @date 2022/6/14 18:15
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DSTransactional {
}
