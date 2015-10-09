package org.grails.plugin.wechat
import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
import org.grails.plugin.wechat.util.SignatureHelper
import org.grails.plugin.wechat.util.StringUtils
import org.springframework.beans.factory.InitializingBean
/**
 * Created by hhxiao on 2014/9/29.
 */
class WechatTokenService implements InitializingBean {
    static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APP_ID&secret=APP_SECRET"
    static final String OAUTH_TOKEN_API = 'https://api.weixin.qq.com/sns/oauth2/access_token?appid=APP_ID&secret=APP_SECRET&code=CODE&grant_type=authorization_code'
    static final String REFRESH_TOKEN_API = 'https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APP_ID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN'
    static final String AUTH_URL = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=APP_ID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect'
    static final String SNSAPI_BASE_SCOPE = 'snsapi_base'
    static final String SNSAPI_USERINFO_SCOPE = 'snsapi_userinfo'

    def grailsApplication

    private String _appId
    private String _appSecret
    private String _appToken

    private String _payMchId
    private String _payKey
    private String _payIp
    private String _payCallback

    private AccessToken accessToken
    private Timer timer = new Timer()
    private File tokenFile = null

    String getAppId() {
        _appId
    }

    String getAppSecret() {
        _appSecret
    }

    String getAppToken() {
        _appToken
    }

    String getMerchantId() {
        _payMchId
    }

    String getPaymentKey() {
        _payKey
    }

    String getPaymentIp() {
        _payIp ?: InetAddress.getLocalHost().hostAddress
    }

    String getPaymentCallback() {
        _payCallback
    }

    String getAuthUrl(String redirectUrl, String scope, String state = '') {
        AUTH_URL.replace('APP_ID', _appId).replace('REDIRECT_URI', redirectUrl).replace('SCOPE', scope).replace('STATE', state ?: StringUtils.randomString(6))
    }

    String getAuthBaseUrl(String redirectUrl, String state = '') {
        getAuthUrl(redirectUrl, SNSAPI_BASE_SCOPE, state)
    }

    String getAuthUserInfoUrl(String redirectUrl, String state = '') {
        getAuthUrl(redirectUrl, SNSAPI_USERINFO_SCOPE, state)
    }

    AccessToken getAccessToken(String code) {
        String url = OAUTH_TOKEN_API.replace('APP_ID', _appId)
                .replace('APP_SECRET', _appSecret)
                .replace('CODE', code)
        String ret = HttpUtils.get(url)
        JsonHelper.parseJson(ret, AccessToken.class)
    }

    AccessToken refreshToken(AccessToken token) {
        String url = REFRESH_TOKEN_API.replace('APP_ID', _appId)
                .replace('REFRESH_TOKEN', token.refreshToken)
        String ret = HttpUtils.get(url)
        JsonHelper.parseJson(ret, AccessToken.class)
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
                String url = ACCESS_TOKEN_URL.replace('APP_ID', appId).replace('APP_SECRET', _appSecret)
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

        _payMchId = grailsApplication.config.grails?.wechat.pay?.mch?.toString()
        _payKey = grailsApplication.config.grails?.wechat.pay?.key?.toString()
        _payIp = grailsApplication.config.grails?.wechat.pay?.ip?.toString()
        _payCallback = grailsApplication.config.grails?.wechat.pay?.callback?.toString()

        if(!_appId || !_appSecret || !_appToken) {
            throw new IllegalStateException("Weixin AppId or AppSecret or AppToken is not configured")
        }
    }
}
