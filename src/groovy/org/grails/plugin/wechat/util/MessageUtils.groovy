package org.grails.plugin.wechat.util
import groovy.util.slurpersupport.GPathResult
import org.grails.plugin.wechat.message.Message
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
    "${message.msgType.name()}": ${JsonHelper.toJson(message.getAdditionalResponseJson())}
}
"""
    }

    static Message fromGPathResult(GPathResult xml) {
        String type = xml.MsgType
        Message message = (Message)Thread.currentThread().getContextClassLoader().loadClass("${Message.package.name}.${type.capitalize()}Message").newInstance()

        xml.children().findAll {it.name() != 'MsgType'}.each {
            String name = it.name()
            try {
                Method getter = message.class.getMethod("get$name")
                Method setter = message.class.getMethod("set$name", getter.getReturnType())
                def value = it.text().trim()
                if(getter.getReturnType() == long.class) {
                    setter.invoke(message, value.toLong())
                } else if(getter.getReturnType().isEnum()) {
                    setter.invoke(message, Enum.valueOf(getter.getReturnType(), value))
                } else if(getter.getReturnType() == String.class) {
                    setter.invoke(message, value)
                }
            } catch(NoSuchMethodException e) {}
        }
        return message
    }

    static Message fromXml(String text) {
        XmlSlurper slurper = new XmlSlurper()
        return fromGPathResult(slurper.parseText(text))
    }

    static long generateMessageId() {
        System.nanoTime()
    }
}
