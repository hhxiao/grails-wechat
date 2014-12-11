package org.grails.plugin.wechat.annotation

import org.grails.plugin.wechat.message.EventType
import org.grails.plugin.wechat.message.MsgType

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by haihxiao on 2014/9/30.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandler {
    MsgType[] value() default [];
    EventType[] events() default [];
    String[] keys() default [];
    boolean exclude() default false;
    int priority() default 0
}
