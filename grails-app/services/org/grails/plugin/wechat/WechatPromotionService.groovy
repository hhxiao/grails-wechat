package org.grails.plugin.wechat

import org.grails.plugin.wechat.annotation.VerificationRequired
import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.bean.QrCode
import org.grails.plugin.wechat.bean.QrScene
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
/**
 * Created by haihxiao on 2014/12/5.
 */
class WechatPromotionService {
    private static final String PROMOTION_URL = 'https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token='
    private static final String TICKET_URL = 'https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket='
    public static final Random random = new Random()

    def wechatTokenService

    @VerificationRequired
    QrCode getQrCode(QrScene scene, int expire_seconds = 60) {
        AccessToken token = wechatTokenService.accessToken
        String url = PROMOTION_URL + token.accessToken
        int sceneId
        if(scene == QrScene.QR_SCENE) {
            sceneId = random.nextInt(Integer.MAX_VALUE - 1) + 1
        } else {
            sceneId = random.nextInt(100000 - 1) + 1
        }
        def req = [action_name: scene.name(), action_info: [scene: [scene_id: sceneId]]]
        if(scene == QrScene.QR_SCENE) {
            req.put('expire_seconds', expire_seconds)
        }
        String ret = HttpUtils.post(url, 'application/json', JsonHelper.toJson(req))
        return JsonHelper.parseJson(ret, QrCode.class)
    }

    String getQrCodeUrl(String ticket) {
        TICKET_URL + URLEncoder.encode(ticket, 'UTF-8')
    }
}
