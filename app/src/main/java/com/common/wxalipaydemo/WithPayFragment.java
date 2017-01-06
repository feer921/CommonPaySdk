package com.common.wxalipaydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.wxalipaydemo.wxapi.WXPayEntryActivity;

import common.pay.sdk.CommonPayConfig;
import common.pay.sdk.PayEntryActivity;
import common.pay.sdk.PrePayOrderInfo;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-01-06
 * Time: 19:05
 * DESC:
 */
public class WithPayFragment extends Fragment implements View.OnClickListener{
    TextView tvPayResult;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pay_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_wx_pay).setOnClickListener(this);
        view.findViewById(R.id.btn_zfb_pay).setOnClickListener(this);
        tvPayResult = (TextView) view.findViewById(R.id.tv_show_pay_result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("info","WithPayFragment----> onActivityResult() requestCode = " + requestCode + " resultCode = " + resultCode
        );

        switch (requestCode) {
            case TEST_REQUEST_PAY_CODE:
                String toastHint = "支付模式:%s,响应码:%s,结果描述:%s";
                String payModeDesc = "未知";
                String payRespCode = "unKnow";
                if (data != null) {
                    int payMode = data.getIntExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_MODE, CommonPayConfig.PAY_MODE_WX);
                    payModeDesc = payMode == CommonPayConfig.PAY_MODE_ALIPAY ? "[支付宝]" : "[微信]";
                    payRespCode = data.getStringExtra(CommonPayConfig.INTENT_KEY_REAL_PAY_RESULT_STATUS_CODE);
                }
                String resultDesc = "支付失败";
                switch (resultCode) {
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_OK:
                        resultDesc = "支付成功";
                        break;
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_CANCEL:
                        resultDesc = "支付被取消了";
                        break;
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_NO_WX:
                        resultDesc = "支付失败,未安装微信APP";
                        break;
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_ERROR:
                        resultDesc = "支付失败";
                        break;
                }
                String payResultInfo = "支付模式:" + payModeDesc + "\n" +
                        "支付SDK的实际响应码：" + payRespCode + "\n" +
                        "结果描述：" + resultDesc;
                tvPayResult.setText(payResultInfo);
                break;
        }
    }
    private static String testAliOrderInfo = "partner=\"2088121771207747\"&seller_id=\"cloudtone@163.com\"&out_trade_no=\"201611011806231255910\"&subject=\"流量充值\"&body=\"人民币与云币兑换比例为1:100\"&total_fee=\"0.01\"&notify_url=\"http://112.95.168.216:41002/skysimApp/aliPayNotifyV2\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"7d\"&return_url=\"m.alipay.com\"&sign=\"M3K6Yea6e6%2B6pBgn%2BfEmeduuKtUtjPJURRqf%2BZ9LN%2FuqGN2ZVgQho1fNgGHE0Egn8hcPaTkGfyYjW2oKHcKsWihN5ZOMndwNlXZkMkZpGxXz7X5E2gA3TX7JHw1E8au23MCLGL8OCGxKJJ6EMtHwuqd1ciGWHcfrPWmACtfhGgg%3D\"&sign_type=\"RSA\"";

    /**
     * 测试用的服务器返回的微信支付订单信息Json类型数据
     * 这里需要将该Josn数据转换成PrePayOrderInfo对象
     */
    private static String testWxServerRespData = "{\"appid\":\"wxb4bbf0651d312ab6\",\"partnerid\":\"1372735502\",\"prepayid\":\"wx20161101180444057d3200540481820870\",\"noncestr\":\"6xzactdvq9ct817m7dnoq8u1w3snnr3x\",\"timestamp\":\"1477994684\",\"package\":\"Sign=WXPay\",\"paySign\":\"A8915C273F63CBACA54C360E283EEDC0\",\"orderNo\":\"201611011804441132840\"}";

    private static final int TEST_REQUEST_PAY_CODE = 180;
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_wx_pay:
                CustomOrderInfo wxOrderInfo = new CustomOrderInfo();
                wxOrderInfo.parseDataFromJsonStr(testWxServerRespData);
                PayEntryActivity.startPayActivity(this, wxOrderInfo, TEST_REQUEST_PAY_CODE, WXPayEntryActivity.class);
                break;
            case R.id.btn_zfb_pay:
                PrePayOrderInfo alipayOrderInfo = new PrePayOrderInfo();
                alipayOrderInfo.setAlipayInfo(testAliOrderInfo);
                PayEntryActivity.startPayActivity(this, alipayOrderInfo, TEST_REQUEST_PAY_CODE, WXPayEntryActivity.class);
                break;
        }
    }
}
