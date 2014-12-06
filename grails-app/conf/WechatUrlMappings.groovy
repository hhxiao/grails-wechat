class WechatUrlMappings {

    static mappings = {
        "/wechat"(controller: "wechat", parseRequest: true) {
            action = [GET: "echo", POST: "post"]
        }
    }
}
