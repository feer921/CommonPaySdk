package com.common.wxalipaydemo;



import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import common.pay.sdk.ICanPayOrderInfo;
import common.pay.sdk.utils.Util;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-11-01
 * Time: 19:24
 * DESC: 因为CommonPayLib中的PrePayOrderInfo对象，在我们公司的服务器端响应下 一些属性不匹配，所以自定义一个
 * 只要实现ICanPayOrderInfo接口就行
 */
public class CustomOrderInfo implements ICanPayOrderInfo {
    private String appid;
    private String partnerid;
    private String prepayid;
    private String appPackage;
    private String timeStamp;
    private String nonceStr;
    //例如此属性/字段，我们公司的服务器返回的订单信息Json数据中该字段名就是和PrePayOrderInfo中不一致
    private String wxPayOrderSign;


    private String alipayInfo;
    @Override
    public PayReq convert2WxPayReq() {
        PayReq wxPayReq = new PayReq();
        wxPayReq.appId = this.appid;
        wxPayReq.partnerId = this.partnerid;
        wxPayReq.prepayId = this.prepayid;
        wxPayReq.packageValue = this.appPackage;
        wxPayReq.timeStamp = this.timeStamp;
        wxPayReq.nonceStr = this.nonceStr;
        wxPayReq.sign = this.wxPayOrderSign;
        return wxPayReq;
    }

    @Override
    public String getAlipayInfo() {
        return alipayInfo;
    }

    @Override
    public boolean canPayThisOrder() {
        return isAliPayOrder() || isWxPayOrder();
    }

    @Override
    public boolean isAliPayOrder() {
        return !Util.isEmpty(alipayInfo);
    }

    @Override
    public boolean isWxPayOrder() {
        return !Util.isEmpty(prepayid);
    }

    /**
     *  "appid": "wxb4bbf0651d312ab6",
        "partnerid": "1372735502",
        "prepayid": "wx20161101180444057d3200540481820870",
        "noncestr": "6xzactdvq9ct817m7dnoq8u1w3snnr3x",
        "timestamp": "1477994684",
        "package": "Sign=WXPay",
        "paySign": "A8915C273F63CBACA54C360E283EEDC0",
        "orderNo": "201611011804441132840"
     * @param jsonDataStr
     */
    public void parseDataFromJsonStr(String jsonDataStr) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonDataStr);
            this.appid = jsonObj.optString("appid");
            this.partnerid = jsonObj.optString("partnerid");

            this.prepayid = jsonObj.optString("prepayid");

            this.nonceStr = jsonObj.optString("noncestr");

            this.timeStamp = jsonObj.optString("timestamp");

            this.appPackage = jsonObj.optString("package");

            this.wxPayOrderSign = jsonObj.optString("paySign");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
