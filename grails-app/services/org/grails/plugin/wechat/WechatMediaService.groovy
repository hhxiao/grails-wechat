package org.grails.plugin.wechat

import org.grails.plugin.wechat.annotation.VerificationRequired
import org.grails.plugin.wechat.message.News

/**
 * Created by haihxiao on 2014/12/12.
 */
@VerificationRequired
class WechatMediaService {
    private static final String UPLOAD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token="
    private static final String UPLOAD_VIDEO_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token="

    def wechatTokenService

    def uploadNews(News... articles) {
        uploadNews(articles.toList())
    }

    def uploadNews(Collection<News> articles) {

    }

    def uploadVideo(Collection<News> articles) {

    }
}
