package org.grails.plugin.wechat.session

import org.grails.plugin.wechat.util.WechatSessionHelper

/**
 * Created by haihxiao on 2015/1/10.
 */
class WechatSession extends HashMap<Object, Object> {
    private String wechatId

    WechatSession(String wechatId) {
        this.wechatId = wechatId
    }

    String getWechatId() {
        return wechatId
    }

    /**
     * Get the current session.
     * @return the session
     */
    public static WechatSession getCurrent() {
        return WechatSessionHelper.getCurrent();
    }
}
