package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.bean.ReturnCode
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper

/**
 * Created by haihxiao on 2014/12/5.
 */
class WeixinMenuService {
    private static final String MENU_CREATE_URL = 'https://api.weixin.qq.com/cgi-bin/menu/create?access_token='
    private static final String MENU_QUERY_URL = 'https://api.weixin.qq.com/cgi-bin/menu/get?access_token='
    private static final String MENU_DELETE_URL = 'https://api.weixin.qq.com/cgi-bin/menu/delete?access_token='

    def wechatTokenService

    void createMenu(String menuDefinitions) {
        AccessToken token = wechatTokenService.accessToken
        String url = MENU_CREATE_URL + token.accessToken
        String ret = HttpUtils.post(url, 'application/json', JsonHelper.toJson(menuDefinitions))
        ReturnCode code = JsonHelper.parseJson(ret, ReturnCode.class)
        if(code.errcode) {
            throw new WeixinException(code)
        }
    }

    String getMenu() {
        AccessToken token = wechatTokenService.accessToken
        String url = MENU_QUERY_URL + token.accessToken
        HttpUtils.get(url)
    }

    void deleteMenu() {
        AccessToken token = wechatTokenService.accessToken
        String url = MENU_DELETE_URL + token.accessToken
        String ret = HttpUtils.get(url)
        ReturnCode code = JsonHelper.parseJson(ret, ReturnCode.class)
        if(code.errcode) {
            throw new WeixinException(code)
        }
    }
}
