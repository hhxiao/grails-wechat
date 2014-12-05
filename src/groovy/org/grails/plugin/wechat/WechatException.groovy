package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.ReturnCode

/**
 * Created by haihxiao on 2014/9/29.
 */
class WechatException extends RuntimeException {
    ReturnCode err

    WechatException(ReturnCode err) {
        super("${err.errcode}.${err.errmsg}")
        this.err = err
    }
}
