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

import common.pay.sdk.CommonPayConfig;

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
                TestPay.testWxPayInFragment(this,TEST_REQUEST_PAY_CODE);
                break;
            case R.id.btn_zfb_pay:
                TestPay.testZfbPayInFragment(this, TEST_REQUEST_PAY_CODE);
                break;
        }
    }
}
