package com.common.wxalipaydemo;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.common.wxalipaydemo.wxapi.WXPayEntryActivity;
import common.pay.sdk.PayEntryActivity;
import common.pay.sdk.PrePayOrderInfo;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-01-12
 * Time: 13:55
 * DESC:
 */
public class TestPay {
    private static String testAliOrderInfo = "partner=\"2088121771207747\"&seller_id=\"cloudtone@163.com\"&out_trade_no=\"201611011806231255910\"&subject=\"流量充值\"&body=\"人民币与云币兑换比例为1:100\"&total_fee=\"0.01\"&notify_url=\"http://112.95.168.216:41002/skysimApp/aliPayNotifyV2\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"7d\"&return_url=\"m.alipay.com\"&sign=\"M3K6Yea6e6%2B6pBgn%2BfEmeduuKtUtjPJURRqf%2BZ9LN%2FuqGN2ZVgQho1fNgGHE0Egn8hcPaTkGfyYjW2oKHcKsWihN5ZOMndwNlXZkMkZpGxXz7X5E2gA3TX7JHw1E8au23MCLGL8OCGxKJJ6EMtHwuqd1ciGWHcfrPWmACtfhGgg%3D\"&sign_type=\"RSA\"";

    /**
     * 测试用的服务器返回的微信支付订单信息Json类型数据
     * 这里需要将该Josn数据转换成PrePayOrderInfo对象
     */
    private static String testWxServerRespData = "{\"appid\":\"wxb4bbf0651d312ab6\",\"partnerid\":\"1372735502\",\"prepayid\":\"wx20161101180444057d3200540481820870\",\"noncestr\":\"6xzactdvq9ct817m7dnoq8u1w3snnr3x\",\"timestamp\":\"1477994684\",\"package\":\"Sign=WXPay\",\"paySign\":\"A8915C273F63CBACA54C360E283EEDC0\",\"orderNo\":\"201611011804441132840\"}";

    public static void testWxPay(Activity curActivity,int curRequestCode) {
        CustomOrderInfo wxOrderInfo = new CustomOrderInfo();
        wxOrderInfo.parseDataFromJsonStr(testWxServerRespData);
        PayEntryActivity.startPayActivity(curActivity, wxOrderInfo, curRequestCode, WXPayEntryActivity.class);
    }

    public static void testWxPayInFragment(Fragment fragment, int curRequestCode) {
        CustomOrderInfo wxOrderInfo = new CustomOrderInfo();
        wxOrderInfo.parseDataFromJsonStr(testWxServerRespData);
        PayEntryActivity.startPayActivity(fragment, wxOrderInfo, curRequestCode, WXPayEntryActivity.class);
    }
    public static void testZfbPay(Activity curActivity, int curRequestCode) {
        PrePayOrderInfo alipayOrderInfo = new PrePayOrderInfo();
        alipayOrderInfo.setAlipayInfo(testAliOrderInfo);
        PayEntryActivity.startPayActivity(curActivity, alipayOrderInfo, curRequestCode, WXPayEntryActivity.class);
    }
    public static void testZfbPayInFragment(Fragment fragment, int curRequestCode) {
        PrePayOrderInfo alipayOrderInfo = new PrePayOrderInfo();
        alipayOrderInfo.setAlipayInfo(testAliOrderInfo);
        PayEntryActivity.startPayActivity(fragment, alipayOrderInfo, curRequestCode, WXPayEntryActivity.class);
    }
}
