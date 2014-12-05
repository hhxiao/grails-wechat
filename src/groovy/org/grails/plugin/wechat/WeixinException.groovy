package org.grails.plugin.wechat

/**
 * Created by haihxiao on 2014/9/29.
 */
class WeixinException extends RuntimeException {
    Error err

    WeixinException(Error err) {
        super(err.errmsg)
        this.err = err
    }

    static class Error {
        String errcode
        String errmsg
    }
}
