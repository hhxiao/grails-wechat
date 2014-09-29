package org.grails.plugin.wechat.util

import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.message.MsgType
import org.grails.plugin.wechat.message.ResponseMessage

import java.lang.reflect.Method

/**
 * Created by haihxiao on 2014/9/29.
 */
class MessageUtils {
    static String toXml(ResponseMessage message) {
        """<xml>
 <ToUserName><![CDATA[${message.toUserName}]]></ToUserName>
 <FromUserName><![CDATA[${message.fromUserName}]]></FromUserName>
 <CreateTime>${message.createTime}</CreateTime>
 <MsgType><![CDATA[${message.msgType.name()}]]></MsgType>
 ${message.getAdditionalResponseXml()}
 <MsgId>${message.msgId}</MsgId>
 </xml>
"""
    }

    static String toJson(ResponseMessage message) {
        """{
    "touser":"${message.toUserName}",
    "msgtype":"${message.msgType.name()}",
    "${message.msgType.name()}":
    {
        ${JsonHelper.toJson(message.getAdditionalResponseJson())}
    }
}
"""
    }

    static Message fromXml(String text) {
        XmlSlurper slurper = new XmlSlurper()
        def xml = slurper.parseText(text)
        String type = xml.MsgType
        MsgType msgType = MsgType.valueOf(type)
        Message message = (Message)Thread.currentThread().getContextClassLoader().loadClass("${Message.package.name}.${type.capitalize()}Message").newInstance()
        message.msgType = msgType
        println message.class
        message.class.methods.toList().each { println it.name }
        xml.children().findAll {it.name() != 'MsgType'}.each {
            String name = it.name()
            Method getter = message.class.getMethod("get$name")
            Method setter = message.class.getMethod("set$name", getter.getReturnType())
            def value = it.text().trim()
            if(getter.getReturnType() == long.class) {
                setter.invoke(message, value.toLong())
            } else if(getter.getReturnType().isEnum()) {
                setter.invoke(message, Enum.valueOf(getter.getReturnType(), value))
            } else {
                setter.invoke(message, value)
            }
        }
        return message
    }

    static long generateMessageId() {
        0
    }
}
