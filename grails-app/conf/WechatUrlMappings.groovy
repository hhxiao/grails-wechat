class WechatUrlMappings {

    static mappings = {
        "/wechat"(controller: "wechat", parseRequest: true) {
            action = [GET: "echo", POST: "post"]
        }
        "/wechat_pay"(controller: "wechatPay", parseRequest: true) {
            action = [POST: "post"]
        }
    }
}
