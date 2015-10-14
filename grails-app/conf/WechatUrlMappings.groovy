class WechatUrlMappings {

    static mappings = {
        "/wechat"(controller: "wechat", parseRequest: true) {
            action = [GET: "echo", POST: "post"]
        }
        "/wepay/order"(controller: "wxpay", parseRequest: true) {
            action = [GET: "order"]
        }
        "/wepay/callback"(controller: "wxpay", parseRequest: true) {
            action = [GET: "echo", POST: "callback"]
        }
        "/wepay/result"(controller: "wxpay", parseRequest: true) {
            action = [GET: "echo", POST: "result"]
        }
    }
}
