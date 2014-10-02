package org.grails.plugin.wechat.util

import groovy.util.slurpersupport.GPathResult
import org.grails.plugin.wechat.message.*

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
                Class<?> returnType = getter.getReturnType()
                Method setter = message.class.getMethod("set$name", returnType)
                def value = it.text().trim()

                if(returnType == long.class) {
                    setter.invoke(message, value.toLong())
                } else if(returnType == int.class) {
                    setter.invoke(message, value.toInteger())
                } else if(returnType == String.class) {
                    setter.invoke(message, value)
                } else if(returnType == EventType.class) {
                    setter.invoke(message, EventType.of(value))
                } else if(returnType.isEnum()) {
                    setter.invoke(message, Enum.valueOf(returnType, value))
                }
            } catch(NoSuchMethodException e) {}
        }
        return message
    }

    static Message fromXml(String text) {
        XmlSlurper slurper = new XmlSlurper()
        return fromGPathResult(slurper.parseText(text))
    }

    static Collection<MsgType> getApplicableMsgTypes(Class<? extends Message> messageClass) {
        if(messageClass == Message.class) return MsgType.values()
        String simpleName = messageClass.simpleName
        MsgType msgType = MsgType.valueOf(simpleName.substring(0, simpleName.length() - 'message'.length()).toLowerCase())
        return [msgType]
    }
}
