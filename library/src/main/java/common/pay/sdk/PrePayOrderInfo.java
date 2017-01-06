package common.pay.sdk;

import com.tencent.mm.sdk.modelpay.PayReq;

import common.pay.sdk.utils.Util;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-09-26
 * Time: 19:23
 * DESC: 微信、支付宝的预付订单信息,如果各自的APP有不同的字段，则可继承重写一些方法
 * 本实体Bean供APP各自依据服务端的响应解析而成，比如：使用GoSon、JackSon等协议解析成本预支付订单信息实体
 */
public class PrePayOrderInfo implements ICanPayOrderInfo{
    /**
     * 微信原本需要的支付订单信息
     * PayReq request = new PayReq();

     request.appId = "wxd930ea5d5a258f4f";

     request.partnerId = "1900000109";

     request.prepayId= "1101000000140415649af9fc314aa427",;

     request.packageValue = "Sign=WXPay";

     request.nonceStr= "1101000000140429eb40476f8896f4c9";

     request.timeStamp= "1398746574";

     request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";

     */
    /**
     * 即为开通微信支付的APP在微信开放平台所属的APPID,
     * 微信这货需要APPID的验证
     */
    protected String appid;
    protected String partnerid;
    protected String prepayid;
    /**
     * 即为开通微信支付的APP在微信开放平台所填写的对应的包名，
     * 微信这货需要包名的验证
     */
    protected String appPackage;
    /**
     * 订单的时间戳
     */
    protected String timestamp;

    protected String noncestr;
    /**
     * 支付订单的签名，一般为各家APP的服务器生成传下来的
     */
    protected String paySign;
    /**
     * 转换成微信支付请求对象
     * 该方法为关键
     * 如果使用者的APP的服务器返回的微信、支付宝订单信息不满足本对象的属性(例如属性名字不一致)
     * 如果继承的话，需要重写此方法
     * @return 微信支付的请求信息对象
     */
    public PayReq convert2WxPayReq() {
        PayReq wxPayReq = new PayReq();
        wxPayReq.appId = this.appid;
        wxPayReq.partnerId = this.partnerid;
        wxPayReq.prepayId = this.prepayid;
        wxPayReq.packageValue = this.appPackage;
        wxPayReq.timeStamp = this.timestamp;
        wxPayReq.nonceStr = this.noncestr;
        wxPayReq.sign = this.paySign;
        return wxPayReq;
    }

    /**
     * 支付宝的订单信息
     */
    protected String alipayInfo;

    /**
     * 该方法为关键
     * 如果使用者的APP的服务器返回的微信、支付宝订单信息不满足本对象的属性(例如属性名字不一致)
     * 如果继承的话，需要重写此方法
     * 以返回支付宝的订单信息
     * @return
     */
    public String getAlipayInfo() {
        return alipayInfo;
    }

    public void setAlipayInfo(String alipayInfo) {
        this.alipayInfo = alipayInfo;
    }

    /**
     * 是否能支付此次订单
     * 依据为有支付宝的订单信息或者有微信的订单信息，则可支付
     * @return
     */
    public boolean canPayThisOrder() {
        return isAliPayOrder() || isWxPayOrder();
    }

    /**
     * 该方法为关键
     * 如果使用者的APP的服务器返回的微信、支付宝订单信息不满足本对象的属性(例如属性名字不一致)
     * 如果继承的话，需要重写此方法
     * @return
     */
    public boolean isAliPayOrder() {
        return !Util.isEmpty(alipayInfo);
    }

    /**
     * 该方法为关键
     * 如果使用者的APP的服务器返回的微信、支付宝订单信息不满足本对象的属性(例如属性名字不一致)
     * 如果继承的话，需要重写此方法
     * @return
     */
    public boolean isWxPayOrder() {
        return !Util.isEmpty(prepayid);
    }
}
