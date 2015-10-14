package org.grails.plugin.wechat.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Authors: Hai-Hua Xiao (hhxiao@gmail.com)
 * Date: 15/10/10
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PaymentHandler {
    String value() default "";
    boolean exclude() default false;
    int priority() default 0
}
