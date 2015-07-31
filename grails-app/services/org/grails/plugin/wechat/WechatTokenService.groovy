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

    private String _appId
    private String _appSecret
    private String _appToken

    private String _mchId

    private AccessToken accessToken
    private Timer timer = new Timer()
    private File tokenFile = null

    String getAppId() {
        _appId
    }

    String getMerchantId() {
        _mchId
    }

    synchronized AccessToken getAccessToken() throws WechatException {
        if(accessToken == null) {
            // check cache first
            boolean reload = true
            int expiresInMs = 0

            if(tokenFile == null) {
                tokenFile = new File("wechat-token.dat")
                if(tokenFile.file) {
                    String text = tokenFile.text
                    if(text) {
                        try {
                            AccessToken token = JsonHelper.parseJson(text, AccessToken.class)

                            long lastModified = tokenFile.lastModified()
                            long now = System.currentTimeMillis()
                            expiresInMs = token.expiresInMillis + lastModified - now

                            if(expiresInMs > 0) {
                                // not expired yet
                                reload = false
                                accessToken = token
                                log.info("AccessToken loaded: ${accessToken.accessToken}")
                            } else {
                                tokenFile.text = ""
                            }
                        } catch (Exception ignore) {}
                    }
                }
            }

            if(reload) {
                String url = ACCESS_TOKEN_URL.replace('APP_ID', appId).replace('APP_SECRET', appSecret)
                String text = HttpUtils.get(url)
                accessToken = JsonHelper.parseJson(text, AccessToken.class)
                tokenFile.text = text
                log.info("AccessToken retrieved: ${accessToken.accessToken}")
                expiresInMs = accessToken.expiresInMillis
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
        SignatureHelper.checkSignature(_appToken, signature, timestamp, nonce)
    }

    @Override
    void afterPropertiesSet() throws Exception {
        _appId = grailsApplication.config.grails?.wechat.app?.id?.toString()
        _appSecret = grailsApplication.config.grails?.wechat.app?.secret?.toString()
        _appToken = grailsApplication.config.grails?.wechat.app?.token?.toString()
        _mchId = grailsApplication.config.grails?.wechat.mch?.id?.toString()

        if(!_appId || !_appSecret || !_appToken) {
            throw new IllegalStateException("Weixin AppId or AppSecret or AppToken is not configured")
        }
    }
}
