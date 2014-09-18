package org.grails.plugin.wechat

import org.springframework.beans.factory.InitializingBean

/**
 * Created by haihxiao on 17/9/14.
 */
class WechatService implements InitializingBean {
    def grailsApplication

    String apiServer
    String appId
    String appSecret

    @Override
    void afterPropertiesSet() throws Exception {
        apiServer = grailsApplication.config.wechat?.api?.url?.toString()
        appId = grailsApplication.config.wechat.api?.app?.id?.toString()
        appSecret = grailsApplication.config.wechat.api?.app?.secret?.toString()
        if(!appId || !appSecret) {
            throw new IllegalStateException("Weixin AppId or AppSecret is not configured")
        }
    }
}
