package org.grails.plugin.wechat

/**
 * Created by haihxiao on 2014/12/5.
 */
class WechatUser {
    static constraints = {
        openid(maxSize: 64, unique: true)
        nickname(maxSize: 64, nullable: true)
        language(maxSize: 32, nullable: true)
        city(maxSize: 32, nullable: true)
        province(maxSize: 32, nullable: true)
        country(maxSize: 32, nullable: true)
        headimgurl(maxSize: 255, nullable: true)
        unionid(maxSize: 64, nullable: true)
    }

    int subscribe
    String openid
    String nickname
    int sex
    String language
    String city
    String province
    String country
    String headimgurl
    long subscribeTime
    String unionid
}
