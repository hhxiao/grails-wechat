package org.grails.plugin.wechat

import org.grails.plugin.wechat.token.AccessToken
import org.springframework.beans.factory.InitializingBean

import java.util.concurrent.TimeUnit

/**
 * Created by hhxiao on 2014/9/29.
 */
class WechatConfigService implements InitializingBean {
    def grailsApplication

    String appId
    String appSecret
    String appToken

    String getAccessToken() {
        return AccessToken.get(appId, appSecret).accessToken
    }

    @Override
    void afterPropertiesSet() throws Exception {
        appId = grailsApplication.config.grails?.wechat.app?.id?.toString()
        appSecret = grailsApplication.config.grails?.wechat.app?.secret?.toString()
        appToken = grailsApplication.config.grails?.wechat.app?.token?.toString()
        if(!appId || !appSecret || !appToken) {
            throw new IllegalStateException("Weixin AppId or AppSecret or AppToken is not configured")
        }
        TimerTask task = new TimerTask() {
            @Override
            void run() {
                // refresh token
                //
            }
        }
        Timer timer = new Timer()
        timer.schedule(task, TimeUnit.SECONDS.toMillis(15), TimeUnit.SECONDS.toMillis(7200))
    }
}
