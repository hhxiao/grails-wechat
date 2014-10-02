package org.grails.plugin.wechat
import org.grails.plugin.wechat.token.AccessToken
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
import org.springframework.beans.factory.InitializingBean
/**
 * Created by hhxiao on 2014/9/29.
 */
class WechatTokenService implements InitializingBean {
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APP_ID&secret=APP_SECRET"

    def grailsApplication

    String appId
    String appSecret
    String appToken

    private AccessToken get(String appId, String appSecret) throws WeixinException {
        String url = ACCESS_TOKEN_URL.replace('APP_ID', appId).replace('APP_SECRET', appSecret)
        String text = HttpUtils.get(url)
        return JsonHelper.parseJson(text, AccessToken.class)
    }

    @Override
    void afterPropertiesSet() throws Exception {
        appId = grailsApplication.config.grails?.wechat.app?.id?.toString()
        appSecret = grailsApplication.config.grails?.wechat.app?.secret?.toString()
        appToken = grailsApplication.config.grails?.wechat.app?.token?.toString()
        if(!appId || !appSecret || !appToken) {
            throw new IllegalStateException("Weixin AppId or AppSecret or AppToken is not configured")
        }
    }
}
