package org.grails.plugin.wechat.annotation
/**
 * Authors: Hai-Hua Xiao (hhxiao@gmail.com)
 * Date: 15/10/10
 **/
public @interface PaymentHandler {
    boolean exclude() default false;
    int priority() default 0
}
