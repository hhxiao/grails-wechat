package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.ReturnCode

/**
 * Created by haihxiao on 2014/9/29.
 */
class WeixinException extends RuntimeException {
    ReturnCode err

    WeixinException(ReturnCode err) {
        super(err.errmsg)
        this.err = err
    }

    static class Error {
        String errcode
        String errmsg
    }
}
