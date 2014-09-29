package org.grails.plugin.wechat.token

import org.grails.plugin.wechat.WeixinException
import org.grails.plugin.wechat.util.JsonHelper

/**
 * Created by hhxiao on 2014/9/29.
 */
class AccessToken {
    String accessToken
    long expiresIn

    static AccessToken get(String appId, String appSecret) throws WeixinException {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=${appId}&secret=${appSecret}"
//        HttpsURLConnection urlConnection = (HttpsURLConnection)new URL(url).openConnection()
//        String text = urlConnection.inputStream.text
        String text = "{\"access_token\":\"xxx\",\"expires_in\":124}"
        return JsonHelper.parseJson(text, AccessToken.class)
    }
}
