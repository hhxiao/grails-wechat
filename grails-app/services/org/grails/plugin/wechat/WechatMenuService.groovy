package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.bean.ReturnCode
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper

/**
 * Created by haihxiao on 2014/12/5.
 */
class WechatMenuService {
    private static final String MENU_CREATE_URL = 'https://api.weixin.qq.com/cgi-bin/menu/create?access_token='
    private static final String MENU_QUERY_URL = 'https://api.weixin.qq.com/cgi-bin/menu/get?access_token='
    private static final String MENU_DELETE_URL = 'https://api.weixin.qq.com/cgi-bin/menu/delete?access_token='

    def wechatTokenService

    void createMenu(String menuDefinitions) {
        AccessToken token = wechatTokenService.accessToken
        String url = MENU_CREATE_URL + token.accessToken
        String ret = HttpUtils.postJson(url, menuDefinitions)
        JsonHelper.parseJson(ret, ReturnCode.class)
    }

    String getMenu() {
        AccessToken token = wechatTokenService.accessToken
        String url = MENU_QUERY_URL + token.accessToken
        String ret = HttpUtils.get(url)
        JsonHelper.parseJson(ret, ReturnCode.class)
        ret
    }

    void deleteMenu() {
        AccessToken token = wechatTokenService.accessToken
        String url = MENU_DELETE_URL + token.accessToken
        String ret = HttpUtils.get(url)
        JsonHelper.parseJson(ret, ReturnCode.class)
    }
}
