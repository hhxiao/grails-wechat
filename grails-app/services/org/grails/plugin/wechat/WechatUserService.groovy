package org.grails.plugin.wechat

import org.grails.plugin.wechat.annotation.VerificationRequired
import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
/**
 * Created by haihxiao on 2014/12/5.
 */
@VerificationRequired
class WechatUserService {
    private static final String USER_INFO_URL = 'https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPEN_ID&lang=zh_CN'
    private static final String GROUP_MEMBER_UPDATE_URL = 'https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=ACCESS_TOKEN'
    private static final String REMARK_URL = 'https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=ACCESS_TOKEN'

    def wechatTokenService

    void setUserRemark(String openId, String remark) {
        AccessToken token = wechatTokenService.accessToken
        String url = REMARK_URL.replace('ACCESS_TOKEN', token.accessToken)
        Map map = new HashMap()
        map.put('openid', openId)
        map.put('remark', remark)
        String result = HttpUtils.postJson(url, map)
        log.info("Set user remark: ${openId} -> ${remark} - ${result}")
    }

    WechatUser getUser(String openId) {
        String text = getUserInfo(openId)
        WechatUser user = WechatUser.findByOpenid(openId)
        if(!user) {
            user = JsonHelper.parseJson(text, WechatUser.class).save()
        } else {
            WechatUser u = JsonHelper.parseJson(text, WechatUser.class)
            user.properties = u.properties
            user.save()
        }
        return user
    }

    void updateGroup(String openId, int groupId) {
        AccessToken token = wechatTokenService.accessToken
        String url = GROUP_MEMBER_UPDATE_URL.replace('ACCESS_TOKEN', token.accessToken)
        Map map = new HashMap()
        map.put('openid', openId)
        map.put('to_groupid', groupId)
        String result = HttpUtils.postJson(url, map)
        log.info("Updated group member: ${openId} -> ${groupId} - ${result}")
    }

    private String getUserInfo(String openId) {
        AccessToken token = wechatTokenService.accessToken
        String url = USER_INFO_URL.replace('ACCESS_TOKEN', token.accessToken).replace('OPEN_ID', openId)
        HttpUtils.get(url)
    }
}
