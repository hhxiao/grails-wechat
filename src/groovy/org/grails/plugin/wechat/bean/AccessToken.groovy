package org.grails.plugin.wechat.bean
/**
 * Created by hhxiao on 2014/9/29.
 */
class AccessToken implements Serializable {
    String accessToken
    String refreshToken
    String openid
    String scope
    String unionid
    long expiresIn

    long getExpiresInMillis() {
        expiresIn * 1000
    }
}
