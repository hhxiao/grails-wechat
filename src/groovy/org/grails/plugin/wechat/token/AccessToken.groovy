package org.grails.plugin.wechat.token

import org.grails.plugin.wechat.WeixinException
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper

/**
 * Created by hhxiao on 2014/9/29.
 */
class AccessToken {
    String accessToken
    long expiresIn
}
