package org.grails.plugin.wechat.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by haihxiao on 2014/12/5.
 */
/**
 * 用于标识接口服务是否需要微信认证
 */
@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface VerificationRequired {
}