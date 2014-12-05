package org.grails.plugin.wechat

import org.grails.plugin.wechat.annotation.VerificationRequired
import org.grails.plugin.wechat.token.AccessToken
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
/**
 * Created by haihxiao on 2014/12/5.
 */
@VerificationRequired
class WechatUserService {
    private static final String USER_INFO_URL = 'https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPEN_ID&lang=zh_CN'

    def wechatTokenService

    WechatUser getUser(String openId) {
        WechatUser user = WechatUser.findByOpenid(openId)
        if(!user) {
            String text = getUserInfo(openId)
            user = JsonHelper.parseJson(text, WechatUser.class).save()
        }
        return user
    }

    void refreshUser(String openId) {
        String text = getUserInfo(openId)
        WechatUser user = WechatUser.findByOpenid(openId)
        if(!user) {
            JsonHelper.parseJson(text, WechatUser.class).save()
        } else {
            WechatUser u = JsonHelper.parseJson(text, WechatUser.class)
            user.properties = u.properties
            user.save()
        }
    }

    private String getUserInfo(String openId) {
        AccessToken token = wechatTokenService.accessToken
        String url = USER_INFO_URL.replace('ACCESS_TOKEN', token.accessToken).replace('OPEN_ID', openId)
        HttpUtils.get(url)
    }
}
