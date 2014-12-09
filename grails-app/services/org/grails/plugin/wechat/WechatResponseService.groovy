package org.grails.plugin.wechat
import org.grails.plugin.wechat.annotation.MessageHandler
import org.grails.plugin.wechat.message.*
/**
 * 微信消息回复接口
 *
 * Created by haihxiao on 17/9/14.
 */
class WechatResponseService {

    @MessageHandler(exclude = true)
    TextMessage responseText(Message message, String content) {
        TextMessage textMessage = initMessage(message, new TextMessage())
        textMessage.content = content
        return textMessage
    }

    @MessageHandler(exclude = true)
    ImageMessage responseImage(Message message, String mediaId) {
        ImageMessage imageMessage = initMessage(message, new ImageMessage())
        imageMessage.mediaId = mediaId
        return imageMessage
    }

    @MessageHandler(exclude = true)
    VoiceMessage responseVoice(Message message, String mediaId) {
        VoiceMessage voiceMessage = initMessage(message, new VoiceMessage())
        voiceMessage.mediaId = mediaId
        return voiceMessage
    }

    @MessageHandler(exclude = true)
    VideoMessage responseVideo(Message message, String mediaId, String title, String description) {
        VideoMessage videoMessage = initMessage(message, new VideoMessage())
        videoMessage.mediaId = mediaId
        videoMessage.title = title
        videoMessage.description = description
        return videoMessage
    }

    @MessageHandler(exclude = true)
    MusicMessage responseMusic(Message message, String mediaId, String title, String description,
                               String musicUrl, String hqMusicUrl) {
        MusicMessage musicMessage = initMessage(message, new MusicMessage())
        musicMessage.mediaId = mediaId
        musicMessage.title = title
        musicMessage.description = description
        musicMessage.musicUrl = musicUrl
        musicMessage.hqMusicUrl = hqMusicUrl
        return musicMessage
    }

    @MessageHandler(exclude = true)
    NewsMessage responseNews(Message message, Article... articles) {
        NewsMessage newsMessage = initMessage(message, new NewsMessage())
        newsMessage.articles = articles.toList()
        return newsMessage
    }

    @MessageHandler(exclude = true)
    NewsMessage responseNews(Message message, List<Article> articles) {
        NewsMessage newsMessage = initMessage(message, new NewsMessage())
        newsMessage.articles = articles
        return newsMessage
    }

    private static <T extends Message> T initMessage(Message request, T response) {
        response.fromUserName = request.toUserName
        response.toUserName = request.fromUserName
        return response
    }
}

