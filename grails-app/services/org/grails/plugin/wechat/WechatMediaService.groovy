package org.grails.plugin.wechat

import org.grails.plugin.wechat.annotation.VerificationRequired
import org.grails.plugin.wechat.bean.MediaType
import org.grails.plugin.wechat.message.News
/**
 * Created by haihxiao on 2014/12/12.
 */
class WechatMediaService {
    private static final String UPLOAD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token="
    private static final String UPLOAD_VIDEO_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token="
    private static final String UPLOAD_URL = 'http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=_TYPE_'

    def wechatTokenService

    @VerificationRequired
    def uploadNews(News... articles) {
        uploadNews(articles.toList())
    }

    @VerificationRequired
    def uploadNews(Collection<News> articles) {

    }

    def upload(InputStream inputStream, MediaType type) {
        String url = UPLOAD_URL.replace('ACCESS_TOKEN', wechatTokenService.accessToken).replace('_TYPE_', type.name().toLowerCase())

    }
}
