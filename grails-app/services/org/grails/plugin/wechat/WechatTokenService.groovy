package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
import org.grails.plugin.wechat.util.SignatureHelper
import org.springframework.beans.factory.InitializingBean
/**
 * Created by hhxiao on 2014/9/29.
 */
class WechatTokenService implements InitializingBean {
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APP_ID&secret=APP_SECRET"

    def grailsApplication

    private String appId
    private String appSecret
    private String appToken
    private AccessToken accessToken
    private Timer timer = new Timer()

    synchronized AccessToken getAccessToken() throws WeixinException {
        if(accessToken == null) {
            String url = ACCESS_TOKEN_URL.replace('APP_ID', appId).replace('APP_SECRET', appSecret)
            String text = HttpUtils.get(url)
            accessToken = JsonHelper.parseJson(text, AccessToken.class)

            // expire in about 2 hours
            Calendar cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, 110)
            timer.schedule(new TimerTask() {
                @Override
                void run() {
                    accessToken = null
                    log.info"AccessToken cleared"
                }
            }, cal.time)

            log.info"AccessToken retrieved: ${accessToken.accessToken}"
        }
        return accessToken
    }

    boolean checkSignature(String signature, String timestamp, String nonce) {
        SignatureHelper.checkSignature(appToken, signature, timestamp, nonce)
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
