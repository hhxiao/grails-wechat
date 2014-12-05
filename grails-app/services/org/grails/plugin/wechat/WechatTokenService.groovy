package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
import org.grails.plugin.wechat.util.SignatureHelper
import org.springframework.beans.factory.InitializingBean

import java.util.concurrent.TimeUnit

/**
 * Created by hhxiao on 2014/9/29.
 */
class WechatTokenService implements InitializingBean {
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APP_ID&secret=APP_SECRET"

    private static final int TOKEN_EXPIRATION_IN_MS = TimeUnit.MINUTES.toMillis(115)

    def grailsApplication

    private String appId
    private String appSecret
    private String appToken
    private AccessToken accessToken
    private Timer timer = new Timer()

    synchronized AccessToken getAccessToken() throws WechatException {
        if(accessToken == null) {
            // check cache first
            boolean reload = true
            int expiresInMs = TOKEN_EXPIRATION_IN_MS
            File tokenFile = new File("wechat-token.dat")
            if(tokenFile.file) {
                long lastModified = tokenFile.lastModified()
                long now = System.currentTimeMillis()
                expiresInMs = TOKEN_EXPIRATION_IN_MS + lastModified - now

                if(expiresInMs > 0) {
                    // not expired yet
                    reload = false
                    String text = tokenFile.text
                    accessToken = JsonHelper.parseJson(text, AccessToken.class)
                    log.info"AccessToken loaded: ${accessToken.accessToken}"
                }
            }

            if(reload) {
                String url = ACCESS_TOKEN_URL.replace('APP_ID', appId).replace('APP_SECRET', appSecret)
                String text = HttpUtils.get(url)
                accessToken = JsonHelper.parseJson(text, AccessToken.class)
                tokenFile.text = text
                log.info"AccessToken retrieved: ${accessToken.accessToken}"
            }

            // expire in about 2 hours
            Calendar cal = Calendar.getInstance()
            cal.add(Calendar.MILLISECOND, expiresInMs)
            timer.schedule(new TimerTask() {
                @Override
                void run() {
                    accessToken = null
                    log.info"AccessToken expired"
                }
            }, cal.time)

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
