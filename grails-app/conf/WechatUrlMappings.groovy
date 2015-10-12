class WechatUrlMappings {

    static mappings = {
        "/wechat"(controller: "wechat", parseRequest: true) {
            action = [GET: "echo", POST: "post"]
        }
        "/wechat_pay"(controller: "wechatPay", parseRequest: true) {
            action = [GET: "echo", POST: "callback"]
        }
        "/wechat_pay_result"(controller: "wechatPay", parseRequest: true) {
            action = [GET: "echo", POST: "result"]
        }
    }
}
