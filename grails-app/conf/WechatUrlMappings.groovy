class WechatUrlMappings {

    static mappings = {
        "/wechat"(controller: "wechat", parseRequest: true) {
            action = [GET: "check", POST: "post"]
        }
    }
}
