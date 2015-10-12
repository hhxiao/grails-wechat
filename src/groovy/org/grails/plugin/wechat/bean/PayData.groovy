package org.grails.plugin.wechat.bean
import org.grails.plugin.wechat.util.SignatureHelper
/**
 * Created by haihxiao on 8/1/2015.
 */
enum PayDataKey {
    appid,
    attach,
    bank_type,
    bill_date,
    bill_type,
    cash_fee,
    cash_fee_type,
    coupon_fee,
    coupon_count,
    body,
    detail,
    device_info,
    fee_type,
    goods_tag,
    limit_pay,
    mch_id,
    nonce_str,
    notify_url,
    openid,
    op_user_id,
    out_trade_no,
    out_refund_no,
    product_id,
    spbill_create_ip,
    total_fee,
    trade_state,
    trade_state_desc,
    trade_type,
    time_end,
    time_expire,
    time_start,
    sign,
    transaction_id,
    refund_id,
    refund_fee,
    result_code,
    return_code,
    return_msg,
    err_code,
    err_code_des,
    prepay_id,
    code_url
}

enum TradeType {
    NATIVE,
    JSAPI,
    WAP,
    APP
}

class PayData extends HashMap<String, Object> {
    private String sign

    PayData() {
    }

    PayData(String tradeNo, int totalFee, String body) {
        put(PayDataKey.out_trade_no, tradeNo)
        put(PayDataKey.total_fee, totalFee)
        put(PayDataKey.body, body)
    }

    void put(PayDataKey key, Object val) {
        super.put(key.name(), val)
    }

    void putIfAbsent(PayDataKey key, Object val) {
        if(!containsKey(key.name())) {
            super.put(key.name(), val)
        }
    }

    static PayData fromXml(String text) {
        PayData ret = new PayData()
        new XmlSlurper().parseText(text).children().each {
            ret.put(it.name(), it.text().trim())
        }
        ret.sign = ret['sign']
        ret
    }

    void sign(String paymentKey) {
        sign = SignatureHelper.sign(this, [key: paymentKey]).toUpperCase()
    }

    void validate() {
        checkData(PayDataKey.out_trade_no)
        checkData(PayDataKey.body)
        checkData(PayDataKey.total_fee)
        checkData(PayDataKey.trade_type)
        checkData(PayDataKey.notify_url)

        switch (get(PayDataKey.trade_type.name())) {
            case TradeType.NATIVE.name():
                checkData(PayDataKey.product_id)
                break
            case TradeType.JSAPI.name():
                checkData(PayDataKey.openid)
                break
        }
    }

    String toXml() {
        """<xml>
${this.sort().entrySet().findAll{it.value}.collect {"  <${it.key}>${it.value instanceof String ? "<![CDATA[${it.value}]]>" : it.value}</${it.key}>"}.join("\n")}
  <sign>$sign</sign>
</xml>
"""
    }

    private void checkData(PayDataKey key) {
        if(!containsKey(key.name())) throw new IllegalArgumentException("$key is required")
    }
}
