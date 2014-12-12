package org.grails.plugin.wechat.bean

/**
 * Created by haihxiao on 2014/12/12.
 */
enum MessageType {
    MPNEWS('mpnews'),
    IMAGE('image'),
    TEXT('text', 'content'),
    VOICE('voice'),
    VIDEO('video');

    final String title
    final String key

    MessageType(String title) {
        this(title, 'media_id')
    }

    MessageType(String title, String key) {
        this.title = title
        this.key = key
    }

    Map getContent(String content) {
        [(key): content]
    }
}
