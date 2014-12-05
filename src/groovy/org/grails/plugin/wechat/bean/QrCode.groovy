package org.grails.plugin.wechat.bean

/**
 * Created by haihxiao on 2014/12/5.
 */
class QrCode implements Serializable {
    String ticket
    int expireSeconds
    String url
}
