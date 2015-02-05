package org.grails.plugin.wechat

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.grails.plugin.wechat.annotation.VerificationRequired
import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.bean.MediaType
import org.grails.plugin.wechat.message.News
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.ImageUtils
import org.grails.plugin.wechat.util.JsonHelper

/**
 * Created by haihxiao on 2014/12/12.
 */
class WechatMediaService {
    private static final String UPLOAD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN"
    private static final String UPLOAD_VIDEO_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token=ACCESS_TOKEN"
    private static final String UPLOAD_URL = 'http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=_TYPE_'

    def wechatTokenService

    @VerificationRequired
    def uploadNews(News... articles) {
        uploadNews(articles.toList())
    }

    @VerificationRequired
    def uploadNews(Collection<News> articles) {
        AccessToken token = wechatTokenService.accessToken
        String url = UPLOAD_NEWS_URL.replace('ACCESS_TOKEN', token.accessToken)
        Map map = new HashMap()
        map.put('articles', articles)
        String result = HttpUtils.postJson(url, map)
        UploadResult up = JsonHelper.parseJson(result, UploadResult.class)
        log.info("News uploaded: ${result}")
        return up.mediaId
    }

    def upload(File imageFile, MediaType type) {
        AccessToken token = wechatTokenService.accessToken
        String url = UPLOAD_URL.replace('ACCESS_TOKEN', token.accessToken).replace('_TYPE_', type.name().toLowerCase())

        CloseableHttpClient httpclient = HttpClients.createDefault()
        try {
            HttpPost httppost = new HttpPost(url)
            FileBody bin = new FileBody(ImageUtils.bestForWechat(imageFile))
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("media", bin).build()
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost)
            try {
                HttpEntity resEntity = response.getEntity();
                String result
                if (resEntity != null) {
                    result = resEntity.content.getText('UTF-8')
                } else {
                    result = '{}'
                }
                log.info("Image ${imageFile.name} uploaded: ${result} - ${response.statusLine}")
                EntityUtils.consume(resEntity);
                UploadResult up = JsonHelper.parseJson(result, UploadResult.class)
                return up.mediaId
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    private static class UploadResult {
        String type
        String mediaId
        long createdAt
    }
}
