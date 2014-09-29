package org.grails.plugin.wechat

/**
 * Created by haihxiao on 2014/9/29.
 */
class WeixinException extends RuntimeException {
    String errcode
    String errmsg

    WeixinException() {
    }
}
