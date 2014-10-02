grails-wechat
=============

Grails Wechat Integration Plugin, 微信公众平台 Grails 插件 

## Introduction

目前提供的接口:

1. WechatTokenService(Token服务，获取access_token)
2. WechatResponseService(消息响应服务，发送响应消息给客户端)
3. WechatCustomerService(客服接口，发送客服消息给客户端)

## Installation

To install the plugin add a dependency to BuildConfig.groovy:
~~~~~~~~~~~
compile ":wechat:0.1"
~~~~~~~~~~~

To config wechat appId, appSecret and appToken in Config.groovy:
~~~~~~~~~~~
grails.wechat.app.id='wx..................'
grails.wechat.app.secret='856..................'
grails.wechat.app.token='token..................'
~~~~~~~~~~~

## Usage

Annotation based or conventional callback declaration

~~~~~~~~~~~groovy
class SampleService {
    def wechatResponseService

    @MessageHandler(value=MsgType.event, events=[EventType.subscribe, EventType.unsubscribe])
    ResponseMessage onSubscriptionChanged(EventMessage message) {
        wechatResponseService.responseText(message, (message.event == EventType.subscribe) ? '欢迎' : '再见')
    }

    ResponseMessage onText(TextMessage message) {
        wechatResponseService.responseText(message, "收到：" + message.content)
    }

    ResponseMessage onImage(ImageMessage message) {
        wechatResponseService.responseNews(message, new Article([
            title: "标题一", description: '描述一', picUrl: message.picUrl
        ]), new Article(
            title: "标题二", description: '描述二', picUrl: message.picUrl
        ))
    }

    @MessageHandler(value=MsgType.location)
    ResponseMessage onLocationReceived(LocationMessage message) {
        wechatResponseService.responseText(message, "收到位置消息: ${message.label}")
    }

    @MessageHandler(value=MsgType.event, events=[EventType.SCAN])
    ResponseMessage onScanned(EventMessage message) {
        wechatResponseService.responseText(message, "扫描了: ${message.eventKey}")
    }

    @MessageHandler(value=MsgType.event, events=[EventType.CLICK])
    ResponseMessage onMenuClicked(EventMessage message) {
        wechatResponseService.responseText(message, "点击了：${message.eventKey}")
    }

    @MessageHandler(value=MsgType.event, events=[EventType.VIEW])
    ResponseMessage onItemViewed(EventMessage message) {
        wechatResponseService.responseText(message, "查看了：${message.eventKey}")
    }

    @MessageHandler(value=MsgType.event, events=[EventType.LOCATION])
    ResponseMessage onLocationEvent(EventMessage message) {
        wechatResponseService.responseText(message, "收到位置事件: ${message.latitude}:${message.longitude}:${message.precision}")
    }
}

~~~~~~~~~~~

